/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

data class TransactionDTO (
    var idTransaction: Long = 0,
    var transactionName: String = "",
    var statusFlag: String = "1",
    var message: String = "",
    var transactionDetails: List<TransactionDetailDTO>? = null,
    var userRequest: String? = null,
    var userApprove: String? = null,
    var userDelivery: String? = null,
    var userCancel: String? = null,
):AuditDTO()