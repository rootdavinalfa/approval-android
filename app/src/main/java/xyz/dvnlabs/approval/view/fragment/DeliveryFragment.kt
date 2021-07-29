/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.fragment

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.data.UserRepo
import xyz.dvnlabs.approval.core.util.mapStatusTrx
import xyz.dvnlabs.approval.databinding.FragmentDeliveryBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.model.UserNoPassword
import xyz.dvnlabs.approval.view.viewmodel.MainViewModel
import xyz.dvnlabs.approval.view.viewmodel.UserViewModel

class DeliveryFragment : BottomSheetDialogFragment() {

    private var fragmentDeliveryBinding: FragmentDeliveryBinding? = null

    private val binding get() = fragmentDeliveryBinding!!

    private val userRepo: UserRepo by inject()

    private val transactionRepo: TransactionRepo by inject()

    private val userViewModel: UserViewModel by sharedViewModel()

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDeliveryBinding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return fragmentDeliveryBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        networkFetch()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }

    private fun initView() {
        userViewModel.userList.observe(viewLifecycleOwner, {
            binding.deliveryListUser.setItems(it.map { e -> e.userName })
        })

        userViewModel.transactionLive.observe(viewLifecycleOwner, {
            binding.deliveryTextId.text = "Transaction ID: ${it.idTransaction}"
            binding.deliveryTextStatus.text = it.statusFlag.mapStatusTrx()
        })

        binding.deliveryButtonAction.setOnClickListener {
            val position = binding.deliveryListUser.selectedIndex
            val user = userViewModel.userList.value?.get(position)
            val transactionDTO = userViewModel.transactionLive.value
            if (transactionDTO != null && user != null) {
                transactionRepo.attachDelivery(
                    transactionDTO.idTransaction,
                    user.id,
                    requireContext(),
                    mainViewModel.currentUser.value?.token ?: "",
                    object : BaseNetworkCallback<TransactionDTO> {
                        override fun onSuccess(data: TransactionDTO) {
                            dismiss()
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
                            println()
                        }

                        override fun onHideProgress() {
                            println()
                        }

                    }
                )
            }
        }
    }

    private fun networkFetch() {
        val currentUser = mainViewModel.currentUser.value
        currentUser?.let {
            userRepo.listUser(requireContext(), it.token, "", "ROLE_GUDANG",
                object : BaseNetworkCallback<List<UserNoPassword>> {
                    override fun onSuccess(data: List<UserNoPassword>) {
                        userViewModel.setUserList(data)
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
                        println()
                    }

                    override fun onHideProgress() {
                        println()
                    }

                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDeliveryBinding = null
    }
}