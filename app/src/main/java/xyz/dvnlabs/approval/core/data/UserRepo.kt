/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data

import org.koin.core.component.KoinComponent
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.model.LoginRequest
import xyz.dvnlabs.approval.model.LoginResponse
import xyz.dvnlabs.approval.model.UserNoPassword


class UserRepo : KoinComponent {

    private val baseURL = Constant.BASE_URL

    fun login(
        userName: String,
        password: String,
        callback: BaseNetworkCallback<LoginResponse>
    ) {
        callback.onShowProgress()
        val call = ApiService.getClient(baseURL, "").create(AuthAPI::class.java)
            .signIn(LoginRequest(userName, password))
        call.enqueue(GenericRetrofitCallback(callback))
    }

    fun getProfile(token: String, userName: String, callback: BaseNetworkCallback<UserNoPassword>) {

    }

}