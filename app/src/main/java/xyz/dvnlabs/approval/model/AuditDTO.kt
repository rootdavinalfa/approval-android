/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

import java.util.*

open class AuditDTO(
    var createdBy: String = "",
    var createdDate: Date = Date(),
    var lastModifiedBy: String = "",
    var lastModifiedDate: Date = Date(),
)
