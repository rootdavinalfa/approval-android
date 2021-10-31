/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core

object Constant {
    const val HOST = "srv.dvnlabs.xyz:8080"
    const val BASE_URL = "http://$HOST/"
    const val USER_URL = "user/"
    const val AUTH_URL = "auth/"
    const val DRUGS_URL = "drugs/"
    const val TRANSACTION_URL = "transaction/"
    const val NOTIFICATION_URL = "notification/"
    const val BASE_URL_WEBSOCKET = "ws://$HOST/live/websocket"
    const val NOTIFICATION_SUBSCRIBE_URL = "/user/queue/notification"
}