/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.util

import xyz.dvnlabs.approval.model.Role

class RolePicker {

    companion object {
        fun pickFromAdmin(role: List<Role>?): Role? {
            return role?.find { it.roleName == "ROLE_ADMIN" }
                ?: role?.find { it.roleName == "ROLE_VGUDANG" }
                ?: role?.find { it.roleName == "ROLE_GUDANG" }
                ?: role?.find { it.roleName == "ROLE_DELIVER" }
        }

        fun pickByRoleName(roleName: String, role: List<Role>?): Role? {
            return role?.find { it.roleName == roleName }
        }

        fun isUserHave(roleName: String, role: List<Role>?): Boolean {
            return role?.find { it.roleName == roleName }.isNotNull()
        }

        fun isUserHaves(roleNames: List<String>, role: List<Role>?): Boolean {
            return role?.find { roleNames.contains(it.roleName) }.isNotNull()
        }

        fun isNotFound(roleNames: List<String>, role: List<Role>?): Boolean {
            return role?.none { roleNames.contains(it.roleName) } ?: true
        }
    }

}