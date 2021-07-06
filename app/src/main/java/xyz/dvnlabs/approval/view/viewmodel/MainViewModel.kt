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
import org.koin.core.component.KoinComponent
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.model.UserNoPassword


class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val userDataFlow: MutableStateFlow<UserNoPassword> =
        MutableStateFlow(UserNoPassword())

    val userData: LiveData<UserNoPassword> = userDataFlow
        .asLiveData(viewModelScope.coroutineContext)
        .distinctUntilChanged()

    fun setUserData(userNoPassword: UserNoPassword) {
        userDataFlow.value = userNoPassword
    }

    private val transactionLastFlow: MutableStateFlow<List<TransactionDTO>> =
        MutableStateFlow(ArrayList())

    val transactionLast: LiveData<List<TransactionDTO>> = transactionLastFlow
        .asLiveData(viewModelScope.coroutineContext).distinctUntilChanged()

    fun setTransactionLast(transactionDTO: List<TransactionDTO>) {
        transactionLastFlow.value = transactionDTO
    }

    private val transactionFinishFlow: MutableStateFlow<List<TransactionDTO>> =
        MutableStateFlow(ArrayList())

    val transactionFinish: LiveData<List<TransactionDTO>> = transactionFinishFlow
        .asLiveData(viewModelScope.coroutineContext).distinctUntilChanged()

    fun setTransactionFinish(transactionDTO: List<TransactionDTO>) {
        transactionFinishFlow.value = transactionDTO
    }

}