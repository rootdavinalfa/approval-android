/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.data.local

import androidx.room.Entity
import java.util.*

@Entity(tableName = "Notification")
data class Notification(
    var idNotification: String = "",
    var receivedUser: String = "",
    var body: String = "",
    var sender: String = "",
    var target: String = "",
    /**
     * NOTIFICATION FLAG
     *
     * 0 = Received Not Read
     *
     * 1 = Received Read
     */
    var flag: String = "0",
    var idTransaction: Long = 0,
    var receivedOn: Date = Date()

)
