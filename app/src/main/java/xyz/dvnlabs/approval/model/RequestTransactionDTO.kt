/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

data class RequestTransactionDTO(
    var transactionDTO: TransactionDTO = TransactionDTO(),
    var transactionDetails: List<TransactionDetailDTO>? = null
)
