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
import xyz.dvnlabs.approval.model.DrugDTO
import xyz.dvnlabs.approval.model.NotificationDTO
import xyz.dvnlabs.approval.model.TransactionDTO

class UserViewModel(application: Application) : AndroidViewModel(application) {


    private val userSelectedDrugFlow: MutableStateFlow<List<DrugDTO>> =
        MutableStateFlow(ArrayList())

    val userSelectedDrug: LiveData<List<DrugDTO>> = userSelectedDrugFlow
        .asLiveData(viewModelScope.coroutineContext)
        .distinctUntilChanged()

    fun addUserSelectedDrug(drugDTO: DrugDTO) {
        val newDrugs = userSelectedDrugFlow.value.toMutableList()
        newDrugs.add(drugDTO)
        userSelectedDrugFlow.value = newDrugs
    }

    fun clearUserSelectedDrug(){
        userSelectedDrugFlow.value = emptyList()
    }

    private val transactionFlow: MutableStateFlow<TransactionDTO> =
        MutableStateFlow(TransactionDTO())

    val transactionLive: LiveData<TransactionDTO> = transactionFlow
        .asLiveData(viewModelScope.coroutineContext)
        .distinctUntilChanged()

    fun setTransaction(transactionDTO: TransactionDTO) {
        transactionFlow.value = transactionDTO
    }


    private val userNotificationFlow: MutableStateFlow<List<NotificationDTO>> =
        MutableStateFlow(ArrayList())

    val userNotification: LiveData<List<NotificationDTO>> = userNotificationFlow
        .asLiveData(viewModelScope.coroutineContext)
        .distinctUntilChanged()

    fun setUserNotification(notifications: List<NotificationDTO>) {
        userNotificationFlow.value = notifications
    }


}