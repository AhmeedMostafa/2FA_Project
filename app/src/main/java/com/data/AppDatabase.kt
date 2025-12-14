package com.example.a2faproject.data

import androidx.room.Database
import androidx.room.RoomDatabase
<<<<<<< Updated upstream
import com.example.authenticator.model.Token

@Database(entities = [Token::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
=======
import androidx.room.migration.Migration
import androidx.room.Room
import com.model.Token
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Token::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add stable remoteId for cloud sync.
                db.execSQL("ALTER TABLE tokens ADD COLUMN remoteId TEXT NOT NULL DEFAULT ''")
                // Backfill remoteId for existing rows (use existing unique 'id' values).
                db.execSQL("UPDATE tokens SET remoteId = CAST(id AS TEXT) WHERE remoteId = ''")
                // Unique index to prevent duplicates across sync.
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tokens_remoteId ON tokens(remoteId)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "authenticator_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
>>>>>>> Stashed changes
}