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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import xyz.dvnlabs.approval.core.util.ItemDiff
import xyz.dvnlabs.approval.databinding.RvListHistoryBinding
import xyz.dvnlabs.approval.model.NotificationDTO
import xyz.dvnlabs.approval.view.fragment.NotificationFragmentDirections

class RvListHistory(val context: Context) : RecyclerView.Adapter<RvListHistory.ViewHolder>() {

    private var dataList: List<NotificationDTO> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvListHistoryBinding
            .inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    fun setData(notification: List<NotificationDTO>) {
        val diffCallback =
            ItemDiff(this.dataList, notification, arrayOf("id"))
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList = notification
        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(val binding: RvListHistoryBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var notification: NotificationDTO? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView() {
            notification = dataList[bindingAdapterPosition]
            binding.rvListhistId.text =
                "Transaction ID: ${notification?.transaction?.idTransaction}"
            binding.rvListhistMessage.text = notification?.body
        }

        override fun onClick(v: View?) {
            val navController = itemView.findNavController()
            val action = NotificationFragmentDirections
                .actionNotificationFragmentToDetailTrxFragment(
                    idTransaction = notification?.transaction?.idTransaction ?: 0L
                )
            navController.navigate(action)
        }

    }
}