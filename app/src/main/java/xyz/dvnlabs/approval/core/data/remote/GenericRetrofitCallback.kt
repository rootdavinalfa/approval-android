/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.remote

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.event.RxBus
import xyz.dvnlabs.approval.core.event.UnAuthorized
import xyz.dvnlabs.approval.model.ErrorResponse
import java.util.*

class GenericRetrofitCallback<T>(
    private val baseNetworkCallback: BaseNetworkCallback<T>
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        baseNetworkCallback.onHideProgress()
        when (response.code()) {
            200 -> {
                response.body()?.let { baseNetworkCallback.onSuccess(it) }
            }
            201 -> {
                response.body()?.let { baseNetworkCallback.onSuccess(it) }
            }
            401 -> {
                response.errorBody()?.let {
                    val json = Gson().fromJson(it.charStream(), ErrorResponse::class.java)
                    RxBus.publish(UnAuthorized(json))
                    baseNetworkCallback.onUnAuthorized(
                        json
                    )
                }
            }
            403 -> {
                response.errorBody()?.let {
                    baseNetworkCallback.onUnAuthorized(
                        Gson().fromJson(it.charStream(), ErrorResponse::class.java)
                    )
                }
            }
            404 -> {
                response.errorBody()?.let {
                    baseNetworkCallback.onFailed(
                        Gson().fromJson(it.charStream(), ErrorResponse::class.java)
                    )
                }
            }
        }

    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        baseNetworkCallback.onHideProgress()
        baseNetworkCallback.onFailed(
            ErrorResponse(
                message = "Failure on request",
                timestamp = Date(),
                error = t.localizedMessage!!
            )
        )
    }

}