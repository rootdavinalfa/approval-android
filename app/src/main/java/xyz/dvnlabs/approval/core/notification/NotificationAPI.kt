/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.*

class NotificationAPI(private val context: Context) : LifecycleService() {

    companion object {
        @JvmStatic
        var service: NotificationService? = null
            private set

        @JvmStatic
        var api: NotificationAPI? = null
            private set
    }

    var isServiceBound = false
        private set

    fun bind() {
        api = this
        Log.i(this.javaClass.simpleName, "BINDING")
        Intent(context, NotificationService::class.java).also {
            context.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }
        isServiceBound = true
    }

    fun unbind() {
        api = null
        Log.i(this.javaClass.simpleName, "UNBINDING")
        context.unbindService(serviceConnection)
        isServiceBound = false
        service = null
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(arg0: ComponentName, binder: IBinder) {
            val notificationBinder = binder as NotificationService.NotificationBinder
            service = notificationBinder.service
            isServiceBound = true
            if (service != null) {
                Log.i(this@NotificationAPI.javaClass.simpleName, "BIND OK!")
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.i(context.packageName, "ServiceConnection::onServiceDisconnected() called")
            service = null
            isServiceBound = false
            if (service == null) {
                api = null
                Log.i(this@NotificationAPI.javaClass.simpleName, "Service Disconnected")
            }
        }
    }
}