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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import xyz.dvnlabs.approval.R
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.event.RefreshAction
import xyz.dvnlabs.approval.core.event.RxBus
import xyz.dvnlabs.approval.core.event.TargetAction
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.Page
import xyz.dvnlabs.approval.core.util.Pageable
import xyz.dvnlabs.approval.databinding.FragmentDashboardBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.model.UserNoPassword
import xyz.dvnlabs.approval.view.rv.RvListTrx
import xyz.dvnlabs.approval.view.viewmodel.MainViewModel

class DashboardFragment : FragmentBase() {

    private var fragmentDashboardBinding: FragmentDashboardBinding? = null

    private val binding get() = fragmentDashboardBinding!!

    private val userRepo: UserRepo by inject()

    private val transactionRepo: TransactionRepo by inject()

    private val preferences: Preferences by inject()

    private val mainViewModel: MainViewModel by sharedViewModel()

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
                if (it.token.isNotEmpty()) {
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
        networkFetch()
        viewAction()
        RxBus.listen(RefreshAction::class.java)
            .subscribe {
                if (it.where == TargetAction.FRAGMENT_DASHBOARD) {
                    networkFetch()
                }
            }
    }

    private fun networkFetch() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    transactionRepo.getPage(
                        userRequest = it.userName,
                        statusFlagIn = "1,2,3,4",
                        pageable = Pageable()
                            .pageRequest(size = 5)
                            .sortRequest("createdDate", Pageable.SORT_DIRECTION.DESC)
                            .build(),
                        context = requireContext(),
                        token = it.token,
                        callback = object : BaseNetworkCallback<Page<TransactionDTO>> {
                            override fun onSuccess(data: Page<TransactionDTO>) {
                                mainViewModel.setTransactionLast(data.content)
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        errorResponse.error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onShowProgress() {
                                println()
                            }

                            override fun onHideProgress() {
                                println()
                            }

                        }
                    )

                    // Delivered

                    transactionRepo.getPage(
                        userRequest = it.userName,
                        statusFlagIn = "4,5",
                        pageable = Pageable()
                            .pageRequest(size = 5)
                            .sortRequest("createdDate", Pageable.SORT_DIRECTION.DESC)
                            .build(),
                        context = requireContext(),
                        token = it.token,
                        callback = object : BaseNetworkCallback<Page<TransactionDTO>> {
                            override fun onSuccess(data: Page<TransactionDTO>) {
                                mainViewModel.setTransactionFinish(data.content)
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        errorResponse.error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
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

    private fun viewAction() {

        val adapterLast = RvListTrx(requireContext())
        binding.dashboardListtrxLayout.listtrxLast.layoutManager =
            LinearLayoutManager(requireContext())
        binding.dashboardListtrxLayout.listtrxLast.adapter = adapterLast

        mainViewModel.transactionLast.observe(viewLifecycleOwner, {
            adapterLast.setData(it)
        })

        val adapterFinish = RvListTrx(requireContext())
        binding.dashboardListtrxLayout.listtrxFinish.layoutManager =
            LinearLayoutManager(requireContext())
        binding.dashboardListtrxLayout.listtrxFinish.adapter = adapterFinish

        mainViewModel.transactionFinish.observe(viewLifecycleOwner, {
            adapterFinish.setData(it)
        })


        binding.dashboardButtonAdd.setOnClickListener {
            RequestFragment().show(requireActivity().supportFragmentManager, "ADD_FRAGMENT")
        }

        binding.dashboardImgNotif.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.fragmentContainerView)
            navController.navigate(R.id.notificationFragment)

        }

        mainViewModel.userData.observe(viewLifecycleOwner,
            {
                binding.dashboardTextUsername.text = it.userName
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDashboardBinding = null
    }

}