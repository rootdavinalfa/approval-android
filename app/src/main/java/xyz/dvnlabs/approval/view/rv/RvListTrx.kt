/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.rv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.dvnlabs.approval.core.util.ItemDiff
import xyz.dvnlabs.approval.core.util.mapStatusTrx
import xyz.dvnlabs.approval.databinding.RvListTransactionBinding
import xyz.dvnlabs.approval.model.TransactionDTO

class RvListTrx(val context: Context) :
    RecyclerView.Adapter<RvListTrx.ViewHolder>() {

    private var dataList: List<TransactionDTO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvListTransactionBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(drugs: List<TransactionDTO>) {
        val diffCallback =
            ItemDiff(this.dataList, drugs, arrayOf("idTransaction"))
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList = drugs
        diffResult.dispatchUpdatesTo(this)
    }

    fun addData(drug: TransactionDTO) {
        val newDrug = dataList.toMutableList()
        newDrug.add(drug)
        val diffCallback =
            ItemDiff(this.dataList, newDrug, arrayOf("idTransaction"))
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList = newDrug
        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(val binding: RvListTransactionBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        var transactionDTO: TransactionDTO? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView() {
            transactionDTO = dataList[bindingAdapterPosition]
            binding.rvListtrxId.text = "Transaction: ${transactionDTO?.idTransaction?.toString()}"
            binding.rvListtrxStatus.text = transactionDTO?.statusFlag.mapStatusTrx()
        }

        override fun onClick(v: View?) {
            println()
        }


    }

}