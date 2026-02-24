package com.dev.nereya.shushme

import android.app.Application
import com.dev.nereya.shushme.utils.SignalManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}