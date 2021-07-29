/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.core.event

import xyz.dvnlabs.approval.model.ErrorResponse

data class UnAuthorized(val message: ErrorResponse)

data class RefreshAction(val where: TargetAction)