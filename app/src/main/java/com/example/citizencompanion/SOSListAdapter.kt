package com.example.citizencompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.citizencompanion.objects.FIRListItem
import com.example.citizencompanion.objects.SOSListItem
import kotlinx.android.synthetic.main.fir_lists.view.*
import kotlinx.android.synthetic.main.sos_lists_police.view.*

class SOSListAdapter(private val sosList: List<SOSListItem>,
                     private val listener: OnItemClickListener): RecyclerView.Adapter<SOSListAdapter.SOSViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SOSViewHolder {
        // parent is the view holder this recyclerView will be placed in
        // context is the activity where out recyclerView will be placed in
        // attachToRoot false indicates that donot attach the view to recyclerView. It will be handled by RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sos_lists_police, parent, false)

        return SOSViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SOSViewHolder, position: Int) {
        val currentItem = sosList[position]

        holder.SOSTextView.text = currentItem.uidForSOS
        holder.SOSLongitude.text = currentItem.longitude
        holder.SOSLatitude.text = currentItem.latitude
    }

    override fun getItemCount() = sosList.size

    // ViewHolder represents single row of the list
    inner class SOSViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val SOSTextView = itemView.sosUId
        val SOSLongitude = itemView.sosLongitude
        val SOSLatitude = itemView.sosLatitude

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}