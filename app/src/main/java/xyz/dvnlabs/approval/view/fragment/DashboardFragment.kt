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
import androidx.lifecycle.distinctUntilChanged
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
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.event.RefreshAction
import xyz.dvnlabs.approval.core.event.RxBus
import xyz.dvnlabs.approval.core.event.TargetAction
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.Page
import xyz.dvnlabs.approval.core.util.Pageable
import xyz.dvnlabs.approval.core.util.RolePicker
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
                    mainViewModel.userData.distinctUntilChanged()
                        .observe(viewLifecycleOwner, { user ->
                            if (user.id.isNotEmpty()) {
                                binding.dashboardTextUsername.text = user.userName
                                if (RolePicker
                                        .isUserHave("ROLE_APOTIK", user.roles)
                                ) {
                                    fetchApotik(it.token, it.userName)
                                    viewApotik()
                                }

                                if (RolePicker
                                        .isUserHave("ROLE_VGUDANG", user.roles)
                                ) {
                                    binding.dashboardButtonAdd.visibility = View.GONE
                                    fetchGudang(it.token, it.userName)
                                    viewVGudang()
                                }
                                if (RolePicker.isNotFound(
                                        listOf(
                                            "ROLE_APOTIK",
                                            "ROLE_VGUDANG",
                                            "ROLE_GUDANG",
                                            "ROLE_DELIVER",
                                        ), user.roles
                                    )
                                ) {
                                    fetchApotik(it.token, it.userName)
                                    viewApotik()
                                    fetchGudang(it.token, it.userName)
                                    viewVGudang()
                                }
                            }
                        })
                }
            }
        }
    }

    private fun fetchApotik(token: String, username: String) {
        transactionRepo.getPage(
            userRequest = username,
            statusFlagIn = "1,2,3,4",
            pageable = Pageable()
                .pageRequest(size = 5)
                .sortRequest("createdDate", Pageable.SORT_DIRECTION.DESC)
                .build(),
            context = requireContext(),
            token = token,
            callback = object : BaseNetworkCallback<Page<TransactionDTO>> {
                override fun onSuccess(data: Page<TransactionDTO>) {
                    mainViewModel.setTransactionLast(data.content)
                }

                override fun onFailed(errorResponse: ErrorResponse) {
                    Toast.makeText(
                        requireContext(),
                        errorResponse.error,
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

        // Delivered

        transactionRepo.getPage(
            userRequest = username,
            statusFlagIn = "4,5",
            pageable = Pageable()
                .pageRequest(size = 5)
                .sortRequest("createdDate", Pageable.SORT_DIRECTION.DESC)
                .build(),
            context = requireContext(),
            token = token,
            callback = object : BaseNetworkCallback<Page<TransactionDTO>> {
                override fun onSuccess(data: Page<TransactionDTO>) {
                    mainViewModel.setTransactionFinish(data.content)
                }

                override fun onFailed(errorResponse: ErrorResponse) {
                    Toast.makeText(
                        requireContext(),
                        errorResponse.error,
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

    private fun fetchGudang(token: String, username: String) {
        transactionRepo.getPage(
            statusFlagIn = "1",
            pageable = Pageable()
                .pageRequest(size = 5)
                .sortRequest("createdDate", Pageable.SORT_DIRECTION.DESC)
                .build(),
            context = requireContext(),
            token = token,
            callback = object : BaseNetworkCallback<Page<TransactionDTO>> {
                override fun onSuccess(data: Page<TransactionDTO>) {
                    mainViewModel.setTransactionValidate(data.content)
                }

                override fun onFailed(errorResponse: ErrorResponse) {
                    Toast.makeText(
                        requireContext(),
                        errorResponse.error,
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

        // Need Delivery

        transactionRepo.getPage(
            userRequest = username,
            statusFlagIn = "2",
            pageable = Pageable()
                .pageRequest(size = 5)
                .sortRequest("createdDate", Pageable.SORT_DIRECTION.DESC)
                .build(),
            context = requireContext(),
            token = token,
            callback = object : BaseNetworkCallback<Page<TransactionDTO>> {
                override fun onSuccess(data: Page<TransactionDTO>) {
                    mainViewModel.setTransactionDeliver(data.content)
                }

                override fun onFailed(errorResponse: ErrorResponse) {
                    Toast.makeText(
                        requireContext(),
                        errorResponse.error,
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

    private fun viewAction() {

        binding.dashboardButtonAdd.setOnClickListener {
            RequestFragment().show(requireActivity().supportFragmentManager, "ADD_FRAGMENT")
        }

        binding.dashboardImgNotif.setOnClickListener {
            val navController = requireActivity().findNavController(R.id.fragmentContainerView)
            navController.navigate(R.id.notificationFragment)

        }
    }

    private fun viewApotik() {
        binding.dashboardApotik.visibility = View.VISIBLE
        val adapterLast = RvListTrx(requireContext())
        binding.dashboardListtrxLayoutApotik.listtrxLast.layoutManager =
            LinearLayoutManager(requireContext())
        binding.dashboardListtrxLayoutApotik.listtrxLast.adapter = adapterLast

        mainViewModel.transactionLast.observe(viewLifecycleOwner, {
            adapterLast.setData(it)
        })

        val adapterFinish = RvListTrx(requireContext())
        binding.dashboardListtrxLayoutApotik.listtrxFinish.layoutManager =
            LinearLayoutManager(requireContext())
        binding.dashboardListtrxLayoutApotik.listtrxFinish.adapter = adapterFinish

        mainViewModel.transactionFinish.observe(viewLifecycleOwner, {
            adapterFinish.setData(it)
        })


    }

    private fun viewVGudang() {
        binding.dashboardVgudang.visibility = View.VISIBLE

        val adapterValidate = RvListTrx(requireContext())
        binding.dashboardListtrxLayoutVgudang.listtrxBelumvalidasi.layoutManager =
            LinearLayoutManager(requireContext())
        binding.dashboardListtrxLayoutVgudang.listtrxBelumvalidasi.adapter = adapterValidate

        mainViewModel.transactionValidate.observe(viewLifecycleOwner, {
            adapterValidate.setData(it)
        })

        val adapterDeliver = RvListTrx(requireContext())
        binding.dashboardListtrxLayoutVgudang.listtrxBelumpenugasan.layoutManager =
            LinearLayoutManager(requireContext())
        binding.dashboardListtrxLayoutVgudang.listtrxBelumpenugasan.adapter = adapterDeliver

        mainViewModel.transactionFinish.observe(viewLifecycleOwner, {
            adapterDeliver.setData(it)
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDashboardBinding = null
    }

}