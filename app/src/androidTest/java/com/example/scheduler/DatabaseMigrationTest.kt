package com.example.scheduler

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scheduler.data.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrate6To7() {
        // 1. Create the database with version 6
        var db = helper.createDatabase(TEST_DB, 6)

        // 2. Insert test data into the V6 schema
        db.execSQL("INSERT INTO events (id, title, date, time, iconName, isCollection, durationMinutes) " +
                "VALUES ('event1', 'Legacy Event', 'Today', '12:00 PM', 'Cake', 0, 60)")

        // 3. Prepare for migration
        db.close()

        // 4. Run migration to Version 7
        db = helper.runMigrationsAndValidate(TEST_DB, 7, true, AppDatabase.MIGRATION_6_7)

        // 5. Verify the results
        
        // Verify legacy_user was created
        val userCursor = db.query("SELECT * FROM users WHERE username = 'legacy_user'")
        assertEquals("Should have 1 legacy user", 1, userCursor.count)
        userCursor.close()

        // Verify event1 survived and is owned by legacy_user
        val eventCursor = db.query("SELECT * FROM events WHERE id = 'event1'")
        assertEquals("Should have 1 event", 1, eventCursor.count)
        eventCursor.moveToFirst()
        
        val userIdIndex = eventCursor.getColumnIndex("userId")
        assertEquals("Event should be owned by legacy_user", "legacy_user", eventCursor.getString(userIdIndex))
        eventCursor.close()
        
        // Verify referential integrity (foreign key)
        // Trying to insert an event with a non-existent userId should fail if FKs are enabled.
        // Room enables FKs by default in the new table definition we used in the migration.
        
        // Let's check indexes exist
        val indexCursor = db.query("PRAGMA index_list('events')")
        val indexNames = mutableListOf<String>()
        while (indexCursor.moveToNext()) {
            indexNames.add(indexCursor.getString(indexCursor.getColumnIndex("name")))
        }
        indexCursor.close()
        
        assert(indexNames.contains("index_events_timestamp"))
        assert(indexNames.contains("index_events_parentCollectionId"))
        assert(indexNames.contains("index_events_userId"))
    }

    @Test
    @Throws(IOException::class)
    fun migrate7To8() {
        // 1. Create the database with version 7
        var db = helper.createDatabase(TEST_DB, 7)

        // 2. Insert test user and collection/child event in V7
        db.execSQL("INSERT INTO users (username, password, firstName, lastName, email) " +
                "VALUES ('user1', 'pass123', 'Alice', 'Smith', 'alice@example.com')")

        db.execSQL("INSERT INTO events (id, userId, title, date, time, iconName, isCollection, durationMinutes) " +
                "VALUES ('col1', 'user1', 'Japan Trip', 'Oct 20', '10:00 AM', 'Folder', 1, 60)")

        db.execSQL("INSERT INTO events (id, userId, title, date, time, iconName, isCollection, durationMinutes, parentCollectionId) " +
                "VALUES ('child1', 'user1', 'Flight to Tokyo', 'Oct 20', '10:30 AM', 'Flight', 0, 720, 'col1')")

        // 3. Prepare for migration
        db.close()

        // 4. Run migration to Version 8
        db = helper.runMigrationsAndValidate(TEST_DB, 8, true, AppDatabase.MIGRATION_7_8)

        // 5. Verify child event survived and preserves collection reference
        val childCursor = db.query("SELECT * FROM events WHERE id = 'child1'")
        assertEquals("Child event should survive migration", 1, childCursor.count)
        childCursor.moveToFirst()

        val parentColIndex = childCursor.getColumnIndex("parentCollectionId")
        assertEquals("Collection-to-event relationship should be preserved", "col1", childCursor.getString(parentColIndex))
        childCursor.close()

        // 6. Verify foreign key list on events table contains both userId and parentCollectionId FKs
        val fkCursor = db.query("PRAGMA foreign_key_list('events')")
        val fkTables = mutableListOf<String>()
        while (fkCursor.moveToNext()) {
            fkTables.add(fkCursor.getString(fkCursor.getColumnIndex("table")))
        }
        fkCursor.close()

        assertTrue("User foreign key should exist", fkTables.contains("users"))
        assert(fkTables.contains("events"))

        // 7. Verify required indexes exist
        val indexCursor = db.query("PRAGMA index_list('events')")
        val indexNames = mutableListOf<String>()
        while (indexCursor.moveToNext()) {
            indexNames.add(indexCursor.getString(indexCursor.getColumnIndex("name")))
        }
        indexCursor.close()

        assert(indexNames.contains("index_events_timestamp"))
        assert(indexNames.contains("index_events_parentCollectionId"))
        assert(indexNames.contains("index_events_userId"))
    }
}
