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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import xyz.dvnlabs.approval.R
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.NotificationRepo
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.event.RefreshAction
import xyz.dvnlabs.approval.core.event.RxBus
import xyz.dvnlabs.approval.core.event.TargetAction
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.RolePicker
import xyz.dvnlabs.approval.core.util.mapStatusDetailTrx
import xyz.dvnlabs.approval.core.util.mapStatusTrx
import xyz.dvnlabs.approval.databinding.FragmentDetailTrxBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.NotificationDTO
import xyz.dvnlabs.approval.model.RequestTransactionDTO
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.view.rv.RvListDrug
import xyz.dvnlabs.approval.view.rv.RvListHistory
import xyz.dvnlabs.approval.view.viewmodel.MainViewModel
import xyz.dvnlabs.approval.view.viewmodel.UserViewModel

class DetailTrxFragment : FragmentBase() {

    private val args: DetailTrxFragmentArgs by navArgs()

    private var fragmentDetailTrxFragment: FragmentDetailTrxBinding? = null
    private val binding get() = fragmentDetailTrxFragment!!

    private val preferences: Preferences by inject()

    private val transactionRepo: TransactionRepo by inject()

    private val notificationRepo: NotificationRepo by inject()

    private val userViewModel: UserViewModel by sharedViewModel()

    private val mainViewModel: MainViewModel by sharedViewModel()

    private var idTransaction: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDetailTrxFragment = FragmentDetailTrxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        idTransaction = args.idTransaction
        networkFetch()
        initView()
    }

    private fun networkFetch() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    transactionRepo.getById(
                        idTransaction,
                        requireContext(),
                        it.token,
                        object : BaseNetworkCallback<TransactionDTO> {
                            override fun onSuccess(data: TransactionDTO) {
                                userViewModel.setTransaction(data)
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

                        })

                    notificationRepo.getList(
                        idTransaction = idTransaction,
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
                                )
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

    private fun initView() {
        val adapter = RvListDrug(requireContext())
        binding.detailListObat.layoutManager = LinearLayoutManager(requireContext())
        binding.detailListObat.adapter = adapter
        userViewModel.transactionLive.observe(viewLifecycleOwner, {
            it?.transactionDetails?.forEach { td ->
                td.drug?.qty = td.qty
            }

            it.transactionDetails?.mapNotNull { td -> td.drug }?.let { drg ->
                adapter.setData(drg)
            }
            binding.detailTextId.text = "Transaction ID: ${it.idTransaction}"
            binding.detailTextStatus.text = it.statusFlag.mapStatusTrx()
            binding.detailButtonAction.text = it.statusFlag.mapStatusDetailTrx()
        })
        mainViewModel.userData.observe(viewLifecycleOwner, { user ->
            if (user.id.isEmpty()) {
                return@observe
            }

            if (RolePicker.isUserHave(
                    "ROLE_APOTIK", user.roles
                )
            ) {
                binding.detailButtonCancel.isEnabled = true
                binding.detailButtonAction.isEnabled = false
            } else if (RolePicker.isUserHave(
                    "ROLE_VGUDANG", user.roles
                )
            ) {
                binding.detailButtonCancel.isEnabled = false
                binding.detailButtonAction.isEnabled = true
            } else {
                binding.detailButtonCancel.isEnabled = false
                binding.detailButtonAction.isEnabled = false
            }

            binding.detailButtonAction.setOnClickListener {
                if (RolePicker
                        .isUserHave("ROLE_VGUDANG", user.roles)
                ) {
                    userViewModel.transactionLive.value?.let {
                        when (it.statusFlag) {
                            "1" -> {
                                validate()
                            }
                            "2" -> {
                                RequestFragment().show(requireActivity().supportFragmentManager, "ATTACH_DELIVERY_FRAGMENT")
                            }
                        }
                    }
                } else if (RolePicker
                        .isUserHave("ROLE_GUDANG", user.roles)
                ) {
                    // Currently Nothing to do
                }
            }

        })

        binding.detailButtonCancel.setOnClickListener {
            cancelTrx()
        }

        val adapterHistory = RvListHistory(requireContext())
        binding.detailListHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.detailListHistory.adapter = adapterHistory
        userViewModel.userNotification.observe(viewLifecycleOwner, {
            adapterHistory.setData(it)
        })
    }

    private fun cancelTrx() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isEmpty()) {
                    return@collect
                }
                transactionRepo.cancelTransaction(
                    idTransaction,
                    requireContext(),
                    it.token,
                    object : BaseNetworkCallback<Void> {
                        override fun onSuccess(data: Void) {
                            requireActivity().onBackPressed()
                            //RxBus.publish(RefreshAction(TargetAction.FRAGMENT_DASHBOARD))
                        }

                        override fun onFailed(errorResponse: ErrorResponse) {
                            Toast.makeText(
                                requireContext(),
                                errorResponse.message,
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                        override fun onShowProgress() {
                            showProgress()
                        }

                        override fun onHideProgress() {
                            hideProgress()
                        }

                    })
            }
        }
    }

    private fun validate() {
        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isEmpty()) {
                    return@collect
                }
                val requestTransactionDTO = RequestTransactionDTO()
                requestTransactionDTO.transactionDTO = userViewModel.transactionLive.value!!
                requestTransactionDTO.transactionDetails =
                    userViewModel.transactionLive.value?.transactionDetails



                transactionRepo.validateTransaction(
                    requireContext(),
                    requestTransactionDTO,
                    it.token,
                    object : BaseNetworkCallback<TransactionDTO> {
                        override fun onSuccess(data: TransactionDTO) {
                            requireActivity().findNavController(
                                R.id.fragmentContainerView
                            ).navigateUp()
                            RxBus.publish(RefreshAction(TargetAction.FRAGMENT_DASHBOARD))
                        }

                        override fun onFailed(errorResponse: ErrorResponse) {
                            Toast.makeText(
                                requireContext(),
                                errorResponse.message,
                                Toast.LENGTH_LONG
                            )
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

    private fun delivery() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDetailTrxFragment = null
    }

}