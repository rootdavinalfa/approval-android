/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiService {
    companion object {
        fun getClient(context: Context, baseUrl: String, token: String): Retrofit {
            val client = OkHttpClient()
                .newBuilder()
                .addInterceptor {
                    val newRequest = it.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    it.proceed(newRequest)
                }
                .addInterceptor(
                    ChuckerInterceptor
                        .Builder(context).build()
                )
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd\'T\'HH:mm:ss")

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson.create()))
                .build()
        }
    }
}