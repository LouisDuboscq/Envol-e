package com.lduboscq.envolee.android

import android.app.Application
import com.lduboscq.envolee.android.di.appModule
import com.lduboscq.envolee.di.initKoin
import org.koin.android.ext.koin.androidContext

class EnvoleeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(BuildConfig.base_url) {
            androidContext(this@EnvoleeApplication)
            modules(appModule)
        }
    }
}
