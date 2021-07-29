/*
 * Copyright (c) 2021.
 * Davin Alfarizky Putra Basudewa
 * Reference or Educational Purposes Only
 * Skripshit Client
 */

package xyz.dvnlabs.approval.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import xyz.dvnlabs.approval.MainActivity
import xyz.dvnlabs.approval.core.data.local.LocalDB
import xyz.dvnlabs.approval.databinding.FragmentProfileSelectorBinding
import xyz.dvnlabs.approval.view.rv.RvListProfile

class ProfileSelectorFragment : BottomSheetDialogFragment() {

    private var fragmentProfileSelectorBinding: FragmentProfileSelectorBinding? = null
    private val binding get() = fragmentProfileSelectorBinding!!

    private val localDB: LocalDB by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProfileSelectorBinding =
            FragmentProfileSelectorBinding.inflate(inflater, container, false)
        return fragmentProfileSelectorBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        lifecycleScope.launch {
            val users = localDB.userDAO().getAllUser()
            users?.let {
                val adapter = RvListProfile(requireContext())
                binding.profileSelectorList.adapter = adapter
                binding.profileSelectorList.layoutManager = LinearLayoutManager(requireContext())
                adapter.setData(it)
            }
        }

        binding.profileSelectorAdd.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("EVENT","EVENT_PROFILE_ADD")
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentProfileSelectorBinding = null
    }

}