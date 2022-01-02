/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.base

import android.util.Log
import xyz.dvnlabs.approval.model.ErrorResponse


interface BaseNetworkCallback<T> {

    fun onSuccess(data: T)

    fun onFailed(errorResponse: ErrorResponse)

    fun onShowProgress()

    fun onHideProgress()

    fun onUnAuthorized(errorResponse: ErrorResponse){
        return
    }

    fun onVoid(){
        Log.i("NetworkFetch::OnVoid","Void")
    }

}