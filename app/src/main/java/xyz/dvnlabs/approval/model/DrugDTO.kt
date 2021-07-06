/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.model

data class DrugDTO(
    var idDrug: Long = 0,
    var drugName: String = "",
    var classified: String = "",
    var qty: Double = 0.0,
) : AuditDTO()