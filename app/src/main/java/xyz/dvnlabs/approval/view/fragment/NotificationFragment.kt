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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.NotificationRepo
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.databinding.FragmentNotificationBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.NotificationDTO
import xyz.dvnlabs.approval.view.rv.RvListHistory
import xyz.dvnlabs.approval.view.viewmodel.UserViewModel

class NotificationFragment : FragmentBase() {

    private var fragmentNotificationBinding: FragmentNotificationBinding? = null
    private val binding get() = fragmentNotificationBinding!!

    private val notificationRepo: NotificationRepo by inject()

    private val preferences: Preferences by inject()

    private val userViewModel: UserViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentNotificationBinding = FragmentNotificationBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        networkFetch()
    }


    private fun initView() {
        val adapter = RvListHistory(requireContext())
        binding.notificationListNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationListNotification.adapter = adapter
        userViewModel.userNotification.observe(viewLifecycleOwner, {
            adapter.setData(it)
        })
    }

    private fun networkFetch() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    notificationRepo.getList(
                        target = it.userName,
                        context = requireContext(),
                        token = it.token,
                        callback = object : BaseNetworkCallback<List<NotificationDTO>> {
                            override fun onSuccess(data: List<NotificationDTO>) {
                                userViewModel.setUserNotification(data.sortedByDescending { dt -> dt.createdDate })
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                Toast.makeText(
                                    requireContext(),
                                    errorResponse.message,
                                    Toast.LENGTH_LONG
                                ).show()
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


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentNotificationBinding = null
    }

}