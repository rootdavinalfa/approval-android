/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.util

data class Page<T>(
    val content: List<T>,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val last: Boolean = false,
    val first: Boolean = false,
    val number: Int = 0,
    val size: Int = 0,
    val numberOfElements: Int = 0
)