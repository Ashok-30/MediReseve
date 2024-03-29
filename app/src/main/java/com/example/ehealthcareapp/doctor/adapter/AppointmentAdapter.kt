package com.example.ehealthcareapp.doctor.adapter


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.ehealthcareapp.patient.models.Booking
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.doctor.DoctorRescheduleActivity



class AppointmentAdapter(private val bookingList : ArrayList<Booking> ) : RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_data,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookingList[position]



        holder.doctorName.text = currentItem.doctorName
        holder.patientName.text = currentItem.patientName
        holder.bookingDate.text = currentItem.selectedDate

        holder.patientDescription.text = currentItem.patientDescription

        val time = getTimeForSlot(currentItem.slot.toInt())
        holder.bookingTime.text = time
        holder.reschedule.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DoctorRescheduleActivity::class.java)

            intent.putExtra("bookingId", currentItem.bookingId)
            intent.putExtra("patientId", currentItem.patientId)
            intent.putExtra("doctorId", currentItem.doctorId)
            intent.putExtra("doctorName", currentItem.doctorName)
            intent.putExtra("patientName", currentItem.patientName)
            intent.putExtra("bookingDate", currentItem.selectedDate)
            intent.putExtra("patientDescription", currentItem.patientDescription)
            intent.putExtra("availabilityId", currentItem.availabilityId)
            intent.putExtra("bookingTime", time)
            intent.putExtra("slot", currentItem.slot)
            Log.d("slot",currentItem.slot)
            context.startActivity(intent)
        }




    }

    private fun getTimeForSlot(slot: Int): String {
        return when (slot) {
            0 -> "9AM-9:30AM"
            1 -> "9:30AM-10AM"
            2 -> "100AM-10:30AM"
            3 -> "10:30AM-11AM"
            4 -> "11:AM-11:30AM"
            5 -> "12PM-12:30PM"
            6 -> "12:30PM-1PM"
            7 -> "2PM-2:30PM"
            8 -> "2:30PM-3PM"
            9 -> "3PM-3:30PM"
            10 -> "3:30PM-4PM"
            11 -> "4PM-4:30PM"

            else -> "Walk In"
        }
    }


    override fun getItemCount(): Int {


        return bookingList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){


        val doctorName : TextView = itemView.findViewById(R.id.doctorName)
        val patientName : TextView = itemView.findViewById(R.id.patientName)
        val bookingTime : TextView = itemView.findViewById(R.id.bookingTime)
        val bookingDate : TextView = itemView.findViewById(R.id.bookingDate)
        val patientDescription : TextView = itemView.findViewById(R.id.patientDescription)

        val reschedule : Button = itemView.findViewById(R.id.reschedule)


    }


}