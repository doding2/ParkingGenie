package com.android.parkingticketfriends.ui.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.databinding.FragmentReservationBinding
import com.android.parkingticketfriends.model.ParkingMarker
import com.android.parkingticketfriends.model.Reservation
import com.android.parkingticketfriends.model.ReservationRequest
import com.android.parkingticketfriends.ui.common.MapsViewModel
import com.android.parkingticketfriends.ui.common.MapsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReservationFragment : Fragment() {

    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MapsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_reservation, container, false)
        val factory = MapsViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), factory)[MapsViewModel::class.java]

        val marker = requireArguments().getSerializable("marker") as ParkingMarker
        val startTime = requireArguments().getString("start_time")!!
        val endTime = requireArguments().getString("end_time")!!
        val difference = requireArguments().getInt("difference")!!

        binding.parkingLotName.text = marker.name
        binding.name.text = marker.name
        binding.totalTime.text = if (difference / 60 > 0) {
            "총 ${difference / 60}시간 ${difference % 60}분"
        } else {
            "총 ${difference % 60}분"
        }
        binding.startTime.text = startTime
        binding.endTime.text = " ~ $endTime"
        binding.payment.text = "${difference * 50}원"
        binding.textPayButton.text = "총 ${difference * 50}원 결제"


        // 결제하기
        binding.textPayButton.setOnClickListener {

            viewModel.parkingMarkerInfo.value?.remove(marker)
            marker.parking_lots[0].status = "unavailable"
            viewModel.parkingMarkerInfo.value?.add(marker)

            binding.done.root.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "결제에 성공 했습니다.", Toast.LENGTH_SHORT).show()

            viewModel.reservations.value?.add(Reservation(marker.id, marker.parking_lots.first().id.toInt(), startTime, endTime, null))

//            CoroutineScope(Dispatchers.IO).launch {
//                val response = viewModel.server.doReservation(
//                    ReservationRequest(marker.parking_lots.first().id.toInt(), startTime, endTime)
//                )
//
//                if (response.isSuccessful) {
//                    CoroutineScope(Dispatchers.Main).launch {
//                        viewModel.parkingMarkerInfo.value?.remove(marker)
//                        marker.parking_lots[0].status = "unavailable"
//                        viewModel.parkingMarkerInfo.value?.add(marker)
//
//                        binding.done.root.visibility = View.VISIBLE
//                        Toast.makeText(requireContext(), "결제에 성공 했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//
//                } else {
//                    CoroutineScope(Dispatchers.Main).launch {
//                        Toast.makeText(requireContext(), "결제에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
        }

        binding.done.root.setOnClickListener {
            parentFragment?.findNavController()?.popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}