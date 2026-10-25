package com.fundaciontextil

import android.app.Application
import com.fundaciontextil.data.ServiceLocator

class TextilApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.iniciar(this)
    }
}