/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

import java.util.*

data class UserNoPassword(
    var id: String = "",
    var email: String = "",
    var userName: String = "",
    var registeredOn: Date = Date(),
    var roles: List<Role>? = null
)
