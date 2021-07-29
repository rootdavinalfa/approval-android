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
import xyz.dvnlabs.approval.core.data.remote.AuthAPI
import xyz.dvnlabs.approval.core.data.remote.GenericRetrofitCallback
import xyz.dvnlabs.approval.model.LoginRequest
import xyz.dvnlabs.approval.model.LoginResponse
import xyz.dvnlabs.approval.model.UserNoPassword


class UserRepo {

    private val baseURL = Constant.BASE_URL

    fun login(
        context: Context,
        userName: String,
        password: String,
        callback: BaseNetworkCallback<LoginResponse>
    ) {
        callback.onShowProgress()
        val call = ApiService.getClient(context, baseURL, "").create(AuthAPI::class.java)
            .signIn(LoginRequest(userName, password))
        call.enqueue(GenericRetrofitCallback(callback))
    }

    fun getProfile(
        context: Context,
        token: String = "",
        userName: String = "",
        callback: BaseNetworkCallback<UserNoPassword>
    ) {
        callback.onShowProgress()
        val call = ApiService.getClient(context, baseURL, token).create(AuthAPI::class.java)
            .getUser(userName)
        call.enqueue(GenericRetrofitCallback(callback))

    }

    fun listUser(
        context: Context,
        token: String = "",
        userName: String = "",
        roleName: String = "",
        callback: BaseNetworkCallback<List<UserNoPassword>>
    ) {
        callback.onShowProgress()
        val call = ApiService.getClient(context, baseURL, token).create(AuthAPI::class.java)
            .getUserList(userName, roleName)
        call.enqueue(GenericRetrofitCallback(callback))
    }

}