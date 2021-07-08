/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.model.NotificationDTO

interface NotificationAPI {

    @GET("${Constant.NOTIFICATION_URL}list")
    fun getList(
        @Query("sender") sender: String = "",
        @Query("target") target: String = ""
    ): Call<List<NotificationDTO>>

}