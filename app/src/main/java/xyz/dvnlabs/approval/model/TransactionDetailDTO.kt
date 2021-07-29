/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

data class TransactionDetailDTO(
    var id: String = "",
    var detailRequest: String = "",
    var drug: DrugDTO? = null,
    var qty: Double = 0.0,
    var realQty: Double = 0.0
) : AuditDTO()
