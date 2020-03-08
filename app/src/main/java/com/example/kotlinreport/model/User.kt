package com.example.kotlinreport.model

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

}

public enum class SexType(val value: Int) {
    MALE(1),
    FEMALE(0);

    companion object {
        fun valueOf(value: Int): SexType  = SexType.values().first { it.value == value }
    }
}