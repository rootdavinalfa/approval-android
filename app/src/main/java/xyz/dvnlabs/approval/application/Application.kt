package xyz.dvnlabs.approval.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

open class Application : Application() {
    override fun onCreate() {
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule)
        }
        super.onCreate()
    }
}