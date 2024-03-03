package com.example.ehealthcareapp.patient.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView

import com.example.ehealthcareapp.patient.models.Doctors
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.PatientAppointment



class DoctorAdapter(private val context: Context,private val doctorList : ArrayList<Doctors>,
private val patientId: String,private val patientName: String ) : RecyclerView.Adapter<DoctorAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.doctor_data,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {



        val currentItem = doctorList[position]

        holder.first.text = currentItem.first

        holder.specialization.text = currentItem.specialization
        holder.bookButton.setOnClickListener {
            val intent = Intent(context, PatientAppointment::class.java)
            intent.putExtra("doctorName", currentItem.first)
            intent.putExtra("doctorId",currentItem.doctorId)
            intent.putExtra("patientId", patientId)
            intent.putExtra("patientName", patientName)

            if (context != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(holder.itemView.context, "Context is null", Toast.LENGTH_SHORT).show()
            }
        }




    }

    override fun getItemCount(): Int {

        return doctorList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val first : TextView = itemView.findViewById(R.id.name)

        val specialization : TextView = itemView.findViewById(R.id.specialization)
        val bookButton: Button = itemView.findViewById(R.id.bookButton)
    }

}