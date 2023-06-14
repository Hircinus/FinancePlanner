package com.example.finance

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MonthManager(context: Context) {
    private val db: SQLiteDatabase

    init {
        val helper = CustomSQLiteOpenHelper(context)
        db = helper.writableDatabase
        if(!isTableExists("month"))
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
        const val TABLE_ROW_START_DATE = "start_date"
        const val TABLE_ROW_END_DATE = "end_date"

        private const val DB_NAME = "finance_db"
        private const val DB_VERSION = 1
        private const val TABLE_MONTH = "month"
    }

    fun insert(startDate: Long, endDate: Long) {
        val query =
            "INSERT INTO $TABLE_MONTH ($TABLE_ROW_START_DATE, $TABLE_ROW_END_DATE) VALUES ('$startDate', '$endDate');"
        Log.i("Insert", query)
        db.execSQL(query)
    }

    fun selectAll(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_MONTH", null)
    }

    fun getLast(): Cursor {
        return db.rawQuery("SELECT * from $TABLE_MONTH ORDER BY $TABLE_ROW_ID DESC LIMIT 1;", null)
    }

    fun searchStartDate(startDate: Long): Cursor {
        val query =
            "SELECT $TABLE_ROW_ID, $TABLE_ROW_START_DATE, $TABLE_ROW_END_DATE from $TABLE_MONTH WHERE $TABLE_ROW_START_DATE = '$startDate';"
        Log.i("Name search", query)
        return db.rawQuery(query, null)
    }

    fun searchID(id: Int): Cursor {
        val query =
            "SELECT $TABLE_ROW_ID, $TABLE_ROW_START_DATE, $TABLE_ROW_END_DATE from $TABLE_MONTH WHERE $TABLE_ROW_ID = '$id';"
        Log.i("ID search", query)
        return db.rawQuery(query, null)
    }
    private inner class CustomSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            val newTableQuery = ("create table $TABLE_MONTH ($TABLE_ROW_ID integer primary key autoincrement not null, "
                    + "$TABLE_ROW_START_DATE int not null, $TABLE_ROW_END_DATE int not null);")
            Log.i("Table created", newTableQuery)
            db.execSQL(newTableQuery)
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        }
    }
}