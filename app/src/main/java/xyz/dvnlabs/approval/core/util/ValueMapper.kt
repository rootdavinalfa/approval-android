/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.util

fun String?.mapStatusTrx(): String {
    return when (this) {
        "1" -> {
            "OPEN"
        }
        "2" -> {
            "ON PROGRESS WAREHOUSE"
        }
        "3" -> {
            "ON DELIVERY"
        }
        "4" -> {
            "DELIVERED"
        }
        "5" -> {
            "CANCELED"
        }
        else -> {
            this ?: ""
        }
    }
}

fun String?.mapStatusTrxReverse(): String {
    return when (this) {
        "OPEN" -> {
            "1"
        }
        "ON PROGRESS WAREHOUSE" -> {
            "2"
        }
        "ON DELIVERY" -> {
            "3"
        }
        "DELIVERED" -> {
            "4"
        }
        "CANCELED" -> {
            "5"
        }
        else -> {
            this ?: ""
        }
    }
}

fun String?.mapStatusDetailTrx(): String {
    return when (this) {
        "1" -> {
            "Validate"
        }
        "2" -> {
            "Delivery"
        }
        "3" -> {
            "Sudah Diterima"
        }
        else -> {
            ""
        }
    }
}

fun String.extractNumber(): Int {
    return this.filter(Char::isDigit).toInt()
}
