package com.example.ehealthcareapp.doctor.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.models.Rating

class RatingAdapter(private val ratingList: List<Rating>) : RecyclerView.Adapter<RatingAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rating_data, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = ratingList[position]

        holder.patientNameTextView.text = currentItem.patientName
        holder.doctorNameTextView.text = currentItem.doctorName
        holder.ratingDescriptionTextView.text = currentItem.ratingDescription
        holder.bookingTime.text = currentItem.bookingTime
        holder.bookingDate.text = currentItem.bookingDate

        val ratingValue = currentItem.rating.toInt()
        setStarIcons(holder, ratingValue)
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }
     class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientNameTextView: TextView = itemView.findViewById(R.id.patientName)
        val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorName)
        val ratingDescriptionTextView: TextView = itemView.findViewById(R.id.review)
         val bookingTime :TextView=itemView.findViewById(R.id.bookingTime)
         val bookingDate :TextView=itemView.findViewById(R.id.bookingDate)
    }
    private fun setStarIcons(holder: MyViewHolder, ratingValue: Int) {
        val star1 = holder.itemView.findViewById<ImageView>(R.id.star1)
        val star2 = holder.itemView.findViewById<ImageView>(R.id.star2)
        val star3 = holder.itemView.findViewById<ImageView>(R.id.star3)
        val star4 = holder.itemView.findViewById<ImageView>(R.id.star4)
        val star5 = holder.itemView.findViewById<ImageView>(R.id.star5)

        val starFilled = R.drawable.ic_star_filled
        val starEmpty = R.drawable.ic_star_empty

        when (ratingValue) {
            1 -> {
                star1.setImageResource(starFilled)
                star2.setImageResource(starEmpty)
                star3.setImageResource(starEmpty)
                star4.setImageResource(starEmpty)
                star5.setImageResource(starEmpty)
            }
            2 -> {
                star1.setImageResource(starFilled)
                star2.setImageResource(starFilled)
                star3.setImageResource(starEmpty)
                star4.setImageResource(starEmpty)
                star5.setImageResource(starEmpty)
            }
            3 -> {
                star1.setImageResource(starFilled)
                star2.setImageResource(starFilled)
                star3.setImageResource(starFilled)
                star4.setImageResource(starEmpty)
                star5.setImageResource(starEmpty)
            }
            4 -> {
                star1.setImageResource(starFilled)
                star2.setImageResource(starFilled)
                star3.setImageResource(starFilled)
                star4.setImageResource(starFilled)
                star5.setImageResource(starEmpty)
            }
            5 -> {
                star1.setImageResource(starFilled)
                star2.setImageResource(starFilled)
                star3.setImageResource(starFilled)
                star4.setImageResource(starFilled)
                star5.setImageResource(starFilled)
            }
            else -> {
                star1.setImageResource(starEmpty)
                star2.setImageResource(starEmpty)
                star3.setImageResource(starEmpty)
                star4.setImageResource(starEmpty)
                star5.setImageResource(starEmpty)
            }
        }
    }
}
