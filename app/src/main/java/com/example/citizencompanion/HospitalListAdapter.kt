package com.example.citizencompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.citizencompanion.objects.HospitalContactListItem
import kotlinx.android.synthetic.main.hospital_contact_list.view.*

class HospitalListAdapter(private val hospitalList: List<HospitalContactListItem>,
                          private val listener: OnItemClickListener): RecyclerView.Adapter<HospitalListAdapter.HospitalContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalContactViewHolder {
        // parent is the view holder this recyclerView will be placed in
        // context is the activity where out recyclerView will be placed in
        // attachToRoot false indicates that donot attach the view to recyclerView. It will be handled by RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hospital_contact_list, parent, false)

        return HospitalContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HospitalContactViewHolder, position: Int) {
        val currentItem = hospitalList[position]

        holder.hospitalNameView.text = currentItem.hospitalName
        holder.hospitalNumberView.text = currentItem.hostpitalContactNumber
    }

    override fun getItemCount() = hospitalList.size

    // ViewHolder represents single row of the list
    inner class HospitalContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val hospitalNameView = itemView.hospitalName
        val hospitalNumberView = itemView.hospitalNumber

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