/*
 * Copyright (c) 2022.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.base.BaseNetworkCallback
import xyz.dvnlabs.approval.base.FragmentBase
import xyz.dvnlabs.approval.core.data.TransactionRepo
import xyz.dvnlabs.approval.core.util.mapStatusTrx
import xyz.dvnlabs.approval.core.util.mapStatusTrxReverse
import xyz.dvnlabs.approval.databinding.FragmentReportBinding
import xyz.dvnlabs.approval.model.ErrorResponse
import xyz.dvnlabs.approval.model.TransactionDTO
import xyz.dvnlabs.approval.view.rv.RvListTrx
import java.util.*


class ReportFragment : FragmentBase() {

    private var fragmentReportBinding: FragmentReportBinding? = null

    private val transactionRepo: TransactionRepo by inject()

    private val binding get() = fragmentReportBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentReportBinding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val adapter = RvListTrx(requireContext())
        binding.reportStatusTransactionList.adapter = adapter
        binding.reportStatusTransactionList.layoutManager = LinearLayoutManager(requireContext())
        baseMainViewModel.transactionOverview.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        binding.reportTotalTransaction.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val pieEntry = e as PieEntry
                binding.reportStatusText.text =
                    "Transaksi dengan status: ${
                        pieEntry.label.lowercase().replaceFirstChar {
                            it.titlecase(
                                Locale.getDefault()
                            )
                        }
                    }"
                lifecycleScope.launch {
                    basePreferences.getUserPref.collectLatest {
                        pieEntry.label.mapStatusTrxReverse()
                        transactionRepo.getList(
                            statusFlagIn = pieEntry.label.mapStatusTrxReverse(),
                            token = it.token,
                            context = requireContext(),
                            callback = object : BaseNetworkCallback<List<TransactionDTO>> {
                                override fun onSuccess(data: List<TransactionDTO>) {
                                    baseMainViewModel.setTransactionOverview(data)
                                }

                                override fun onFailed(errorResponse: ErrorResponse) {
                                    Log.e("Fragment::Report", errorResponse.message)
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

            override fun onNothingSelected() {
            }

        })

        fetchUser()
        lifecycleScope.launch {
            basePreferences.getUserPref.collect {
                transactionRepo.getList(
                    context = requireContext(),
                    token = it.token,
                    callback = object : BaseNetworkCallback<List<TransactionDTO>> {
                        override fun onSuccess(data: List<TransactionDTO>) {
                            val pieEntry = data
                                .groupingBy { trx ->
                                    trx.statusFlag.mapStatusTrx()
                                }.eachCount().map { map ->
                                    PieEntry(map.value.toFloat(), map.key)
                                }

                            val dataSet = PieDataSet(pieEntry, "")
                            dataSet.valueTextSize = 0.5f

                            val description = binding.reportTotalTransaction.description
                            description.isEnabled = false



                            dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                            val pieData = PieData(dataSet)
                            pieData.setValueFormatter(PercentFormatter())
                            pieData.setValueTextSize(8f)
                            pieData.setValueTextColor(Color.WHITE)
                            binding.reportTotalTransaction.setEntryLabelTextSize(8f)
                            binding.reportTotalTransaction.data = pieData

                            // undo all highlights
                            binding.reportTotalTransaction.highlightValues(null)

                            val legend = binding.reportTotalTransaction.legend
                            legend.isWordWrapEnabled = true
                            legend.setDrawInside(false)
                            legend.textSize = 1f
                            legend.xEntrySpace = -5f
                            legend.yEntrySpace = 0f
                            legend.stackSpace = 0f
                            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                            legend.isEnabled = true


                            binding.reportTotalTransaction.centerText =
                                "Total transaksi: ${data.size}"
                            binding.reportTotalTransaction.isDrawHoleEnabled = true
                            binding.reportTotalTransaction.setUsePercentValues(true)


                            binding.reportTotalTransaction.invalidate()
                            binding.reportTotalTransaction.animateY(1000, Easing.EaseInOutQuad)


                        }

                        override fun onFailed(errorResponse: ErrorResponse) {
                            Toast.makeText(
                                requireContext(),
                                errorResponse.message,
                                Toast.LENGTH_SHORT
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