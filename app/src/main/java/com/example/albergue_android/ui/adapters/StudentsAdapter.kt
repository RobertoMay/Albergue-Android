package com.example.albergue_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.albergue_android.R
import com.example.albergue_android.domain.models.StudentDocDocument

class StudentsAdapter(private var students: List<StudentDocDocument>, private val onDocumentsClick: (StudentDocDocument) -> Unit) :
    RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnViewDocuments: Button = itemView.findViewById(R.id.btnViewDocuments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.tvName.text = "${student.name} ${student.lastName1} ${student.lastName2}"
        holder.tvStatus.text = if (student.enrollmentStatus) "Inscrito" else "No inscrito"
        holder.btnViewDocuments.text = "Ver documentos" // Actualizado para usar el Button

        // Cambiar el color de fondo según el estado
        val statusBackground = when (student.enrollmentStatus) {
            true -> R.drawable.badge_background_success
            else -> R.drawable.badge_background
        }
        holder.tvStatus.setBackgroundResource(statusBackground)

        // Configurar el clic en "Ver documentos"
        holder.btnViewDocuments.setOnClickListener {
            onDocumentsClick(student)
        }
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newStudents: List<StudentDocDocument>) {
        students = newStudents
        notifyDataSetChanged()
    }
}

data class Student(
    val name: String,
    val status: String,
    val documents: String
)