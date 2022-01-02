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
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkBuilder
import com.google.gson.Gson
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
import xyz.dvnlabs.approval.R
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.core.data.NotificationRepo
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.data.local.LocalDB
import xyz.dvnlabs.approval.core.data.local.Notification
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.extractNumber
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.NotificationDTO
import xyz.dvnlabs.approval.model.UserNoPassword
import xyz.dvnlabs.approval.view.activity.MenuActivity
import xyz.dvnlabs.approval.view.fragment.DetailTrxFragmentArgs


class NotificationService : Service() {

    private val binder = NotificationBinder()

    private var mStompClient: StompClient? = null

    private var compositeDisposable: CompositeDisposable? = null

    private val userRepo: UserRepo by inject()

    private val preferences: Preferences by inject()

    private val localDB: LocalDB by inject()

    private var userToken: String = ""

    private val notificationRepo: NotificationRepo by inject()


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

    @DelicateCoroutinesApi
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
                        Log.e("NotificationService", errorResponse.error)
                    }

                    override fun onShowProgress() {

                    }

                    override fun onHideProgress() {

                    }

                })
            }
        }
    }

    @DelicateCoroutinesApi
    suspend fun changedToken() {
        Log.i("NotificationService", "User token changed!")
        resetStomp()
        mStompClient?.disconnect()
        connectStomp()
    }

    @DelicateCoroutinesApi
    fun runStomp(token: String) {
        val headers = listOf(
            StompHeader("Authorization", "Bearer ${token}")
        )

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
                                Log.i("NotificationService", it.message, it.exception)
                            }
                            LifecycleEvent.Type.ERROR -> {
                                Log.e("NotificationService", it.message, it.exception)
                            }
                            LifecycleEvent.Type.CLOSED -> {
                                Log.w("NotificationService", it.message, it.exception)
                                resetStomp()
                            }
                            LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                                Log.e("NotificationService", it.message, it.exception)
                            }
                            else -> {
                                Log.e("NotificationService", it.message, it.exception)
                            }
                        }
                    }, {
                        Log.e("NotificationService", it.message, it)
                    })
            )

        compositeDisposable?.add(dispLifecycle)
        val dispTopic = RxJavaBridge.toV3Disposable(
            mStompClient!!.topic(Constant.NOTIFICATION_SUBSCRIBE_URL)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val notificationDTO = Gson().fromJson(it.payload, NotificationDTO::class.java)
                    Log.i("NotificationResponse", notificationDTO.toString())
                    saveNotification(notificationDTO)
                }, {
                    Log.e("NotificationService", it.message, it)
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

    @DelicateCoroutinesApi
    fun saveNotification(notificationDTO: NotificationDTO) {
        GlobalScope.launch {
            val notification =
                localDB.notificationDAO().findById(notificationDTO.id) ?: Notification(flag = "-1")

            if (notification.flag != notificationDTO.flag) {
                localDB.notificationDAO().save(
                    Notification(
                        idNotification = notificationDTO.id,
                        receivedUser = notificationDTO.target,
                        body = notificationDTO.body,
                        sender = notificationDTO.sender,
                        target = notificationDTO.target,
                        flag = notificationDTO.flag,
                        idTransaction = notificationDTO.transaction?.idTransaction,
                    )
                )
                val idTransaction = notificationDTO.transaction?.idTransaction ?: 0L

                val pendingIntent = NavDeepLinkBuilder(this@NotificationService)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.detailTrxFragment)
                    .setComponentName(MenuActivity::class.java)
                    .setArguments(DetailTrxFragmentArgs(idTransaction).toBundle())
                    .createPendingIntent()

                NotificationBuilder
                    .notify(
                        NotificationID.NOTIFICATION_NEW_UPDATE,
                        this@NotificationService,
                        notificationDTO.title,
                        notificationDTO.body,
                        pendingIntent,
                        notificationDTO.id.extractNumber()
                    )
            }
        }
    }

    suspend fun getNotification(userName: String = ""): List<Notification> {
        if (userName.isEmpty()) {
            return localDB.notificationDAO().getAll()
        }
        return localDB.notificationDAO().getAllByUser(userName)
    }

    suspend fun liveNotification(userName: String = ""): LiveData<List<Notification>> {
        if (userName.isEmpty()) {
            return localDB.notificationDAO().getAllLive()
        }
        return localDB.notificationDAO().getAllByUserLive(userName)
    }

    @DelicateCoroutinesApi
    fun refreshNotification() {
        GlobalScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    notificationRepo.getList(
                        target = it.userName,
                        context = this@NotificationService,
                        token = it.token,
                        callback = object : BaseNetworkCallback<List<NotificationDTO>> {
                            override fun onSuccess(data: List<NotificationDTO>) {
                                data.forEach { notification ->
                                    saveNotification(notification)
                                }
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                Log.e("NotificationService", errorResponse.message)
                            }

                            override fun onShowProgress() {

                            }

                            override fun onHideProgress() {

                            }
                        }
                    )
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}