package com.example.albergue_android.ui.documentsdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.StudentDocService
import com.example.albergue_android.domain.models.Comment
import com.example.albergue_android.domain.models.StudentDocument
import com.example.albergue_android.ui.adapters.CommentsAdapter
import com.example.albergue_android.ui.adapters.DocumentReviewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class DocumentsDialogFragment : DialogFragment() {

    private lateinit var studentName: String
    private var isEnrolled: Boolean = false
    private lateinit var studentDocuments: List<StudentDocument>
    private lateinit var tutorDocuments: List<StudentDocument>
    private lateinit var adapter: DocumentReviewAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private var comments = mutableListOf<Comment>()
    private var newComment = ""
    private lateinit var aspiranteId: String
    private var isStudentDocumentsExpanded = false
    private var isTutorDocumentsExpanded = false


    companion object {
        private const val ARG_STUDENT_NAME = "student_name"
        private const val ARG_IS_ENROLLED = "is_enrolled"
        private const val ARG_STUDENT_DOCS = "student_docs"
        private const val ARG_TUTOR_DOCS = "tutor_docs"
        private const val ARG_STUDENT_ID = "student_id"


        fun newInstance(
            studentName: String,
            isEnrolled: Boolean,
            studentDocuments: List<StudentDocument>,
            tutorDocuments: List<StudentDocument>,
            studentId: String
        ): DocumentsDialogFragment {
            val args = Bundle().apply {
                putString(ARG_STUDENT_NAME, studentName)
                putBoolean(ARG_IS_ENROLLED, isEnrolled)
                putSerializable(ARG_STUDENT_DOCS, ArrayList(studentDocuments))
                putSerializable(ARG_TUTOR_DOCS, ArrayList(tutorDocuments))
                putString(ARG_STUDENT_ID, studentId)
            }
            return DocumentsDialogFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentName = it.getString(ARG_STUDENT_NAME) ?: ""
            isEnrolled = it.getBoolean(ARG_IS_ENROLLED)
            studentDocuments = it.getSerializable(ARG_STUDENT_DOCS) as? List<StudentDocument> ?: emptyList()
            tutorDocuments = it.getSerializable(ARG_TUTOR_DOCS) as? List<StudentDocument> ?: emptyList()
            aspiranteId = it.getString(ARG_STUDENT_ID) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle("Documentos de $studentName")
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_documents_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar título y estado
        view.findViewById<TextView>(R.id.tvStudentName).text = "Documentos de $studentName"
        val statusBadge = view.findViewById<TextView>(R.id.tvStatusBadge)
        statusBadge.text = if (isEnrolled) "Inscrito" else "No inscrito"
        statusBadge.setBackgroundResource(
            if (isEnrolled) R.drawable.badge_background_green
            else R.drawable.badge_background_yellow
        )

        // Configurar clics en los encabezados del acordeón
        view.findViewById<View>(R.id.headerStudentDocuments).setOnClickListener {
            toggleAccordion(
                view.findViewById(R.id.contentStudentDocuments),
                view.findViewById(R.id.ivStudentArrow),
                true // Indica que es la sección del alumno
            )
        }

        view.findViewById<View>(R.id.headerTutorDocuments).setOnClickListener {
            toggleAccordion(
                view.findViewById(R.id.contentTutorDocuments),
                view.findViewById(R.id.ivTutorArrow),
                false // Indica que es la sección del tutor
            )
        }




        // Configurar RecyclerView de comentarios
        commentsAdapter = CommentsAdapter(emptyList()) { commentId ->
            deleteComment(commentId)
        }
        view.findViewById<RecyclerView>(R.id.rvComments).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentsAdapter
        }

        // Configurar campo de nuevo comentario
        view.findViewById<EditText>(R.id.etNewComment).addTextChangedListener {
            newComment = it.toString()
            view.findViewById<Button>(R.id.btnAddComment).isEnabled = newComment.isNotBlank()
        }

        // Configurar botón para agregar comentario
        view.findViewById<Button>(R.id.btnAddComment).setOnClickListener {
            addComment()
        }

        loadComments()

        // Configurar RecyclerViews
        val rvStudentDocuments = view.findViewById<RecyclerView>(R.id.rvStudentDocuments)
        val rvTutorDocuments = view.findViewById<RecyclerView>(R.id.rvTutorDocuments)

        rvStudentDocuments.layoutManager = LinearLayoutManager(requireContext())
        rvTutorDocuments.layoutManager = LinearLayoutManager(requireContext())

        // Combinar con documentos por defecto si es necesario
        val completeStudentDocuments = combineWithDefaultDocuments(studentDocuments, true)
        val completeTutorDocuments = combineWithDefaultDocuments(tutorDocuments, false)

        val studentAdapter = DocumentReviewAdapter(completeStudentDocuments, true) { document, newStatus ->
            updateDocumentStatus(document, newStatus)
        }

        val tutorAdapter = DocumentReviewAdapter(completeTutorDocuments, false) { document, newStatus ->
            updateDocumentStatus(document, newStatus)
        }

        rvStudentDocuments.adapter = studentAdapter
        rvTutorDocuments.adapter = tutorAdapter

        // Configurar botones
//        view.findViewById<View>(R.id.btnAcceptAll).setOnClickListener {
//            updateAllDocumentsStatus("approved")
//        }
//
//        view.findViewById<View>(R.id.btnRejectAll).setOnClickListener {
//            updateAllDocumentsStatus("rejected")
//        }
    }

//    fun toggleStudentDocuments(view: View) {
//        toggleAccordion(
//            view.findViewById(R.id.contentStudentDocuments),
//            view.findViewById<ImageView>(R.id.ivStudentArrow)
//        )
//    }
//
//    fun toggleTutorDocuments(view: View) {
//        toggleAccordion(
//            view.findViewById(R.id.contentTutorDocuments),
//            view.findViewById<ImageView>(R.id.ivTutorArrow)
//        )
//    }

    private fun updateDocumentStatus(document: StudentDocument, newStatus: String) {
        if (document.link.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Este documento no puede ser aprobado/rechazado", Toast.LENGTH_SHORT).show()
            return
        }


        println("Aspirante id ${aspiranteId} Lin ${document.link} status ${newStatus}")

        ApiClient.studentDocService.updateDocumentStatus(
            aspiranteId = aspiranteId,
            link = document.link,
            status = newStatus
        ).enqueue(object : Callback<StudentDocService.response> {
            override fun onResponse(
                call: Call<StudentDocService.response>,
                response: Response<StudentDocService.response>
            ) {
                if (response.isSuccessful) {
                    val successMessage = when (newStatus) {
                        "approved" -> "✅ Documento aprobado correctamente"
                        "rejected" -> "❌ Documento rechazado correctamente"
                        else -> "Estado actualizado"
                    }
                    Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show()
                } else {
                    println("Error ${response}" )
                    val errorMessage = when (response.code()) {
                        404 -> "Documento no encontrado"
                        400 -> "Solicitud inválida"
                        else -> "Error al actualizar estado (${response.code()})"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    revertStatus(document, newStatus)
                }
            }

            override fun onFailure(call: Call<StudentDocService.response>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error de red: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                revertStatus(document, newStatus)
            }
        })
    }

    private fun revertStatus(document: StudentDocument, attemptedStatus: String) {
        document.status = if (attemptedStatus == "approved") "rejected" else "approved"

        // Actualiza ambos adaptadores (o solo el que corresponda)
        view?.findViewById<RecyclerView>(R.id.rvStudentDocuments)?.adapter?.notifyDataSetChanged()
        view?.findViewById<RecyclerView>(R.id.rvTutorDocuments)?.adapter?.notifyDataSetChanged()
    }

    private fun toggleAccordion(content: LinearLayout, arrow: ImageView, isStudentSection: Boolean) {
        if (isStudentSection) {
            // Si estamos expandiendo la sección del alumno
            if (!isStudentDocumentsExpanded) {
                // Cerrar tutor si está abierto
                if (isTutorDocumentsExpanded) {
                    view?.findViewById<LinearLayout>(R.id.contentTutorDocuments)?.visibility = View.GONE
                    view?.findViewById<ImageView>(R.id.ivTutorArrow)?.setImageResource(R.drawable.ic_arrow_down)
                    isTutorDocumentsExpanded = false
                }
                // Abrir alumno
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
                isStudentDocumentsExpanded = true
            } else {
                // Cerrar alumno
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
                isStudentDocumentsExpanded = false
            }
        } else {
            // Si estamos expandiendo la sección del tutor
            if (!isTutorDocumentsExpanded) {
                // Cerrar alumno si está abierto
                if (isStudentDocumentsExpanded) {
                    view?.findViewById<LinearLayout>(R.id.contentStudentDocuments)?.visibility = View.GONE
                    view?.findViewById<ImageView>(R.id.ivStudentArrow)?.setImageResource(R.drawable.ic_arrow_down)
                    isStudentDocumentsExpanded = false
                }
                // Abrir tutor
                content.visibility = View.VISIBLE
                arrow.setImageResource(R.drawable.ic_arrow_up)
                isTutorDocumentsExpanded = true
            } else {
                // Cerrar tutor
                content.visibility = View.GONE
                arrow.setImageResource(R.drawable.ic_arrow_down)
                isTutorDocumentsExpanded = false
            }
        }
    }

    private fun combineWithDefaultDocuments(
        backendDocuments: List<StudentDocument>,
        isStudent: Boolean
    ): List<StudentDocument> {
        val defaultDocuments = if (isStudent) {
            listOf(
                StudentDocument("", "Acta de nacimiento", "", Date(), "pending", "Acta de nacimiento"),
                StudentDocument("", "Ultima boleta de calificaciones", "", Date(), "pending", "Última boleta"),
                StudentDocument("", "Cartilla de vacunación", "", Date(), "pending", "Cartilla vacunación"),
                StudentDocument("", "CURP", "", Date(), "pending", "CURP"),
                StudentDocument("", "Comprobante de domicilio", "", Date(), "pending", "Comprobante domicilio"),
                StudentDocument("", "Constancia de identidad", "", Date(), "pending", "Constancia identidad"),
                StudentDocument("", "Certificado médico", "", Date(), "pending", "Certificado médico")
            )
        } else {
            listOf(
                StudentDocument("", "CURP_tutor", "", Date(), "pending", "CURP tutor"),
                StudentDocument("", "Comprobante de domicilio_tutor", "", Date(), "pending", "Comprobante domicilio"),
                StudentDocument("", "Credencial del lector_tutor", "", Date(), "pending", "Credencial lector"),
                StudentDocument("", "Constancia del último grado de estudio_tutor", "", Date(), "pending", "Constancia estudios")
            )
        }

        return defaultDocuments.map { defaultDoc ->
            backendDocuments.find { it.type == defaultDoc.type }?.copy(
                displayName = defaultDoc.displayName
            ) ?: defaultDoc
        }
    }

    private fun loadComments() {
        if (aspiranteId.isEmpty()) {
            Toast.makeText(requireContext(), "ID de estudiante no válido", Toast.LENGTH_SHORT).show()
            return
        }


        ApiClient.studentDocService.getComments(aspiranteId).enqueue(
            object : Callback<List<Comment>> {
                override fun onResponse(
                    call: Call<List<Comment>>,
                    response: Response<List<Comment>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { commentsList ->
                            comments.clear()
                            comments.addAll(commentsList)
                            commentsAdapter.updateData(comments)
                        }
                    } else {
                            println("Error al cargar comentarios: ${response}");
                        Toast.makeText(
                            requireContext(),
                            "Error al cargar comentarios: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun addComment() {
        if (newComment.isBlank()) return

        ApiClient.studentDocService.addComment(
            aspiranteId = aspiranteId,
            text = newComment,
            createdBy = "Admin"
        ).enqueue(object : Callback<StudentDocService.response> {
            override fun onResponse(call: Call<StudentDocService.response>, response: Response<StudentDocService.response>) {
                if (response.isSuccessful) {
                    // Crear el nuevo comentario local
//                    val newCommentObj = Comment(
//                        id = null,
//                        comment = newComment,
//                        createdAt = Date(),
//                        createdBy = "Admin"
//                    )
//
//                    // Agregar a la lista y actualizar el adapter
//                    comments.add(newCommentObj)
//                    commentsAdapter.updateData(comments)

                    // Limpiar el campo de texto
                    view?.findViewById<EditText>(R.id.etNewComment)?.text?.clear()

                    loadComments();

//                    Toast.makeText(
//                        requireContext(),
//                        response.body()?.message ?: "Comentario agregado",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al agregar comentario: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<StudentDocService.response>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateAllDocumentsStatus(status: String) {

        // Crear el cuerpo de la petición
        val body = mapOf("status" to status)

        ApiClient.studentDocService.updateAllDocumentsStatus(aspiranteId, body).enqueue(
            object : Callback<StudentDocService.response> {
                override fun onResponse(
                    call: Call<StudentDocService.response>,
                    response: Response<StudentDocService.response>
                ) {
                    if (response.isSuccessful) {
                        val successMessage = when (status) {
                            "approved" -> "✅ Todos los documentos aprobados correctamente"
                            "rejected" -> "❌ Todos los documentos rechazados correctamente"
                            else -> "Estado de todos los documentos actualizado"
                        }
                        Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show()
                        updateLocalDocumentsStatus(status)
                        reloadDocuments()


                        // Actualizar la UI
                        view?.findViewById<RecyclerView>(R.id.rvStudentDocuments)?.adapter?.notifyDataSetChanged()
                        view?.findViewById<RecyclerView>(R.id.rvTutorDocuments)?.adapter?.notifyDataSetChanged()
                    } else {
                        val errorMessage = when (response.code()) {
                            404 -> "Estudiante no encontrado"
                            400 -> "Solicitud inválida"
                            else -> "Error al actualizar estados (${response.code()})"
                        }
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<StudentDocService.response>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error de red: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun reloadDocuments() {
        // Vuelve a cargar los documentos desde los argumentos
        arguments?.let {
            studentDocuments = it.getSerializable(ARG_STUDENT_DOCS) as? List<StudentDocument> ?: emptyList()
            tutorDocuments = it.getSerializable(ARG_TUTOR_DOCS) as? List<StudentDocument> ?: emptyList()
        }

        // Actualiza los adaptadores con los nuevos datos
        (view?.findViewById<RecyclerView>(R.id.rvStudentDocuments)?.adapter as? DocumentReviewAdapter)?.updateData(
            combineWithDefaultDocuments(studentDocuments, true)
        )

        (view?.findViewById<RecyclerView>(R.id.rvTutorDocuments)?.adapter as? DocumentReviewAdapter)?.updateData(
            combineWithDefaultDocuments(tutorDocuments, false)
        )
    }


    private fun updateLocalDocumentsStatus(newStatus: String) {
        // Actualizar documentos del estudiante
        studentDocuments = studentDocuments.map { doc ->
            if (doc.status == "uploaded") doc.copy(status = newStatus) else doc
        }

        // Actualizar documentos del tutor
        tutorDocuments = tutorDocuments.map { doc ->
            if (doc.status == "uploaded") doc.copy(status = newStatus) else doc
        }
    }

    private fun deleteComment(commentId: String) {
        println("Aspiranteid ${aspiranteId} y commentid ${commentId}")
        ApiClient.studentDocService.deleteComment(
            aspiranteId = commentId,
            commentId = aspiranteId
        ).enqueue(object : Callback<StudentDocService.response> {
            override fun onResponse(call: Call<StudentDocService.response>, response: Response<StudentDocService.response>) {
                if (response.isSuccessful) {
                    comments.removeAll { it.id == commentId }
                    commentsAdapter.updateData(comments)

//                    Toast.makeText(
//                        requireContext(),
//                        response.body()?.message ?: "Comentario eliminado",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    println("Error: ${response}")
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar comentario: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<StudentDocService.response>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}