package com.example.kotlinreport.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kotlinreport.model.User

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        @JvmStatic
        private val DATABASE_NAME: String = "my_db"
        @JvmStatic
        private val DATABASE_VERSION: Int = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createUserTable: String =
            "CREATE TABLE " + User.TABLE_NAME +
                    "(" +
                    User.COLUMN_ID_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    User.COLUMN_NAME_NAME + " CHAR(50) NOT NULL," +
                    User.COLUMN_SEX_NAME + " TINYINT NOT NULL," +
                    User.COLUMN_AVATA_NAME + " CHAR(100) NULL" +
                    ")"
        db?.execSQL(createUserTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropUserTable: String = "DROP TABLE IF EXISTS ${User.TABLE_NAME}"
        db?.execSQL(dropUserTable)
        onCreate(db)
    }
}