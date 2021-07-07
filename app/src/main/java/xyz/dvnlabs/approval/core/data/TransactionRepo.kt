/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data

import android.content.Context
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.core.data.remote.ApiService
import xyz.dvnlabs.approval.core.data.remote.GenericRetrofitCallback
import xyz.dvnlabs.approval.core.data.remote.TransactionAPI
import xyz.dvnlabs.approval.core.util.Page
import xyz.dvnlabs.approval.model.RequestTransactionDTO
import xyz.dvnlabs.approval.model.TransactionDTO

class TransactionRepo {

    private val baseURL = Constant.BASE_URL

    fun createTransaction(
        context: Context,
        requestTransactionDTO: RequestTransactionDTO,
        token: String,
        callback: BaseNetworkCallback<RequestTransactionDTO>
    ) {
        val call = ApiService.getClient(context, baseURL, token).create(TransactionAPI::class.java)
            .createTransaction(requestTransactionDTO)
        call.enqueue(GenericRetrofitCallback(callback))
    }

    fun getList(
        userApprove: String? = null,
        userDelivery: String? = null,
        userRequest: String? = null,
        statusFlagIn: String? = null,
        context: Context,
        token: String,
        callback: BaseNetworkCallback<List<TransactionDTO>>
    ) {
        val call = ApiService.getClient(context, baseURL, token).create(TransactionAPI::class.java)
            .getList(userApprove, userDelivery, userRequest, statusFlagIn)
        call.enqueue(GenericRetrofitCallback(callback))
    }

    fun getPage(
        userApprove: String? = null,
        userDelivery: String? = null,
        userRequest: String? = null,
        statusFlagIn: String? = null,
        pageable: Map<String, String>,
        context: Context,
        token: String,
        callback: BaseNetworkCallback<Page<TransactionDTO>>
    ) {

        val call = ApiService.getClient(context, baseURL, token).create(TransactionAPI::class.java)
            .getPage(pageable, userApprove, userDelivery, userRequest, statusFlagIn)
        call.enqueue(GenericRetrofitCallback(callback))
    }

    fun getById(
        id: Long,
        context: Context,
        token: String,
        callback: BaseNetworkCallback<TransactionDTO>
    ) {
        val call = ApiService.getClient(context, baseURL, token).create(TransactionAPI::class.java)
            .getById(id)
        call.enqueue(GenericRetrofitCallback(callback))
    }

}