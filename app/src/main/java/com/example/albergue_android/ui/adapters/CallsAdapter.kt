package com.example.albergue_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.albergue_android.R
import androidx.recyclerview.widget.ListAdapter
import com.example.albergue_android.domain.models.Call

class CallsAdapter(
    private val onDeleteClick: (String) -> Unit // Lambda que recibe el id de la convocatoria
) : ListAdapter<Call, CallsAdapter.CallViewHolder>(CallDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_call, parent, false)
        return CallViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val call = getItem(position)
        holder.bind(call)
        holder.itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
            onDeleteClick(call.id ?: "") // Pasar el id de la convocatoria al lambda
        }
    }

    class CallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(call: Call) {
            itemView.findViewById<TextView>(R.id.callTitle).text = call.title
            val statusTextView = itemView.findViewById<TextView>(R.id.callStatus)
            statusTextView.text = if (call.status) "Abierta" else "Cerrada"
            statusTextView.setBackgroundResource(
                if (call.status) R.drawable.status_background_open else R.drawable.status_background_closed
            )
            itemView.findViewById<TextView>(R.id.callStartDate).text = "Fecha de inicio: ${call.startDate}"
            itemView.findViewById<TextView>(R.id.callEndDate).text = "Fecha de cierre: ${call.endDate}"
            itemView.findViewById<TextView>(R.id.callCupo).text = "Cupos: ${call.cupo}"
        }
    }

    class CallDiffCallback : DiffUtil.ItemCallback<Call>() {
        override fun areItemsTheSame(oldItem: Call, newItem: Call): Boolean {
            return oldItem.id == newItem.id // Comparar por id
        }

        override fun areContentsTheSame(oldItem: Call, newItem: Call): Boolean {
            return oldItem == newItem
        }
    }
}