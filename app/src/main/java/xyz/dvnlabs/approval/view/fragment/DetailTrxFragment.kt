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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.mapStatusTrx
import xyz.dvnlabs.approval.databinding.FragmentDetailTrxBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.view.rv.RvListDrug
import xyz.dvnlabs.approval.view.viewmodel.UserViewModel

class DetailTrxFragment : FragmentBase() {

    private val args: DetailTrxFragmentArgs by navArgs()

    private var fragmentDetailTrxFragment: FragmentDetailTrxBinding? = null
    private val binding get() = fragmentDetailTrxFragment!!

    private val preferences: Preferences by inject()

    private val transactionRepo: TransactionRepo by inject()

    private val userViewModel: UserViewModel by inject()

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

    private fun initView() {
        val adapter = RvListDrug(requireContext())
        binding.detailListObat.layoutManager = LinearLayoutManager(requireContext())
        binding.detailListObat.adapter = adapter
        userViewModel.transactionLive.observe(viewLifecycleOwner, {
            it.transactionDetails?.mapNotNull { td -> td.drug }?.let { drg ->
                adapter.setData(drg)
            }
            binding.detailTextId.text = "Transaction ID: ${it.idTransaction}"
            binding.detailTextStatus.text = it.statusFlag.mapStatusTrx()
            binding.detailButtonAction.isEnabled = it.statusFlag == "3"
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentDetailTrxFragment = null
    }

}