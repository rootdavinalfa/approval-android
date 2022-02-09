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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import xyz.dvnlabs.approval.R
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.RolePicker
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.UserNoPassword
import xyz.dvnlabs.approval.view.viewmodel.MainViewModel

open class FragmentBase : Fragment() {

    private val userRepo: UserRepo by inject()
    private val preferences: Preferences by inject()
    val basePreferences : Preferences get() = preferences
    private val mainViewModel: MainViewModel by sharedViewModel()
    val baseMainViewModel : MainViewModel get() = mainViewModel

    private val appBarConfig = AppBarConfiguration(setOf(R.id.menu_appbar))
    private lateinit var toolbar: Toolbar
    private lateinit var include: View

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
        fetchUser()
    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment -> toolbar.visibility = View.GONE
                R.id.detailTrxFragment -> toolbar.visibility = View.GONE
                R.id.notificationFragment -> toolbar.visibility = View.GONE
                R.id.reportFragment -> toolbar.visibility = View.GONE
                else -> toolbar.visibility = View.VISIBLE
            }
        }
    }

    fun showProgress() {
        include.visibility = View.VISIBLE
    }

    fun hideProgress() {
        include.visibility = View.GONE
    }

    fun fetchUser() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    mainViewModel.setCurrentUser(it)

                    // Get profile from API
                    userRepo.getProfile(
                        token = it.token,
                        userName = it.userName,
                        context = requireContext(),
                        callback = object : BaseNetworkCallback<UserNoPassword> {
                            override fun onSuccess(data: UserNoPassword) {
                                mainViewModel.setUserData(data)
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                Toast.makeText(activity, errorResponse.error, Toast.LENGTH_LONG)
                                    .show()
                            }

                            override fun onShowProgress() {
                                showProgress()
                            }

                            override fun onHideProgress() {
                                hideProgress()
                            }

                        }
                    )
                }
            }
        }
    }

}