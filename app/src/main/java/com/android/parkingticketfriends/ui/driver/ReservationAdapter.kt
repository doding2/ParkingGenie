package com.android.parkingticketfriends.ui.driver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.parkingticketfriends.R
import com.android.parkingticketfriends.databinding.ItemReservationBinding
import com.android.parkingticketfriends.model.Reservation
import java.text.SimpleDateFormat
import java.util.*

class ReservationAdapter(
    private var items: List<Reservation>,
    private val onRemoveClick: (Reservation) -> Unit
): RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemReservationBinding = DataBindingUtil.inflate(inflater, R.layout.item_reservation, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemReservationBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Reservation
        ) {
            val format = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
            val startDate = format.parse(item.start_time)
            val endDate = format.parse(item.end_time)

            val longTime = startDate.time - endDate.time
            val hours = (longTime / (1000 * 60 * 60) % 24).toInt()
            val minutes = (longTime / (1000 * 60) % 60).toInt()

            binding.timeConsuming.text = if (hours > 0) {
                "${hours}시간 ${minutes}분"
            } else {
                "${minutes}분"
            }
            binding.parkingName.text = "ID: ${item.id}"
            binding.startTime.text = "시작 : ${item.start_time}"
            binding.endTime.text = "끝 : ${item.end_time}"
            binding.stopTime.text = if (item.stop_time == null) {
                binding.stopReservation.visibility = View.VISIBLE
                binding.word.visibility = View.VISIBLE
                ""
            } else {
                binding.stopReservation.visibility = View.GONE
                binding.word.visibility = View.GONE
                "종료 : ${item.stop_time}"
            }

            binding.stopReservation.setOnClickListener {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault())
                item.stop_time = dateFormat.format(Calendar.getInstance().time)
                onRemoveClick(item)
            }
        }
    }
}