package com.android.parkingticketfriends.ui.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.databinding.FragmentCheckReservationBinding
import com.android.parkingticketfriends.model.Reservation
import com.android.parkingticketfriends.ui.common.MapsViewModel
import com.android.parkingticketfriends.ui.common.MapsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckReservationFragment : Fragment() {


    private var _binding: FragmentCheckReservationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MapsViewModel
    private lateinit var adapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_reservation, container, false)
        val factory = MapsViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), factory)[MapsViewModel::class.java]


//         리저베이션 구독
//        viewModel.reservations.observe(viewLifecycleOwner) {
//            print("리스트: $it")
//            adapter = ReservationAdapter(it, ::stopReservation)
//            binding.recyclerView.adapter = adapter
//        }

        adapter = ReservationAdapter(viewModel.reservations.value!!, ::stopReservation)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    private fun stopReservation(item: Reservation) {
        val marker = viewModel.parkingMarkerInfo.value?.find {
            it.id == item.id
        }!!
        viewModel.parkingMarkerInfo.value?.remove(marker)
        marker.parking_lots[0].status = "available"
        viewModel.parkingMarkerInfo.value?.add(marker)

        adapter.notifyDataSetChanged()

//        CoroutineScope(Dispatchers.IO).launch {
//            val response = viewModel.server.stopReservation(item.id)
//
//            if (response.isSuccessful) {
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}