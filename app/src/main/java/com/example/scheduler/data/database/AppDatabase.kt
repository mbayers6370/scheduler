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
                // Add userId column with a placeholder value for existing records
                db.execSQL("ALTER TABLE events ADD COLUMN userId TEXT NOT NULL DEFAULT 'legacy_user'")
                // Create index on userId
                db.execSQL("CREATE INDEX IF NOT EXISTS index_events_userId ON events(userId)")
                
                // Note: SQLite doesn't easily support adding Foreign Keys to existing tables via ALTER.
                // For a production app, we would create a new table and migrate data.
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
