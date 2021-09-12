/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.notification


import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.UserNoPassword


class NotificationService : Service() {

    private val binder = NotificationBinder()

    private var mStompClient: StompClient? = null

    private var compositeDisposable: CompositeDisposable? = null

    private val userRepo: UserRepo by inject()

    private val preferences: Preferences by inject()

    private var userToken: String = ""

    inner class NotificationBinder() : Binder() {
        val service: NotificationService
            get() = this@NotificationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        mStompClient?.disconnect()
        if (compositeDisposable != null) compositeDisposable?.dispose()
        super.onDestroy()
    }

    @DelicateCoroutinesApi
    override fun onCreate() {
        mStompClient =
            Stomp.over(Stomp.ConnectionProvider.OKHTTP, Constant.BASE_URL_WEBSOCKET)

        GlobalScope.launch {
            connectStomp()
        }
    }

    private suspend fun connectStomp() {
        preferences.getUserPref.collect {
            if (it.token.isNotEmpty()) {
                userToken = it.token
                userRepo.getProfile(this, it.token, it.userName, callback = object :
                    BaseNetworkCallback<UserNoPassword> {
                    override fun onSuccess(data: UserNoPassword) {
                        runStomp(it.token)
                    }

                    override fun onFailed(errorResponse: ErrorResponse) {
                        println("FAILED: ${errorResponse.error}")
                    }

                    override fun onShowProgress() {

                    }

                    override fun onHideProgress() {

                    }

                })
            }
        }
    }

    suspend fun changedToken(){
        println("User token changed!")
        resetStomp()
        mStompClient?.disconnect()
        connectStomp()
    }

    fun runStomp(token: String) {
        val headers = listOf(
            StompHeader("Authorization", "Bearer ${token}")
        )

        println("HEADERS: $headers")
        mStompClient
            ?.withClientHeartbeat(1000)
            ?.withServerHeartbeat(1000)

        resetStomp()

        val dispLifecycle: Disposable =
            RxJavaBridge.toV3Disposable(
                mStompClient!!.lifecycle()
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        when (it.type) {
                            LifecycleEvent.Type.OPENED -> {
                                println("Stomp connection open")
                                Log.i("NotificationService", it.message, it.exception)
                            }
                            LifecycleEvent.Type.ERROR -> {
                                println("Stomp connection error")
                                Log.e("NotificationService", it.message, it.exception)
                            }
                            LifecycleEvent.Type.CLOSED -> {
                                println("Stomp connection closed")
                                Log.w("NotificationService", it.message, it.exception)
                                resetStomp()
                            }
                            LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                                println("Stomp failed server heartbeat")
                                Log.e("NotificationService", it.message, it.exception)
                            }
                        }
                    }, {
                        Log.e("NotificationService1", it.message, it)
                    })
            )

        compositeDisposable?.add(dispLifecycle)
        val dispTopic = RxJavaBridge.toV3Disposable(
            mStompClient!!.topic(Constant.NOTIFICATION_SUBSCRIBE_URL)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    println(it.payload)
                }, {
                    Log.e("NotificationService2", it.message, it)
                })
        )
        compositeDisposable?.add(dispTopic)
        mStompClient?.connect(headers)
    }

    private fun resetStomp() {
        if (compositeDisposable != null) {
            compositeDisposable?.dispose()
        }
        compositeDisposable = CompositeDisposable()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}