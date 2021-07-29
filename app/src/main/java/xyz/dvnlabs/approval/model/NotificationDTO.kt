/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

data class NotificationDTO(
    var id: String = "",
    var title: String = "",
    var body: String = "",
    var sender: String = "",
    var target: String = "",
    /**
     * NOTIFICATION FLAG
     *
     * 0 = Published Not Read
     *
     * 1 = Published Read
     */
    var flag: String = "0",
    var transaction: TransactionDTO? = null
) : AuditDTO()
