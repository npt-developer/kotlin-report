package com.example.kotlinreport.application

import android.app.Application
import com.example.kotlinreport.config.AppConfig
import com.facebook.stetho.Stetho

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // for debug
        if (AppConfig.APP_DEBUG) {
            // debug network + sqlite, ...
            Stetho.initializeWithDefaults(this)

        }
    }
}