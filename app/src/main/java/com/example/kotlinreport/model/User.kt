package com.example.kotlinreport.model

class User(var id: Int, var name: String, var sex: SexType, var avata: String?) {
}

public enum class SexType(val sex: Int) {
    MALE(1),
    FEMALE(0)
}