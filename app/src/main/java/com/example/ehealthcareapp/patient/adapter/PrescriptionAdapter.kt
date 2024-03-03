package com.example.ehealthcareapp.patient.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ehealthcareapp.R // Replace with your app's R file path
import com.example.ehealthcareapp.patient.models.Prescription // Replace with your app's package name and model class

class PrescriptionAdapter(private val prescriptionList: List<Prescription>) :
    RecyclerView.Adapter<PrescriptionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.patient_prescription_data, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = prescriptionList[position]

        holder.medicineTextView.text = currentItem.MedicineName
        holder.checkBoxMorning.isChecked = currentItem.Morning
        holder.checkBoxAfternoon.isChecked = currentItem.Afternoon
        holder.checkBoxNight.isChecked = currentItem.Night

        // Set checkboxes enabled or disabled based on boolean values
        holder.checkBoxMorning.isEnabled = currentItem.Morning
        holder.checkBoxAfternoon.isEnabled = currentItem.Afternoon
        holder.checkBoxNight.isEnabled = currentItem.Night
    }




    override fun getItemCount(): Int {
        return prescriptionList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val medicineTextView: TextView = itemView.findViewById(R.id.medicine)
        val checkBoxMorning: CheckBox = itemView.findViewById(R.id.checkBoxMorning)
        val checkBoxAfternoon: CheckBox = itemView.findViewById(R.id.checkBoxAfternoon)
        val checkBoxNight: CheckBox = itemView.findViewById(R.id.checkboxNight)
    }
}
