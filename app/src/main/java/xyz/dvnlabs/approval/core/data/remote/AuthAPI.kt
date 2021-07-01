/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.model.LoginRequest
import xyz.dvnlabs.approval.model.LoginResponse
import xyz.dvnlabs.approval.model.UserNoPassword

interface AuthAPI {
    @POST("${Constant.AUTH_URL}signin")
    fun signIn(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("${Constant.USER_URL}username/{username}")
    fun getUser(@Path("username") username: String): Call<UserNoPassword>
}