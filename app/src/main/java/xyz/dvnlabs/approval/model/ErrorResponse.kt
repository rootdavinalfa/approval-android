/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

import java.util.*

data class ErrorResponse(
    var path: String = "",
    var message: String = "",
    var error: String = "",
    var status: String = "",
    var timestamp: Date = Date()
)
