/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.databinding.FragmentDashboardBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.UserNoPassword

class DashboardFragment : FragmentBase() {

    private var fragmentDashboardBinding: FragmentDashboardBinding? = null

    private val binding get() = fragmentDashboardBinding!!

    private val userRepo: UserRepo by inject()

    private val preferences: Preferences by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDashboardBinding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                userRepo.getProfile(
                    token = it.token,
                    userName = it.userName,
                    context = requireContext(),
                    callback = object : BaseNetworkCallback<UserNoPassword> {
                        override fun onSuccess(data: UserNoPassword) {
                            binding.dashboardTextUsername.text = data.userName
                        }

                        override fun onFailed(errorResponse: ErrorResponse) {
                            Toast.makeText(activity, errorResponse.error, Toast.LENGTH_LONG).show()
                        }

                        override fun onShowProgress() {
                            println()
                        }

                        override fun onHideProgress() {
                            println()
                        }

                    }
                )
            }
            println("TOKEN ${preferences.getUserPref.lastOrNull()?.token}")


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDashboardBinding = null
    }

}