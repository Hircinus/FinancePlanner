package com.example.finance

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataManager(context: Context) {
    private val db: SQLiteDatabase

    init {
        val helper = CustomSQLiteOpenHelper(context)
        db = helper.writableDatabase
        if(!isTableExists("budget"))
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
        const val TABLE_ROW_NAME = "name"
        const val TABLE_ROW_CATEGORY = "category"
        const val TABLE_ROW_TOTAL_MONTHLY_AMOUNT = "totalMonthlyAmount"
        const val TABLE_ROW_CURRENT_BALANCE = "currentBalance"
        const val TABLE_ROW_MONTH_ID = "month_id"

        private const val DB_NAME = "finance_db"
        private const val DB_VERSION = 1
        private const val TABLE_BUDGET = "budget"
        private const val TABLE_MONTH = "month"
    }


    fun insert(name: String, monthlyAmount: Double, category: String, month_id: Int) {
        val query =
            "INSERT INTO $TABLE_BUDGET ($TABLE_ROW_NAME, $TABLE_ROW_CATEGORY, $TABLE_ROW_TOTAL_MONTHLY_AMOUNT, $TABLE_ROW_CURRENT_BALANCE, $TABLE_ROW_MONTH_ID) VALUES ('$name', '$category', '$monthlyAmount', '0', '$month_id');"
        Log.i("Insert", query)
        db.execSQL(query)
    }

    fun delete(name: String) {
        val query = "DELETE FROM $TABLE_BUDGET WHERE $TABLE_ROW_NAME = '$name';"
        Log.i("Delete", query)
        db.execSQL(query)
    }
    fun delete(id: Int) {
        val query = "DELETE FROM $TABLE_BUDGET WHERE $TABLE_ROW_ID = '$id';"
        Log.i("Delete", query)
        db.execSQL(query)
    }

    fun updateBalance(id: Int, balance: Double) {
        val query =
            "UPDATE $TABLE_BUDGET SET $TABLE_ROW_CURRENT_BALANCE = '$balance' WHERE $TABLE_ROW_ID = '$id';"
        Log.i("Update balance on $id", query)
        db.execSQL(query)
    }

    fun updateBudget(id: Int, budget: Double) {
        val query =
            "UPDATE $TABLE_BUDGET SET $TABLE_ROW_TOTAL_MONTHLY_AMOUNT = '$budget' WHERE $TABLE_ROW_ID = '$id';"
        Log.i("Update budget on $id", query)
        db.execSQL(query)
    }

    fun updateCategory(id: Int, category: String) {
        val query =
            "UPDATE $TABLE_BUDGET SET $TABLE_ROW_CATEGORY = '$category' WHERE $TABLE_ROW_ID = '$id';"
        Log.i("Update balance on $id", query)
        db.execSQL(query)
    }

    fun selectAll(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_BUDGET", null)
    }
    fun selectAllByCategory(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_BUDGET ORDER BY $TABLE_ROW_CATEGORY ASC", null)
    }

    fun getLast(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_BUDGET ORDER BY $TABLE_ROW_MONTH_ID DESC LIMIT 1;", null)
    }

    fun searchName(name: String): Cursor {
        val query =
            "SELECT $TABLE_ROW_ID, $TABLE_ROW_NAME, $TABLE_ROW_CATEGORY, $TABLE_ROW_TOTAL_MONTHLY_AMOUNT, $TABLE_ROW_CURRENT_BALANCE, $TABLE_ROW_MONTH_ID from $TABLE_BUDGET WHERE $TABLE_ROW_NAME = '$name';"
        Log.i("Name search", query)
        return db.rawQuery(query, null)
    }

    fun searchID(id: Int): Cursor {
        val query =
            "SELECT $TABLE_ROW_ID, $TABLE_ROW_NAME, $TABLE_ROW_CATEGORY, $TABLE_ROW_TOTAL_MONTHLY_AMOUNT, $TABLE_ROW_CURRENT_BALANCE, $TABLE_ROW_MONTH_ID from $TABLE_BUDGET WHERE $TABLE_ROW_ID = '$id';"
        Log.i("ID search", query)
        return db.rawQuery(query, null)
    }
    private inner class CustomSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val newTableQuery = ("create table $TABLE_BUDGET ($TABLE_ROW_ID integer primary key autoincrement not null, "
                    + "$TABLE_ROW_NAME text not null, $TABLE_ROW_CATEGORY text not null, $TABLE_ROW_TOTAL_MONTHLY_AMOUNT double not null, "
                    + "$TABLE_ROW_CURRENT_BALANCE double not null, $TABLE_ROW_MONTH_ID integer, "
                    + "FOREIGN KEY ($TABLE_ROW_MONTH_ID) REFERENCES $TABLE_MONTH('_id'));")
            Log.i("Table created", newTableQuery)
            db.execSQL(newTableQuery)
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }
    }
}