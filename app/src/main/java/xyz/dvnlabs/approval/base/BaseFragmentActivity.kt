/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.base

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.MainActivity
import xyz.dvnlabs.approval.core.data.local.LocalDB
import xyz.dvnlabs.approval.core.event.RxBus
import xyz.dvnlabs.approval.core.event.UnAuthorized
import xyz.dvnlabs.approval.core.preferences.Preferences

open class BaseFragmentActivity : FragmentActivity() {

    private val preferences: Preferences by inject()
    private val localDB: LocalDB by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBus.listen(UnAuthorized::class.java)
            .subscribe {
                backToMain()
            }
    }

    private fun backToMain() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                localDB.userDAO().deleteUser(it)
                val user = localDB.userDAO().getUser(it.userName)
                if (user == null) {
                    val intent = Intent(this@BaseFragmentActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }
    }

}