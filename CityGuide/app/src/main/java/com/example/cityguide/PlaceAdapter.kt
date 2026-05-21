package com.example.cityguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaceAdapter(
    private var places: List<Place>,
    private val onClick: (Place) -> Unit,
    private val onLongClick: (View, Place) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.titleTextView.text = place.title
        holder.categoryTextView.text = place.category
        holder.descriptionTextView.text = place.description
        holder.imageView.setImageResource(place.imageResId)
        holder.itemView.setOnClickListener { onClick(place) }
        holder.itemView.setOnLongClickListener {
            onLongClick(it, place)
            true
        }
    }

    override fun getItemCount(): Int = places.size

    fun update(newPlaces: List<Place>) {
        places = newPlaces
        notifyDataSetChanged()
    }

    class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.placeImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.placeTitleTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.placeCategoryTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.placeDescriptionTextView)
    }
}
