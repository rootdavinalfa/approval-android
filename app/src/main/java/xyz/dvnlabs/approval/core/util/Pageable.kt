/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.util

import java.net.URLEncoder
import java.util.*
import kotlin.collections.HashMap

class Pageable {

    private var pageable = HashMap<String, String>()

    fun pageRequest(page: Int = 1, size: Int = 10): Pageable {
        pageable["page"] = (page - 1).toString()
        pageable["size"] = size.toString()
        return this
    }

    fun sortRequest(field: String, direction: SORT_DIRECTION): Pageable {
        pageable["sort"] = "$field%2C${direction.name.lowercase(Locale.getDefault())}"
        return this
    }

    fun build(): HashMap<String, String> {
        return pageable
    }

    enum class SORT_DIRECTION {
        DESC,
        ASC
    }
}