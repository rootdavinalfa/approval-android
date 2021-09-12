package xyz.dvnlabs.approval.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import xyz.dvnlabs.approval.core.notification.NotificationAPI

open class Application : Application() {
    private var notificationAPI: NotificationAPI? = null


    override fun onCreate() {
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule)
        }
        notificationAPI = NotificationAPI(this)
        if (NotificationAPI.service == null) {
            notificationAPI?.bind()
        }

        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        notificationAPI?.unbind()
    }
}