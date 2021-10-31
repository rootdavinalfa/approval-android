/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.dvnlabs.approval.core.data.local.Notification
import xyz.dvnlabs.approval.core.notification.NotificationAPI
import xyz.dvnlabs.approval.core.preferences.Preferences

class NotificationViewModel(application: Application) : AndroidViewModel(application),
    KoinComponent {

    private val preference: Preferences by inject()

    var notificationList: LiveData<List<Notification>> = MutableLiveData()

    init {
        setNotification()
    }

    private fun setNotification() {
        viewModelScope.launch {
            preference.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    NotificationAPI.service?.liveNotification(it.userName)
                        ?.let { listNotification ->
                            notificationList = listNotification
                        }
                }
            }

        }
    }

}