/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.base

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import xyz.dvnlabs.approval.R

open class FragmentBase : Fragment() {
    private val appBarConfig = AppBarConfiguration(setOf(R.id.menu_appbar))
    private lateinit var toolbar: Toolbar

    override fun onStart() {
        super.onStart()
        // setup navigation with toolbar
        toolbar = requireActivity().findViewById(R.id.menu_toolbar)
        val navController = requireActivity().findNavController(R.id.fragmentContainerView)
        visibilityNavElements(navController)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfig)
    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment -> toolbar.visibility = View.GONE
                else -> toolbar.visibility = View.VISIBLE
            }
        }
    }

}