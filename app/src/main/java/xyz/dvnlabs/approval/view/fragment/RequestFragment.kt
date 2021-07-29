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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.dvnlabs.approval.R
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.core.data.DrugRepo
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.event.RefreshAction
import xyz.dvnlabs.approval.core.event.RxBus
import xyz.dvnlabs.approval.core.event.TargetAction
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.databinding.FragmentCreateBinding
import xyz.dvnlabs.approval.model.DrugDTO
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.RequestTransactionDTO
import xyz.dvnlabs.approval.model.TransactionDetailDTO
import xyz.dvnlabs.approval.view.rv.RvListDrug
import xyz.dvnlabs.approval.view.viewmodel.UserViewModel


class RequestFragment : BottomSheetDialogFragment() {

    private var fragmentCreateBinding: FragmentCreateBinding? = null

    private val binding get() = fragmentCreateBinding!!

    private val drugRepo: DrugRepo by inject()

    private val transactionRepo: TransactionRepo by inject()

    private val preferences: Preferences by inject()

    private val userViewModel: UserViewModel by sharedViewModel()

    private var drugs: List<DrugDTO> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCreateBinding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewInit()
    }


    private fun viewInit() {
        val adapter = RvListDrug(requireContext())
        binding.createListObat.layoutManager = LinearLayoutManager(requireContext())
        binding.createListObat.adapter = adapter

        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    drugRepo.getList(
                        requireContext(),
                        it.token,
                        object : BaseNetworkCallback<List<DrugDTO>> {
                            override fun onSuccess(data: List<DrugDTO>) {
                                drugs = data
                                binding.createObatTextf.setItems(data.map { drg -> drg.drugName })
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                println()
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
        }

        userViewModel.userSelectedDrug.observe(viewLifecycleOwner, {
            adapter.setData(it)
        })

        binding.createButtonAdd.setOnClickListener {
            val position = binding.createObatTextf.selectedIndex
            val drug = drugs[position]
            drug.qty = binding.createObatqtyTextf.text.toString().toDouble()
            userViewModel.addUserSelectedDrug(
                drug
            )
        }

        binding.createButtonCreatereq.setOnClickListener {
            requestTransaction()
        }

    }

    private fun requestTransaction() {
        val requestTransactionDTO = RequestTransactionDTO()
        val detailTransaction: MutableList<TransactionDetailDTO> = ArrayList()
        userViewModel.userSelectedDrug.value?.let {
            it.forEach { drg ->
                detailTransaction.add(
                    TransactionDetailDTO(
                        drug = drg,
                        qty = drg.qty
                    )
                )
            }
        }

        requestTransactionDTO.transactionDetails = detailTransaction

        lifecycleScope.launch {
            preferences.getUserPref.collect {
                if (it.token.isNotEmpty()) {
                    transactionRepo.createTransaction(
                        requireContext(),
                        requestTransactionDTO,
                        it.token,
                        object : BaseNetworkCallback<RequestTransactionDTO> {
                            override fun onSuccess(data: RequestTransactionDTO) {
                                userViewModel.clearUserSelectedDrug()
                                RxBus.publish(RefreshAction(TargetAction.FRAGMENT_DASHBOARD))
                                this@RequestFragment.dismiss()
                            }

                            override fun onFailed(errorResponse: ErrorResponse) {
                                Toast.makeText(
                                    requireContext(),
                                    errorResponse.error,
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
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentCreateBinding = null
    }

}