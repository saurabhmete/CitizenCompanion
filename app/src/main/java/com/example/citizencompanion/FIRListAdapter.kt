package com.example.citizencompanion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.citizencompanion.objects.FIRListItem
import kotlinx.android.synthetic.main.fir_lists.view.*

class FIRListAdapter(private val fIRList: List<FIRListItem>,
                     private val listener: OnItemClickListener): RecyclerView.Adapter<FIRListAdapter.FIRViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FIRViewHolder {
        // parent is the view holder this recyclerView will be placed in
        // context is the activity where out recyclerView will be placed in
        // attachToRoot false indicates that donot attach the view to recyclerView. It will be handled by RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fir_lists, parent, false)

        return FIRViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FIRViewHolder, position: Int) {
        val currentItem = fIRList[position]

        holder.FIRTextView.text = currentItem.firId
    }

    override fun getItemCount() = fIRList.size

    // ViewHolder represents single row of the list
    inner class FIRViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val FIRTextView = itemView.firId

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