/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
    private lateinit var include : View

    override fun onStart() {
        super.onStart()
        // setup navigation with toolbar
        toolbar = requireActivity().findViewById(R.id.menu_toolbar)
        val navController = requireActivity().findNavController(R.id.fragmentContainerView)
        visibilityNavElements(navController)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfig)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        include = requireActivity().findViewById(R.id.menu_progress)
    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment -> toolbar.visibility = View.GONE
                R.id.detailTrxFragment -> toolbar.visibility = View.GONE
                R.id.notificationFragment -> toolbar.visibility = View.GONE
                else -> toolbar.visibility = View.VISIBLE
            }
        }
    }

    fun showProgress(){
        include.visibility = View.VISIBLE
    }

    fun hideProgress(){
        include.visibility = View.GONE
    }

}