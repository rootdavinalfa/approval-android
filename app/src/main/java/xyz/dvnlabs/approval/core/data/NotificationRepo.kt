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
import xyz.dvnlabs.approval.core.data.remote.NotificationAPI
import xyz.dvnlabs.approval.model.NotificationDTO

class NotificationRepo {
    private val baseURL = Constant.BASE_URL


    fun getList(
        sender: String = "",
        target: String = "",
        idTransaction: Long? = null,
        context: Context,
        token: String,
        callback: BaseNetworkCallback<List<NotificationDTO>>
    ) {
        callback.onShowProgress()
        val call = ApiService.getClient(context, baseURL, token).create(NotificationAPI::class.java)
            .getList(sender, target, idTransaction)
        call.enqueue(GenericRetrofitCallback(callback))
    }
}