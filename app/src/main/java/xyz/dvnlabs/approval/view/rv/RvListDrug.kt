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
import xyz.dvnlabs.approval.databinding.RvListBarangBinding
import xyz.dvnlabs.approval.model.DrugDTO

class RvListDrug(val context: Context) :
    RecyclerView.Adapter<RvListDrug.ViewHolder>() {

    private var dataList: List<DrugDTO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvListBarangBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(drugs: List<DrugDTO>) {
        val diffCallback =
            ItemDiff(this.dataList, drugs, arrayOf("idDrug"))
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList = drugs
        diffResult.dispatchUpdatesTo(this)
    }

    fun getDrugs(): List<DrugDTO> {
        return dataList
    }

    fun addData(drug: DrugDTO) {
        val newDrug = dataList.toMutableList()
        newDrug.add(drug)
        val diffCallback =
            ItemDiff(this.dataList, newDrug, arrayOf("idDrug"))
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList = newDrug
        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(val binding: RvListBarangBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        var drug: DrugDTO? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView() {
            drug = dataList[bindingAdapterPosition]
            binding.rvListbarangNama.text = drug?.drugName
            binding.rvListbarangQty.text = drug?.qty?.toString()
        }

        override fun onClick(v: View?) {
            println()
        }


    }

}