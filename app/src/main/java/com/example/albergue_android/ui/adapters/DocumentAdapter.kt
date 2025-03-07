package com.example.albergue_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.albergue_android.R
import com.example.albergue_android.domain.models.StudentDocument


class DocumentAdapter(
    private val documents: List<StudentDocument>,
    private val statusTranslation: Map<String, String>,
    private val onViewClick: (StudentDocument) -> Unit,
    private val onEditClick: (StudentDocument) -> Unit,
    private val onDeleteClick: (StudentDocument) -> Unit,
    private val onUploadClick: (StudentDocument) -> Unit
) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document, statusTranslation, onViewClick, onEditClick, onDeleteClick, onUploadClick)
    }

    override fun getItemCount(): Int = documents.size

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDocumentName: TextView = itemView.findViewById(R.id.tvDocumentName)
        private val tvDocumentStatus: TextView = itemView.findViewById(R.id.tvDocumentStatus)
        private val btnView: FrameLayout = itemView.findViewById(R.id.btnView)
        private val btnEdit: FrameLayout = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: FrameLayout = itemView.findViewById(R.id.btnDelete)
        private val btnUpload: Button = itemView.findViewById(R.id.btnUpload)

        fun bind(
            document: StudentDocument,
            statusTranslation: Map<String, String>,
            onViewClick: (StudentDocument) -> Unit,
            onEditClick: (StudentDocument) -> Unit,
            onDeleteClick: (StudentDocument) -> Unit,
            onUploadClick: (StudentDocument) -> Unit
        ) {
            // Usar displayName en lugar de type
            tvDocumentName.text = document.displayName
            tvDocumentStatus.text = statusTranslation[document.status] ?: document.status

            // Aplicar el fondo según el estado
            tvDocumentStatus.background = ContextCompat.getDrawable(
                itemView.context,
                getBadgeClass(document.status)
            )

            // Configurar visibilidad de los botones según el estado
            when (document.status) {
                "approved" -> {
                    btnView.visibility = View.VISIBLE
                    btnEdit.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnUpload.visibility = View.GONE
                }
                "rejected", "uploaded" -> {
                    btnView.visibility = View.VISIBLE
                    btnEdit.visibility = View.VISIBLE
                    btnDelete.visibility = View.VISIBLE
                    btnUpload.visibility = View.GONE
                }
                "pending" -> {
                    btnView.visibility = View.GONE
                    btnEdit.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnUpload.visibility = View.VISIBLE
                }
                else -> {
                    btnView.visibility = View.GONE
                    btnEdit.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                    btnUpload.visibility = View.GONE
                }
            }

            // Configurar acciones de los botones
            btnView.setOnClickListener { onViewClick(document) }
            btnEdit.setOnClickListener { onEditClick(document) }
            btnDelete.setOnClickListener { onDeleteClick(document) }
            btnUpload.setOnClickListener { onUploadClick(document) }
        }

        private fun getBadgeClass(status: String): Int {
            return when (status) {
                "pending" -> R.drawable.badge_pending
                "uploaded" -> R.drawable.badge_uploaded
                "approved" -> R.drawable.badge_approved
                "rejected" -> R.drawable.badge_rejected
                else -> R.drawable.badge_default
            }
        }
    }
}