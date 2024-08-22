package com.example.transactions.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Convert all money from Double values in dollars to Int values in cents
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        with(db) {
            // prepare all double values into cents instead of dollars so they can be typecast properly
            execSQL("""UPDATE transactions
                |SET amount = amount * 100;
            """.trimMargin())
            execSQL("""UPDATE recurrings
                |SET amount = amount * 100;
            """.trimMargin())

            // convert the transactions table to use an integer instead of a double for the amount column
            execSQL("""CREATE TABLE transactions_backup(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, type INTEGER NOT NULL, category INTEGER NOT NULL, amount REAL NOT NULL, reason TEXT NOT NULL, timestamp INTEGER NOT NULL)""")
            execSQL("""INSERT INTO transactions_backup SELECT * from transactions""")
            execSQL("""DROP TABLE transactions""")
            execSQL("""CREATE TABLE transactions(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, type INTEGER NOT NULL, category INTEGER NOT NULL, amount INTEGER NOT NULL, reason TEXT NOT NULL, timestamp INTEGER NOT NULL)""")
            execSQL("""INSERT INTO transactions SELECT * from transactions_backup""")
            execSQL("""DROP TABLE transactions_backup""")

            // convert the recurrings table to use an integer instead of a double for the amount column
            execSQL("""CREATE TABLE recurrings_backup(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` REAL NOT NULL, `title` TEXT NOT NULL, `details` TEXT, `active` INTEGER NOT NULL, `type` INTEGER NOT NULL, `period` INTEGER NOT NULL, `firstCharge` INTEGER NOT NULL, `lastCharge` INTEGER NOT NULL, `nextCharge` INTEGER NOT NULL DEFAULT 0)""")
            execSQL("""INSERT INTO recurrings_backup SELECT * from recurrings""")
            execSQL("""DROP TABLE recurrings""")
            execSQL("""CREATE TABLE recurrings(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` INTEGER NOT NULL, `title` TEXT NOT NULL, `details` TEXT, `active` INTEGER NOT NULL, `type` INTEGER NOT NULL, `period` INTEGER NOT NULL, `firstCharge` INTEGER NOT NULL, `lastCharge` INTEGER NOT NULL, `nextCharge` INTEGER NOT NULL DEFAULT 0)""")
            execSQL("""INSERT INTO recurrings SELECT * from recurrings_backup""")
            execSQL("""DROP TABLE recurrings_backup""")
        }
    }
}

@Database(
    entities = [Transaction::class, Recurring::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
    ]
)
abstract class TransactionsDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun recurringDao(): RecurringDao

    companion object {
        @Volatile
        private var Instance: TransactionsDatabase? = null

        fun getDatabase(context: Context): TransactionsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    TransactionsDatabase::class.java, "transactions_database"
                )
                    .addMigrations(
                        MIGRATION_3_4
                    )
                    .build()
                    .also { Instance = it}
            }
        }
    }
}

/*

    TransactionsDatabase versions:
        1: Recurring empty, initial Transaction entities.
        2: Added Recurring fields
        3: Added 'nextCharge' field to Recurring

 */