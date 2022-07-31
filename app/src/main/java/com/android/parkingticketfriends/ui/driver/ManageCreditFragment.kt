package com.android.parkingticketfriends.ui.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.databinding.FragmentCheckReservationBinding
import com.android.parkingticketfriends.databinding.FragmentManageCreditBinding

class ManageCreditFragment : Fragment() {

    private var _binding: FragmentManageCreditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_credit, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}