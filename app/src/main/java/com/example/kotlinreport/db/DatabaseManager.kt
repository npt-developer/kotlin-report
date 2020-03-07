package com.example.kotlinreport.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.util.Log
import androidx.core.database.getStringOrNull
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User

class DatabaseManager {

    companion object {
        @JvmStatic
        private var mDbInstance: DatabaseHelper? = null
        @JvmStatic
        public fun getDbInstance(context: Context): DatabaseHelper? {
            synchronized(DatabaseManager::class) {
                if (mDbInstance == null) {
                    mDbInstance = DatabaseHelper(context)
                }
                return mDbInstance
            }
        }

        @JvmStatic
        public fun countUser(context: Context): Long {
            synchronized(DatabaseManager::class) {
                return DatabaseUtils.queryNumEntries(getDbInstance(context)!!.readableDatabase, User.TABLE_NAME)
            }
        }

        @JvmStatic
        public fun getUserList(context: Context, offset: Long, limit: Long): ArrayList<User> {
            synchronized(DatabaseManager::class) {
                var list: ArrayList<User> = ArrayList()

                var db = getDbInstance(context)!!.readableDatabase
                val columns: Array<String> = arrayOf(
                    User.COLUMN_ID_NAME,
                    User.COLUMN_NAME_NAME,
                    User.COLUMN_SEX_NAME,
                    User.COLUMN_AVATA_NAME
                )
                var cursor: Cursor = db.query(
                    User.TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "${offset},${limit}"
                )
                if (cursor.moveToFirst()) {
                    do {
                        list.add(User(
                            cursor.getLong(cursor.getColumnIndex(User.COLUMN_ID_NAME)),
                            cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_NAME)),
                            SexType.valueOf(cursor.getInt(cursor.getColumnIndex(User.COLUMN_SEX_NAME))),
                            cursor.getString(cursor.getColumnIndex(User.COLUMN_AVATA_NAME))
                        ))
                    } while (cursor.moveToNext())
                }
                return list
            }

        }

        @JvmStatic
        public fun insertUser(context: Context, user: User): Long {
            Log.d("DatabaseManager", "Insert user")
            synchronized(DatabaseManager::class) {
                var db = getDbInstance(context)!!.writableDatabase
                db.beginTransaction()

                var contentValues: ContentValues = ContentValues()
                contentValues.put(User.COLUMN_NAME_NAME, user.name)
                contentValues.put(User.COLUMN_SEX_NAME, user.sex.toString())
                contentValues.put(User.COLUMN_AVATA_NAME, user.avatar)

                val result: Long = db.insert(User.TABLE_NAME, null, contentValues)
                Log.d("DatabaseManager", "result ${result}")

                db.setTransactionSuccessful()
                db.endTransaction()
                return result
            }
        }

        @JvmStatic
        public fun updateUser(context: Context, user: User): Boolean {
            Log.d("DatabaseManager", "Update user")
            synchronized(DatabaseManager::class) {
                var db = getDbInstance(context)!!.writableDatabase
                db.beginTransaction()

                var contentValues: ContentValues = ContentValues()
                contentValues.put(User.COLUMN_NAME_NAME, user.name)
                contentValues.put(User.COLUMN_SEX_NAME, user.sex.toString())
                contentValues.put(User.COLUMN_AVATA_NAME, user.avatar)
                val where: String = "${User.COLUMN_ID_NAME} = ?"
                var whereArgs: Array<String> = arrayOf(user.id.toString())
                val result: Int = db.update(User.TABLE_NAME, contentValues, where, whereArgs)

                db.setTransactionSuccessful()
                db.endTransaction()

                return result > 0
            }
        }

        @JvmStatic
        public fun deleteAllUser(context: Context) {
            Log.d("DatabaseManager", "Delete all user")
            synchronized(DatabaseManager::class) {
                var db = getDbInstance(context)!!.writableDatabase
                db.beginTransaction()

                db.delete(User.TABLE_NAME, "${User.COLUMN_ID_NAME} > 0", null)

                db.setTransactionSuccessful()
                db.endTransaction()

            }
        }
    }
}