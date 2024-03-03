package com.example.ehealthcareapp.patient.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.ehealthcareapp.patient.models.Booking
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.PatientPrescriptionActivity
import com.example.ehealthcareapp.patient.PatientRatingActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MedicineAdapter(private val bookingList : ArrayList<Booking> ) : RecyclerView.Adapter<MedicineAdapter.MyViewHolder>() {
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Rating")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.medicine_data,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookingList[position]

        holder.bookingId.text = currentItem.bookingId
        holder.patientId.text = currentItem.patientId
        holder.doctorId.text = currentItem.doctorId

        holder.doctorName.text = currentItem.doctorName
        holder.patientName.text = currentItem.patientName
        holder.bookingDate.text = currentItem.selectedDate
        val ageWithPrefix = "Age: ${currentItem.patientAge}"
        holder.patientAge.text = ageWithPrefix

        val time = getTimeForSlot(currentItem.slot.toInt())
        holder.bookingTime.text = time

        dbRef.orderByChild("bookingId").equalTo(currentItem.bookingId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                        holder.rateButton.setOnClickListener {
                            val context = holder.itemView.context
                            val intent = Intent(context, PatientPrescriptionActivity::class.java)

                            intent.putExtra("bookingId", currentItem.bookingId)
                            intent.putExtra("patientId", currentItem.patientId)
                            intent.putExtra("doctorId", currentItem.doctorId)
                            intent.putExtra("doctorName", currentItem.doctorName)
                            intent.putExtra("patientName", currentItem.patientName)
                            intent.putExtra("bookingDate", currentItem.selectedDate)
                            intent.putExtra("patientAge", currentItem.patientAge)
                            intent.putExtra("bookingTime", time)

                            context.startActivity(intent)
                        }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation if needed
                }
            })
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

        val bookingId : TextView = itemView.findViewById(R.id.bookingId)
        val patientId : TextView = itemView.findViewById(R.id.patientId)
        val doctorId : TextView = itemView.findViewById(R.id.doctorId)
        val doctorName : TextView = itemView.findViewById(R.id.doctorName)
        val patientName : TextView = itemView.findViewById(R.id.patientName)
        val bookingTime : TextView = itemView.findViewById(R.id.bookingTime)
        val bookingDate : TextView = itemView.findViewById(R.id.bookingDate)
        val patientAge : TextView = itemView.findViewById(R.id.patientAge)
        val rateButton : Button = itemView.findViewById(R.id.rateButton)


    }


}