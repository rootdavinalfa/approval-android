/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.model.LoginRequest
import xyz.dvnlabs.approval.model.LoginResponse

interface AuthAPI {
    @POST("${Constant.AUTH_URL}signin")
    fun signIn(@Body loginRequest: LoginRequest): Call<LoginResponse>
}