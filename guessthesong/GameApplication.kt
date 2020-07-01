package com.example.guessthesong

import android.app.Application
import com.facebook.stetho.Stetho

class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}