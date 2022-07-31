package com.android.parkingticketfriends.ui.driver

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.databinding.FragmentRegisterCarBinding

class RegisterCarFragment : Fragment() {


    private var _binding: FragmentRegisterCarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_car, container, false)

        binding.btnRegister.setOnClickListener {
            Toast.makeText(requireContext(), "차가 등록되었습니다", Toast.LENGTH_SHORT).show()
            binding.textView.setText("")
            binding.textView.clearFocus()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}