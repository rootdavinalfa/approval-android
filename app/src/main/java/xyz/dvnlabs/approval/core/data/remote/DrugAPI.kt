/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.remote

import retrofit2.Call
import retrofit2.http.GET
import xyz.dvnlabs.approval.core.Constant
import xyz.dvnlabs.approval.model.DrugDTO

interface DrugAPI {

    @GET("${Constant.DRUGS_URL}list")
    fun getList(): Call<List<DrugDTO>>

}