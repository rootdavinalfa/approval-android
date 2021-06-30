/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.util

fun Any.isString(): Boolean {
    return this is String
}

fun Any?.isNull(): Boolean {
    return this == null
}

fun Any.isList(): Boolean{
    return this is Collection<*>
}