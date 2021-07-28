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
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import xyz.dvnlabs.approval.core.data.local.User
import xyz.dvnlabs.approval.core.preferences.Preferences
import xyz.dvnlabs.approval.core.util.ItemDiff
import xyz.dvnlabs.approval.databinding.RvListProfileBinding

class RvListProfile(val context: Context) :
    RecyclerView.Adapter<RvListProfile.ViewHolder>(),KoinComponent {

    private var dataList: List<User> = emptyList()
    private val preferences: Preferences by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvListProfileBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(user: List<User>) {
        val diffCallback =
            ItemDiff(this.dataList, user, arrayOf("userName"))
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.dataList = user
        diffResult.dispatchUpdatesTo(this)
    }


    inner class ViewHolder(private val binding: RvListProfileBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        var user: User? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView() {
            user = dataList[bindingAdapterPosition]
            binding.rvListprofileUsername.text = user?.userName
        }

        @DelicateCoroutinesApi
        override fun onClick(v: View?) {
            user?.let {
                GlobalScope.async {
                    preferences.savePreference(it)
                }
            }
        }


    }

}