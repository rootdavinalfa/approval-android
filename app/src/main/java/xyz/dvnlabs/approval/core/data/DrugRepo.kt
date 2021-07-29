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
import xyz.dvnlabs.approval.core.data.remote.DrugAPI
import xyz.dvnlabs.approval.core.data.remote.GenericRetrofitCallback
import xyz.dvnlabs.approval.model.DrugDTO

class DrugRepo {

    private val baseURL = Constant.BASE_URL

    fun getList(
        context: Context,
        token: String,
        callback: BaseNetworkCallback<List<DrugDTO>>
    ) {
        callback.onShowProgress()
        val call = ApiService.getClient(context, baseURL, token).create(DrugAPI::class.java)
            .getList()
        call.enqueue(GenericRetrofitCallback(callback))
    }

}