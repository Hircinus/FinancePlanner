package com.example.finance.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class TransactionManager(context: Context) {
    private val db: SQLiteDatabase

    init {
        val helper = CustomSQLiteOpenHelper(context)
        db = helper.writableDatabase
        if(!isTableExists("transactions"))
            helper.onCreate(db)
    }

    fun isTableExists(tableName: String): Boolean {
        val query =
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'"
        db.rawQuery(query, null).use { cursor ->
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true
                }
            }
            return false
        }
    }

    companion object {
        const val TABLE_ROW_ID = "_id"
        const val TABLE_ROW_AMOUNT = "amount"
        const val TABLE_ROW_DATE = "date"
        const val TABLE_ROW_BUDGET_ID = "budget_id"

        private const val DB_NAME = "finance_db"
        private const val DB_VERSION = 1
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val TABLE_BUDGET = "budget"
    }

    fun insert(budgetId: Int, amount: Double) {
        val query =
            "INSERT INTO $TABLE_TRANSACTIONS ($TABLE_ROW_AMOUNT, $TABLE_ROW_BUDGET_ID) VALUES ('$amount', '$budgetId');"
        Log.i("Insert", query)
        db.execSQL(query)
    }

    fun selectAll(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_TRANSACTIONS", null)
    }

    fun getAllByBudgetId(budgetId: Int): Cursor {
        return db.rawQuery("SELECT * from $TABLE_TRANSACTIONS WHERE $TABLE_ROW_BUDGET_ID='$budgetId';", null)
    }

    fun getLast(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_TRANSACTIONS ORDER BY $TABLE_ROW_ID DESC LIMIT 1;", null)
    }

//    fun searchStartDate(startDate: Long): Cursor {
//        val query =
//            "SELECT $TABLE_ROW_ID, $TABLE_ROW_AMOUNT, $TABLE_ROW_DATE from $TABLE_TRANSACTIONS WHERE $TABLE_ROW_AMOUNT = '$startDate';"
//        Log.i("Name search", query)
//        return db.rawQuery(query, null)
//    }

    fun searchID(id: Int): Cursor {
        val query =
            "SELECT $TABLE_ROW_ID, $TABLE_ROW_AMOUNT, $TABLE_ROW_DATE, $TABLE_ROW_BUDGET_ID from $TABLE_TRANSACTIONS WHERE $TABLE_ROW_ID = '$id';"
        Log.i("ID search", query)
        return db.rawQuery(query, null)
    }
    private inner class CustomSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val newTableQuery = ("create table $TABLE_TRANSACTIONS ($TABLE_ROW_ID integer primary key autoincrement not null, "
                    + "$TABLE_ROW_AMOUNT double not null, $TABLE_ROW_DATE int not null);")
            Log.i("Table created", newTableQuery)
            val otherTableQuery = ("ALTER TABLE $TABLE_TRANSACTIONS ADD FOREIGN KEY ($TABLE_ROW_BUDGET_ID) REFERENCES $TABLE_BUDGET(_id);")
            db.execSQL(newTableQuery)
            db.execSQL(otherTableQuery)
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }
    }
}