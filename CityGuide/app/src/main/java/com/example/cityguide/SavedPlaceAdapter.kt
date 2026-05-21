package com.example.cityguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedPlaceAdapter(
    private var savedPlaces: List<SavedPlace>,
    private val onClick: (SavedPlace) -> Unit
) : RecyclerView.Adapter<SavedPlaceAdapter.SavedPlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saved_place, parent, false)
        return SavedPlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedPlaceViewHolder, position: Int) {
        val savedPlace = savedPlaces[position]
        holder.titleTextView.text = savedPlace.placeName
        holder.favoriteTextView.text = "В избранном"
        holder.itemView.setOnClickListener { onClick(savedPlace) }
    }

    override fun getItemCount(): Int = savedPlaces.size

    fun update(newSavedPlaces: List<SavedPlace>) {
        savedPlaces = newSavedPlaces
        notifyDataSetChanged()
    }

    class SavedPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.savedTitleTextView)
        val favoriteTextView: TextView = itemView.findViewById(R.id.savedFavoriteTextView)
    }
}
