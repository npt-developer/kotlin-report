package com.example.kotlinreport.model

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import com.example.kotlinreport.config.AppConfig
import java.io.File
import java.io.FileOutputStream

class User(var id: Long?, var name: String, var sex: SexType, var avatar: String?) {

    companion object {
        @JvmStatic
        public val TABLE_NAME: String = "user"
        @JvmStatic
        public val COLUMN_ID_NAME: String = "id"
        @JvmStatic
        public val COLUMN_NAME_NAME: String = "name"
        @JvmStatic
        public val COLUMN_SEX_NAME: String = "sex"
        @JvmStatic
        public val COLUMN_AVATAR_NAME: String = "avatar"
    }

    fun saveAvatar(context: Context, bitmap: Bitmap) {
        Log.d("saveAvatar", avatar)

        var cw = ContextWrapper(context)
        var directory = cw.getDir(AppConfig.User.AVATAR_FOLDER_NAME, Context.MODE_PRIVATE)
        if (!directory.isDirectory) {
            directory.mkdir()
        }

        var avataPath = File(directory, avatar)

        var fos = FileOutputStream(avataPath)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
    }
}

public enum class SexType(val value: Int) {
    MALE(1),
    FEMALE(0);

    companion object {
        fun valueOf(value: Int): SexType  = SexType.values().first { it.value == value }
    }
}