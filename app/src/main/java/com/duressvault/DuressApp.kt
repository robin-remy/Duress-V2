package com.duressvault

import android.app.Application
import com.duressvault.data.PinRepository

class DuressApp : Application() {
    lateinit var pinRepository: PinRepository
        private set

    override fun onCreate() {
        super.onCreate()
        pinRepository = PinRepository(this)
    }
}
