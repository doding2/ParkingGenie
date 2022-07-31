package com.android.parkingticketfriends.ui.driver

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.TimePickerBindingAdapter.setMinute
import androidx.navigation.fragment.findNavController
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.databinding.FragmentMarkerSheetBinding
import com.android.parkingticketfriends.model.ParkingMarker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

class MarkerSheetFragment(private val marker: ParkingMarker) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "MarkerSheetFragment"
    }

    private var _binding: FragmentMarkerSheetBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_marker_sheet, container, false)

        // 버튼 비활성화
        binding.btnReservation.isClickable = false
        binding.btnReservation.isFocusable = false

        // 현재 시간 올림
        AndroidThreeTen.init(requireContext())

        val time = LocalDateTime.now()
        var intMinute: Int
        val intHour: Int
        val minute: String

        if (time.minute <= 30) {
            intMinute = 30
            minute = "30"
            intHour = time.hour
        } else {
            intMinute = 0
            minute = "00"
            intHour = time.plusHours(1L).hour
        }
        binding.textTime.text = "$intHour:$minute"


        // 데이트 픽커 세팅
        val year = time.year
        val month = time.monthValue
        val day = time.dayOfMonth

        // 최소 날짜 설정
        binding.datePicker.apply {
            minDate = System.currentTimeMillis() - 1000
        }

        // 시간 차이 계산
        var pickerYear: Int = year
        var pickerMonth: Int = month
        var pickerDay: Int = day
        var pickerHour: Int = intHour
        var pickerMinute: Int = intMinute

        var difMinutes: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.datePicker.setOnDateChangedListener { datePicker, i, i2, i3 ->
                pickerYear = i
                pickerMonth = i2 + 1
                pickerDay = i3

                difMinutes = (pickerYear - year) * 525600 + (pickerMonth - month) * 43800 + (pickerDay - day) * 1440 + (pickerHour - intHour) * 60 + pickerMinute - intMinute

                if( difMinutes <= 0) {
                    binding.textReservationTime.text = "0분"
                    binding.btnReservation.isClickable = false
                    binding.btnReservation.isFocusable = false
                } else {
                    val hour = difMinutes / 60
                    val minutes = difMinutes % 60
                    binding.textReservationTime.text = if (hour > 0) {
                        "${hour}시간 ${minutes}분"
                    } else {
                        "${minutes}분"
                    }
                    binding.btnReservation.isClickable = true
                    binding.btnReservation.isFocusable = true
                }
            }
        }

        // 타임 픽커 세팅
        binding.timePicker.apply {
            hour = intHour
            setIs24HourView(true)
            setMinute(intMinute)
        }

        // 시간 차이 계산
        binding.timePicker.setOnTimeChangedListener { timePicker, i, i2 ->

            pickerHour = i
            pickerMinute = i2

            difMinutes = (pickerYear - year) * 525600 + (pickerMonth - month) * 43800 + (pickerDay - day) * 1440 + (pickerHour - intHour) * 60 + pickerMinute - intMinute

            if( difMinutes <= 0) {
                binding.textReservationTime.text = "0분"
                binding.btnReservation.isClickable = false
                binding.btnReservation.isFocusable = false
            } else {
                val hour = difMinutes / 60
                val minutes = difMinutes % 60
                binding.textReservationTime.text = if (hour > 0) {
                    "${hour}시간 ${minutes}분"
                } else {
                    "${minutes}분"
                }
                binding.btnReservation.isClickable = true
                binding.btnReservation.isFocusable = true
            }
        }


        // 리셋 버튼
        binding.btnReset.setOnClickListener {
            binding.datePicker.apply {
                this.updateDate(year, month - 1, day)
            }
            binding.timePicker.apply {
                hour = intHour
                setMinute(intMinute)
            }
        }

        binding.btnReservation.setOnClickListener {
            val difference = (pickerYear - year) * 525600 + (pickerMonth - month) * 43800 + (pickerDay - day) * 1440 + (pickerHour - intHour) * 60 + pickerMinute - intMinute
            if (difference == 0) return@setOnClickListener

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
            val start = Calendar.getInstance()
            start.set(year, month - 1, day, intHour, intMinute, 0)

            val end = Calendar.getInstance()
            end.set(pickerYear, pickerMonth - 1, pickerDay, pickerHour, pickerMinute, 0)

            parentFragment?.findNavController()?.navigate(R.id.action_mapsFragment_to_reservationFragment,
                bundleOf(
                    "marker" to marker,
                    "start_time" to dateFormat.format(start.time),
                    "end_time" to dateFormat.format(end.time),
                    "difference" to difference))

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}