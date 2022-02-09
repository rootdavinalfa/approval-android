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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import xyz.dvnlabs.approval.R
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.NotificationRepo
import xyz.dvnlabs.approval.core.notification.NotificationAPI
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.databinding.FragmentNotificationBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.NotificationDTO
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.view.rv.RvListHistory
import xyz.dvnlabs.approval.view.viewmodel.NotificationViewModel
import xyz.dvnlabs.approval.view.viewmodel.UserViewModel

class NotificationFragment : FragmentBase() {

    private var fragmentNotificationBinding: FragmentNotificationBinding? = null
    private val binding get() = fragmentNotificationBinding!!

    private val notificationRepo: NotificationRepo by inject()

    private val preferences: Preferences by inject()

    private val userViewModel: UserViewModel by sharedViewModel()

    private val notificationViewModel: NotificationViewModel by sharedViewModel()

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
        NotificationAPI.service?.refreshNotification()
    }


    private fun initView() {
        val adapter = RvListHistory(requireContext())
        binding.notificationListNotification.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationListNotification.adapter = adapter

        binding.notificationListReport.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.fragmentContainerView)
            navController.navigate(R.id.reportFragment)
        }

        notificationViewModel.notificationList.observe(viewLifecycleOwner) {
            adapter.setData(it.map { notification ->
                NotificationDTO(
                    id = notification.idNotification,
                    body = notification.body,
                    target = notification.target,
                    flag = notification.flag,
                    transaction = TransactionDTO(idTransaction = notification.idTransaction!!)
                )
            }.toList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentNotificationBinding = null
    }

}