package com.example.scheduler.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.scheduler.data.model.Event
import com.example.scheduler.data.model.User

/**
 * The primary database entry point for the application.
 * Built with Room to provide a robust, persistent SQLite shell for all app data.
 */
@Database(entities = [Event::class, User::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_timestamp ON events(timestamp)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_parentCollectionId ON events(parentCollectionId)")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create a legacy owner to satisfy Foreign Key constraints for existing data
                db.execSQL("INSERT OR IGNORE INTO users (username, password, firstName, lastName, email) " +
                        "VALUES ('legacy_user', '', 'Legacy', 'User', '')")

                // 2. Create the new events table with the full schema including Foreign Key
                db.execSQL("""
                    CREATE TABLE events_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        userId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        date TEXT NOT NULL,
                        time TEXT NOT NULL,
                        iconName TEXT NOT NULL,
                        location TEXT,
                        notes TEXT,
                        reminder TEXT,
                        isCollection INTEGER NOT NULL,
                        dateRange TEXT,
                        eventCount INTEGER,
                        timestamp INTEGER,
                        durationMinutes INTEGER NOT NULL,
                        parentCollectionId TEXT,
                        FOREIGN KEY(userId) REFERENCES users(username) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())

                // 3. Copy existing data from old table to new table, assigning to legacy_user
                db.execSQL("""
                    INSERT INTO events_new (
                        id, userId, title, date, time, iconName, location, notes, reminder, 
                        isCollection, dateRange, eventCount, timestamp, durationMinutes, parentCollectionId
                    )
                    SELECT 
                        id, 'legacy_user', title, date, time, iconName, location, notes, reminder, 
                        isCollection, dateRange, eventCount, timestamp, durationMinutes, parentCollectionId 
                    FROM events
                """.trimIndent())

                // 4. Drop the old table and rename the new one
                db.execSQL("DROP TABLE events")
                db.execSQL("ALTER TABLE events_new RENAME TO events")

                // 5. Recreate required indexes
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_timestamp ON events(timestamp)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_parentCollectionId ON events(parentCollectionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_userId ON events(userId)")
            }
        }

        /**
         * Returns the singleton database instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scheduler_database"
                )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
