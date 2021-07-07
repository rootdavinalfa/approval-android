/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.remote

import retrofit2.Call
import retrofit2.http.*
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.core.util.Page
import xyz.dvnlabs.approval.model.RequestTransactionDTO
import xyz.dvnlabs.approval.model.TransactionDTO

interface TransactionAPI {
    @POST("${Constant.TRANSACTION_URL}create")
    fun createTransaction(@Body createTransactionDTO: RequestTransactionDTO): Call<RequestTransactionDTO>

    @GET("${Constant.TRANSACTION_URL}list")
    fun getList(
        @Query("userApprove") userApprove: String? = null,
        @Query("userDelivery") userDelivery: String? = null,
        @Query("userRequest") userRequest: String? = null,
        @Query("statusFlagIn") statusFlagIn: String? = null
    ): Call<List<TransactionDTO>>

    @GET("${Constant.TRANSACTION_URL}page")
    fun getPage(
        @QueryMap(encoded = true) pageable: Map<String, String>,
        @Query("userApprove") userApprove: String? = null,
        @Query("userDelivery") userDelivery: String? = null,
        @Query("userRequest") userRequest: String? = null,
        @Query("statusFlagIn") statusFlagIn: String? = null,
    ): Call<Page<TransactionDTO>>

    @GET("${Constant.TRANSACTION_URL}{id}")
    fun getById(@Path("id") id: Long): Call<TransactionDTO>

}