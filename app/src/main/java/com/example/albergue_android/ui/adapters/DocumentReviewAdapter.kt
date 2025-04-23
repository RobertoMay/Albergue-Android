package com.example.albergue_android.ui.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.albergue_android.R
import com.example.albergue_android.domain.models.StudentDocument

class DocumentReviewAdapter(
    private var documents: List<StudentDocument>,
    private val isStudent: Boolean,
    private val onStatusUpdated: (document: StudentDocument, newStatus: String) -> Unit
) : RecyclerView.Adapter<DocumentReviewAdapter.DocumentViewHolder>() {

    private val statusTranslation = mapOf(
        "pending" to "Pendiente",
        "uploaded" to "Subido",
        "approved" to "Aprobado",
        "rejected" to "Rechazado"
    )

    private val statusBackground = mapOf(
        "pending" to R.drawable.badge_background_yellow,
        "uploaded" to R.drawable.badge_background_blue,
        "approved" to R.drawable.badge_background_green,
        "rejected" to R.drawable.badge_background_red
    )

    inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDocumentName: TextView = itemView.findViewById(R.id.tvDocumentName)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnView: Button = itemView.findViewById(R.id.btnView)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_review, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]

        // Mostrar nombre del documento (usamos displayName si está disponible)
        holder.tvDocumentName.text = document.displayName ?: document.type.replace("_", " ")

        // Configurar estado
        val status = document.status ?: "pending"
        holder.tvStatus.text = statusTranslation[status] ?: "Pendiente"
        holder.tvStatus.setBackgroundResource(statusBackground[status] ?: R.drawable.badge_background_yellow)

        // Configuración especial para el primer documento del estudiante (Solicitud de Ingreso)
        val isFirstStudentDoc = isStudent && position == 0

        // Mostrar/ocultar botones según el estado
        when (status) {
            "uploaded" -> {
                holder.btnView.visibility = if (isFirstStudentDoc) View.GONE else View.VISIBLE
                holder.btnAccept.visibility = View.VISIBLE
                holder.btnReject.visibility = View.VISIBLE
            }
            "approved" -> {
                holder.btnView.visibility = if (isFirstStudentDoc) View.GONE else View.VISIBLE
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.VISIBLE // Mostrar botón rechazar para documentos aprobados
            }
            "rejected" -> {
                holder.btnView.visibility = if (isFirstStudentDoc) View.GONE else View.VISIBLE
                holder.btnAccept.visibility = View.VISIBLE // Mostrar botón aceptar para documentos rechazados
                holder.btnReject.visibility = View.GONE
            }
            else -> { // pending
                holder.btnView.visibility = View.GONE
                holder.btnAccept.visibility = View.GONE
                holder.btnReject.visibility = View.GONE
            }
        }

        holder.btnAccept.isEnabled = status != "approved"
        holder.btnReject.isEnabled = status != "rejected"



        // Configurar estado de los botones Aceptar/Rechazar
        when (status) {
            "approved" -> {
                holder.btnAccept.isEnabled = false
                holder.btnReject.isEnabled = true
            }
            "rejected" -> {
                holder.btnAccept.isEnabled = true
                holder.btnReject.isEnabled = false
            }
            else -> { // pending, uploaded
                holder.btnAccept.isEnabled = true
                holder.btnReject.isEnabled = true
            }
        }

        // Configurar clics
        if (!isFirstStudentDoc) {
            holder.btnView.setOnClickListener {
                // Lógica para ver documento
                if (document.link.isNullOrEmpty()) {
                    Toast.makeText(holder.itemView.context,
                        "Documento no disponible",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // Abrir el documento
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(document.link))
                    try {
                        holder.itemView.context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(holder.itemView.context,
                            "No hay aplicación para abrir este documento",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        holder.btnAccept.setOnClickListener {
            // Actualizar estado localmente
            document.status = "approved"
            notifyItemChanged(position)

            // Llamar a la API para actualizar el estado
            onStatusUpdated(document, "approved")
        }

        holder.btnReject.setOnClickListener {
            // Actualizar estado localmente
            document.status = "rejected"
            notifyItemChanged(position)

            // Llamar a la API para actualizar el estado
            onStatusUpdated(document, "rejected")
        }
    }

    override fun getItemCount(): Int = documents.size

    fun updateData(newDocuments: List<StudentDocument>) {
        documents = newDocuments
        notifyDataSetChanged()
    }


}