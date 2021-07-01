/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.activity

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import xyz.dvnlabs.approval.databinding.ActivityMenuBinding

class MenuActivity : FragmentActivity() {

    private lateinit var menuBinding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(menuBinding.root)
    }
}