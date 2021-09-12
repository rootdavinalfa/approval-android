/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.activity

import android.content.Intent
import android.os.Bundle
import xyz.dvnlabs.approval.base.BaseFragmentActivity
import xyz.dvnlabs.approval.core.notification.NotificationService
import xyz.dvnlabs.approval.databinding.ActivityMenuBinding

class MenuActivity : BaseFragmentActivity() {

    private lateinit var menuBinding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(menuBinding.root)
        //startService()
    }

    private fun startService() {
        Intent(this, NotificationService::class.java)
            .also {
                startService(it)
            }
    }
}