package com.example.albergue_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.albergue_android.R
import com.example.albergue_android.domain.models.Comment
import java.text.SimpleDateFormat
import java.util.Locale

class CommentsAdapter(
    private var comments: List<Comment>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_admin, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.tvComment.text = comment.comment
        holder.tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(comment.createdAt)

        holder.btnDelete.setOnClickListener {
            println("ida " + comment.id)
            comment.id?.let { id ->
                onDeleteClick(id)
            } ?: showError(holder.itemView.context, "Comentario no tiene ID válido")
        }
    }

    override fun getItemCount(): Int = comments.size

    private fun showError(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun updateData(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }
}