package com.example.albergue_android.ui.components

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.StudentEnrollmentResponse
import com.itextpdf.kernel.pdf.PdfDocument
//import com.itextpdf.kernel.pdf.PdfName.Intent
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.example.albergue_android.domain.models.Comment
import com.example.albergue_android.domain.models.StudentDocDocument
import com.example.albergue_android.domain.models.StudentDocument
import com.example.albergue_android.ui.adapters.DocumentAdapter
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.UnitValue
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Paso3Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Paso3Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var tvWelcome: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var btnPdf: Button
    private lateinit var aspiranteId: String
    private var currentDocument: StudentDocument? = null
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var studentAdapter: DocumentAdapter
    private lateinit var tutorAdapter: DocumentAdapter
    private val comments = mutableListOf<Comment>()


    private var isAccepted: Boolean = false
    private var studentName: String = "Estudiante"
    private var currentEnrollmentPeriod: String = "este periodo"
    private lateinit var instructionsLayout: LinearLayout
    private var aspiranteData: StudentEnrollmentResponse? = null
    val studentDocumentsDefault = listOf(
        StudentDocument("", "Acta de nacimiento", "", Date(), "pending", "Acta de nacimiento"),
        StudentDocument("", "Ultima boleta de calificaciones", "", Date(), "pending", "Última boleta de calificaciones"),
        StudentDocument("", "Cartilla de vacunación", "", Date(), "pending", "Cartilla de vacunación"),
        StudentDocument("", "CURP", "", Date(), "pending", "CURP (Actualizada)"),
        StudentDocument("", "Comprobante de domicilio", "", Date(), "pending", "Comprobante de domicilio"),
        StudentDocument("", "Constancia de identidad", "", Date(), "pending", "Constancia de identidad"),
        StudentDocument("", "Certificado médico", "", Date(), "pending", "Certificado médico")
    )

    val tutorDocumentsDefault = listOf(
        StudentDocument("", "CURP_tutor", "", Date(), "pending", "CURP"),
        StudentDocument("", "Comprobante de domicilio_tutor", "", Date(), "pending", "Comprobante de domicilio"),
        StudentDocument("", "Credencial del lector_tutor", "", Date(), "pending", "Credencial del lector"),
        StudentDocument("", "Constancia del último grado de estudio_tutor", "", Date(), "pending", "Constancia del último grado de estudio")
    )
    val statusTranslation = mapOf(
        "pending" to "Pendiente",
        "uploaded" to "Subido",
        "approved" to "Aprobado",
        "rejected" to "Rechazado"
    )
        private lateinit var adapter: DocumentAdapter
    private val studentDocuments = mutableListOf<StudentDocument>()
    private val tutorDocuments = mutableListOf<StudentDocument>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_paso3, container, false)

        aspiranteId = obtenerAspiranteId()

        tvWelcome = view.findViewById(R.id.tvWelcome)
        tvInstructions = view.findViewById(R.id.tvInstructions)
        btnPdf = view.findViewById(R.id.btnPdf)
        instructionsLayout = view.findViewById(R.id.instructionsLayout)
        lottieLoading = view.findViewById(R.id.lottieLoading)
        val loadingOverlay = view.findViewById<FrameLayout>(R.id.loadingOverlay)

        getEnrollmentForm(aspiranteId)

        // Configurar RecyclerView para documentos del estudiante
        val rvStudentDocuments = view.findViewById<RecyclerView>(R.id.rvStudentDocuments)
        rvStudentDocuments.layoutManager = LinearLayoutManager(requireContext())
        studentAdapter = DocumentAdapter(
            studentDocuments,
            statusTranslation,
            onViewClick = { document -> viewFile(document) },
            onEditClick = { document -> editFile(document) },
            onDeleteClick = { document -> deleteFile(document) },
            onUploadClick = { document -> uploadFile(document) }
        )
        rvStudentDocuments.adapter = studentAdapter

        // Configurar RecyclerView para documentos del tutor
        val rvTutorDocuments = view.findViewById<RecyclerView>(R.id.rvTutorDocuments)
        rvTutorDocuments.layoutManager = LinearLayoutManager(requireContext())
        tutorAdapter = DocumentAdapter(
            tutorDocuments,
            statusTranslation,
            onViewClick = { document -> viewFile(document) },
            onEditClick = { document -> editFile(document) },
            onDeleteClick = { document -> deleteFile(document) },
            onUploadClick = {  document -> uploadFile(document) }
        )
        rvTutorDocuments.adapter = tutorAdapter

        // Obtener documentos del backend
        // Obtener documentos del backend
        getBackendDocuments { backendDocuments ->
            // Filtrar documentos del estudiante (excluyendo los del tutor y "Solicitud de Ingreso")
            val studentDocumentsFiltered = backendDocuments.filter {
                !it.type.contains("_tutor") && it.type != "Solicitud Ingreso"
            }

            // Combinar con los documentos por defecto del estudiante
            studentDocuments.addAll(combineDocuments(studentDocumentsDefault, studentDocumentsFiltered))

            // Filtrar documentos del tutor
            val tutorDocumentsFiltered = backendDocuments.filter {
                it.type.contains("_tutor")
            }

            // Combinar con los documentos por defecto del tutor
            tutorDocuments.addAll(combineDocuments(tutorDocumentsDefault, tutorDocumentsFiltered))

            // Notificar a ambos adapters
            studentAdapter.notifyDataSetChanged()
            tutorAdapter.notifyDataSetChanged()
        }

        // Verificar permisos
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            checkPermissions()
        }

        // Configurar el clic del botón PDF
        btnPdf.setOnClickListener {
            generatePDF(requireContext(), aspiranteData)
        }

        getComments()

        return view

    }



    private fun generatePDF(context: Context, aspiranteData: StudentEnrollmentResponse?) {
        try {
            // Guardar en el almacenamiento privado de la app
            val pdfDir = File(context.getExternalFilesDir(null), "PDFs")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }

            val file = File(pdfDir, "formulario.pdf")

            // Crear el archivo PDF
            val outputStream = FileOutputStream(file)
            val writer = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            // Configurar el documento en tamaño A4
            document.setMargins(20f, 20f, 20f, 20f)

            // Agregar contenido al PDF
            aspiranteData?.data?.let { data ->
                // Título
                document.add(
                    Paragraph("Formulario de Inscripción")
                        .setFontSize(18f)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                )

                // Encabezado
                val header = Table(UnitValue.createPercentArray(floatArrayOf(100f))).useAllAvailableWidth()
                header.addCell(
                    Cell().add(Paragraph("gob.mx"))
                        .setBackgroundColor(DeviceRgb(153, 0, 51)) // Color #990033
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setPadding(5f)
                )
                document.add(header)

                // Tabla de datos generales
                val generalDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                generalDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Instituto Nacional de los Pueblos Indígenas"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                generalDataTable.addCell(
                    Cell().add(Paragraph("Anexo 1."))
                        .setTextAlignment(TextAlignment.CENTER)
                )
                generalDataTable.addCell(
                    Cell().add(Paragraph("Solicitud de Ingreso a la Casa o Comedor"))
                        .setTextAlignment(TextAlignment.CENTER)
                )
                generalDataTable.addCell(
                    Cell().add(Paragraph("(Solicitantes)"))
                        .setTextAlignment(TextAlignment.CENTER)
                )
                document.add(generalDataTable)

                // Tabla de fechas
                val dateTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                dateTable.addCell(
                    Cell().add(Paragraph("Fecha de publicación del formato en el DOF"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                dateTable.addCell(
                    Cell().add(Paragraph("Fecha de solicitud del trámite"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                dateTable.addCell(Cell().add(Paragraph("{{DD}}")))
                dateTable.addCell(Cell().add(Paragraph("{{MM}}")))
                dateTable.addCell(Cell().add(Paragraph("{{AAAA}}")))
                document.add(dateTable)

                // Tabla de datos personales
                val personalDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                personalDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos generales del solicitante"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                personalDataTable.addCell(
                    Cell().add(Paragraph("Información personal"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                personalDataTable.addCell(
                    Cell().add(Paragraph("Pueblo indígena al que pertenece:"))
                        .setTextAlignment(TextAlignment.CENTER)
                )
                personalDataTable.addCell(Cell().add(Paragraph("CURP:")))
                personalDataTable.addCell(Cell().add(Paragraph(data.curp)))
                personalDataTable.addCell(Cell().add(Paragraph("Lengua indígena que habla: ${data.lenguaIndigena}")))
                personalDataTable.addCell(Cell().add(Paragraph("Fecha de nacimiento: ${data.fechaNacimiento}")))
                personalDataTable.addCell(Cell().add(Paragraph("Nombre(s): ${data.nombre}")))
                personalDataTable.addCell(Cell().add(Paragraph("Primer apellido: ${data.primerApellido}")))
                personalDataTable.addCell(Cell().add(Paragraph("Segundo apellido: ${data.segundoApellido}")))
                personalDataTable.addCell(Cell().add(Paragraph("Sexo: ${data.sexo}")))
                personalDataTable.addCell(Cell().add(Paragraph("Teléfono Fijo: ${data.telefonoFijo}")))
                personalDataTable.addCell(Cell().add(Paragraph("Teléfono Móvil: ${data.telefonoMovil}")))
                personalDataTable.addCell(Cell().add(Paragraph("Correo Electrónico: ${data.correoElectronico}")))
                document.add(personalDataTable)

                // Datos de la madre
                val motherDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                motherDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos generales de la madre"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                motherDataTable.addCell(Cell().add(Paragraph("CURP Madre: ${data.curpMadre}")))
                motherDataTable.addCell(Cell().add(Paragraph("Nombre Madre: ${data.nombreMadre}")))
                motherDataTable.addCell(Cell().add(Paragraph("Primer Apellido Madre: ${data.primerApellidoMadre}")))
                motherDataTable.addCell(Cell().add(Paragraph("Segundo Apellido Madre: ${data.segundoApellidoMadre}")))
                motherDataTable.addCell(Cell().add(Paragraph("Fecha de Nacimiento Madre: ${data.fechaNacimientoMadre}")))
                motherDataTable.addCell(Cell().add(Paragraph("Estado de Nacimiento Madre: ${data.edoNacimientoMadre}")))
                document.add(motherDataTable)

// Datos del padre
                val fatherDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                fatherDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos generales del padre"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                fatherDataTable.addCell(Cell().add(Paragraph("CURP Padre: ${data.curpPadre}")))
                fatherDataTable.addCell(Cell().add(Paragraph("Nombre Padre: ${data.nombrePadre}")))
                fatherDataTable.addCell(Cell().add(Paragraph("Primer Apellido Padre: ${data.primerApellidoPadre}")))
                fatherDataTable.addCell(Cell().add(Paragraph("Segundo Apellido Padre: ${data.segundoApellidoPadre}")))
                fatherDataTable.addCell(Cell().add(Paragraph("Fecha de Nacimiento Padre: ${data.fechaNacimientoPadre}")))
                fatherDataTable.addCell(Cell().add(Paragraph("Estado de Nacimiento Padre: ${data.edoNacimientoPadre}")))
                document.add(fatherDataTable)

// Datos del tutor
                val tutorDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                tutorDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos generales del tutor"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                tutorDataTable.addCell(Cell().add(Paragraph("Nombre Tutor: ${data.nombreTutor}")))
                tutorDataTable.addCell(Cell().add(Paragraph("CURP Tutor: ${data.curpTutor}")))
                tutorDataTable.addCell(Cell().add(Paragraph("Parentesco Tutor: ${data.parentescoTutor}")))
                tutorDataTable.addCell(Cell().add(Paragraph("Primer Apellido Tutor: ${data.primerApellidoTutor}")))
                tutorDataTable.addCell(Cell().add(Paragraph("Segundo Apellido Tutor: ${data.segundoApellidoTutor}")))
                tutorDataTable.addCell(Cell().add(Paragraph("Fecha de Nacimiento Tutor: ${data.fechaNacimientoTutor}")))
                tutorDataTable.addCell(Cell().add(Paragraph("Estado de Nacimiento Tutor: ${data.edoNacimientoTutor}")))
                document.add(tutorDataTable)

// Datos de la casa comedor
                val houseDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                houseDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos de la casa comedor"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                houseDataTable.addCell(Cell().add(Paragraph("Comunidad Casa: ${data.comunidadCasa}")))
                houseDataTable.addCell(Cell().add(Paragraph("Localidad Casa: ${data.localidadCasa}")))
                houseDataTable.addCell(Cell().add(Paragraph("Centro Coordinador: ${data.centroCoordinador}")))
                houseDataTable.addCell(Cell().add(Paragraph("Nombre Casa: ${data.nombreCasa}")))
                document.add(houseDataTable)

// Datos académicos
                val academicDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                academicDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos académicos"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                academicDataTable.addCell(Cell().add(Paragraph("CCT: ${data.cct}")))
                academicDataTable.addCell(Cell().add(Paragraph("Nombre Escuela: ${data.nombreEscuela}")))
                academicDataTable.addCell(Cell().add(Paragraph("Alergia Detalles: ${data.alergiaDetalles}")))
                academicDataTable.addCell(Cell().add(Paragraph("Respirar Detalles: ${data.respirarDetalles}")))
                academicDataTable.addCell(Cell().add(Paragraph("Tratamiento Detalles: ${data.tratamientoDetalles}")))
                document.add(academicDataTable)

                // Datos de la casa comedor (continuación)
                houseDataTable.addCell(Cell().add(Paragraph("Tipo de Casa: ${data.tipoCasa}")))
                houseDataTable.addCell(Cell().add(Paragraph("Medio de Acceso: ${data.medioAcceso}")))
                houseDataTable.addCell(Cell().add(Paragraph("Especificación de Acceso: ${data.especifAcceso ?: "N/A"}")))
                houseDataTable.addCell(Cell().add(Paragraph("Riesgo de Acceso: ${data.riesgoAcceso}")))
                houseDataTable.addCell(Cell().add(Paragraph("Especificación de Riesgo: ${data.especifRiesgo ?: "N/A"}")))

// Datos de salud
                val healthDataTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
                healthDataTable.addCell(
                    Cell(1, 2).add(Paragraph("Datos de salud"))
                        .setBackgroundColor(DeviceRgb(217, 217, 217)) // Color #d9d9d9
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                )
                healthDataTable.addCell(Cell().add(Paragraph("Discapacidad: ${data.discapacidad}")))
                healthDataTable.addCell(Cell().add(Paragraph("Tipo de Discapacidad: ${data.tipoDiscapacidad}")))
                healthDataTable.addCell(Cell().add(Paragraph("Especificación de Discapacidad: ${data.especifDiscapacidad ?: "N/A"}")))
                healthDataTable.addCell(Cell().add(Paragraph("Alergia: ${data.alergia}")))
                healthDataTable.addCell(Cell().add(Paragraph("Detalles de Alergia: ${data.alergiaDetalles ?: "N/A"}")))
                healthDataTable.addCell(Cell().add(Paragraph("Problemas para Respirar: ${data.respirar}")))
                healthDataTable.addCell(Cell().add(Paragraph("Detalles de Problemas Respiratorios: ${data.respirarDetalles ?: "N/A"}")))
                healthDataTable.addCell(Cell().add(Paragraph("Tratamiento Médico: ${data.tratamiento}")))
                healthDataTable.addCell(Cell().add(Paragraph("Detalles del Tratamiento: ${data.tratamientoDetalles ?: "N/A"}")))
                document.add(healthDataTable)

// Datos académicos (continuación)
                academicDataTable.addCell(Cell().add(Paragraph("Tipo de Escuela: ${data.tipoEscuela}")))
                academicDataTable.addCell(Cell().add(Paragraph("Escolaridad: ${data.escolaridad}")))
                academicDataTable.addCell(Cell().add(Paragraph("Otra Escolaridad: ${data.otraesco ?: "N/A"}")))
                academicDataTable.addCell(Cell().add(Paragraph("Semestres o Años Cursados: ${data.semestreoanosCursados}")))
                academicDataTable.addCell(Cell().add(Paragraph("Tipo de Curso: ${data.tipoCurso}")))
                academicDataTable.addCell(Cell().add(Paragraph("Solicitud: ${data.solicitud}")))


                // Pie de página
                document.add(
                    Paragraph("Este programa es ajeno a cualquier partido político. Queda prohibido el uso para fines distintos a los establecidos en el programa.")
                        .setFontSize(8f)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            }

            // Cerrar el documento
            document.close()

            // Notificar al usuario que el PDF se ha guardado
            Toast.makeText(context, "PDF guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()

            // Abrir el PDF generado
            openPDF(context, file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openPDF(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooser = Intent.createChooser(intent, "Abrir PDF con...")

        try {
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No hay una aplicación para abrir PDFs", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = mutableListOf<String>()
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions.toTypedArray(),
                    100
                )
            }
        }
    }

    private fun getEnrollmentForm(aspiranteId: String) {
        val service = ApiClient.studentEnrollmentService
        service.getById(aspiranteId).enqueue(object : Callback<StudentEnrollmentResponse> {
            override fun onResponse(
                call: Call<StudentEnrollmentResponse>,
                response: Response<StudentEnrollmentResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val enrollmentResponse = response.body()!!
                    if (!enrollmentResponse.error) {
                        println("EstudianteName :" + enrollmentResponse.data?.nombre)

                        studentName = enrollmentResponse.data?.nombre ?: "Estudiante"
                        updateUI(studentName)
                        aspiranteData = enrollmentResponse
                        println("Aspirante data : $aspiranteData")
                    } else {
                        println("Error: ${enrollmentResponse.msg}")
                    }
                } else {
                    println("Error en la llamada al servicio: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<StudentEnrollmentResponse>, t: Throwable) {
                println("Error de conexión: ${t.message}")
            }
        })
    }

    private fun getBackendDocuments(callback: (List<StudentDocument>) -> Unit) {
        val aspiranteId = obtenerAspiranteId()

        // Hacer la llamada al backend
        ApiClient.studentDocService.getById(aspiranteId).enqueue(object : Callback<List<StudentDocDocument>> {
            override fun onResponse(
                call: Call<List<StudentDocDocument>>,
                response: Response<List<StudentDocDocument>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val studentDocs = response.body()!!

                    // Verificar si la lista no está vacía
                    if (studentDocs.isNotEmpty()) {
                        val studentDoc = studentDocs[0] // Tomar el primer elemento de la lista
                        println("Student Doc primer if ${studentDoc.Documents}")

                        // Obtener el estado de inscripción
                        val enrollmentStatus = studentDoc.enrollmentStatus
                        updateEnrollmentStatus(enrollmentStatus)


                        // Verificar si hay documentos
                        if (studentDoc.Documents != null && studentDoc.Documents.isNotEmpty()) {
                            // Filtrar documentos del estudiante (excluyendo los del tutor y "Solicitud de Ingreso")
                            val studentDocuments = studentDoc.Documents.filter {
                                !it.type.contains("_tutor") && it.type != "Solicitud Ingreso"
                            }

                            // Filtrar documentos del tutor
                            val tutorDocuments = studentDoc.Documents.filter {
                                it.type.contains("_tutor")
                            }

                            // Combinar con los documentos por defecto del estudiante
                            val combinedStudentDocuments = combineDocuments(studentDocumentsDefault, studentDocuments)
                            val combinedTutorDocuments = combineDocuments(tutorDocumentsDefault, tutorDocuments)

                            // Llamar al callback con los documentos combinados
                            callback(combinedStudentDocuments + combinedTutorDocuments)
                            println("Documentos de estudiante combinados $combinedStudentDocuments")
                            println("Documentos de tutor combinados $combinedTutorDocuments")
                        } else {
                            // Si no hay documentos, devolver los documentos por defecto
                            callback(studentDocumentsDefault + tutorDocumentsDefault)
                            println("Entro al else default documents ${studentDocumentsDefault + tutorDocumentsDefault}")
                        }
                    } else {
                        // Si la lista está vacía
                        showErrorDialog("No se encontraron datos del estudiante.")
                        callback(studentDocumentsDefault + tutorDocumentsDefault)
                    }
                } else {
                    // Manejar el error si la respuesta no es exitosa
//                    showErrorDialog("Error al obtener los documentos: ${response.message()}")
                    println("Error al obtener documentos: $response")
                    callback(studentDocumentsDefault + tutorDocumentsDefault)
                }
            }

            override fun onFailure(call: Call<List<StudentDocDocument>>, t: Throwable) {
                // Manejar excepciones
                showErrorDialog("Error de conexión: ${t.message}")
                callback(studentDocumentsDefault + tutorDocumentsDefault)
            }
        })
    }

    fun updateEnrollmentStatus(status: Boolean) {
        isAccepted = status
        updateUI(studentName)
    }

    private fun updateUI(studentName: String) {
        // Cambiar el texto del saludo
        tvWelcome.text = "Bienvenido, $studentName"

        // Cambiar color de texto e instrucciones si está aceptado
        if (isAccepted) {
            tvWelcome.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

            instructionsLayout.setBackgroundResource(R.drawable.instructions_background_green)
            instructionsLayout.setPadding(22, 22, 22, 22)

            btnPdf.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green))
        }
    }

    private fun obtenerAspiranteId(): String {
        val sharedPreferences = requireContext().getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_ID", "0eNvExukbTSRglFVkSzH") ?: "0eNvExukbTSRglFVkSzH"
    }

    private fun combineDocuments(
        defaultDocuments: List<StudentDocument>,
        backendDocuments: List<StudentDocument>
    ): List<StudentDocument> {
        return defaultDocuments.map { defaultDoc ->
            val foundDoc = backendDocuments.find { it.type == defaultDoc.type }
            if (foundDoc != null) {
                // Conservar el displayName del documento por defecto
                foundDoc.copy(displayName = defaultDoc.displayName)
            } else {
                defaultDoc
            }
        }
    }

    fun getBadgeClass(status: String): Int {
        return when (status) {
            "pending" -> R.drawable.badge_pending
            "uploaded" -> R.drawable.badge_uploaded
            "approved" -> R.drawable.badge_approved
            "rejected" -> R.drawable.badge_rejected
            else -> R.drawable.badge_default
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_PDF && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    // Obtener el nombre del archivo desde el Uri
                    val fileName = getFileNameFromUri(uri)

                    // Obtener un InputStream del Uri
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        // Crear un archivo temporal
                        val file = createTempFile("temp_pdf", ".pdf", requireContext().cacheDir)
                        file.outputStream().use { fileStream ->
                            stream.copyTo(fileStream)
                        }

                        // Verificar el tamaño del archivo
                        if (file.length() <= 5 * 1024 * 1024) { // 5 MB
                            currentDocument?.let { document ->
                                if (document.status == "uploaded" || document.status == "rejected") {
                                    editFile(file, uri, document, fileName) // Pasar el nombre del archivo
                                } else {
                                    uploadFile(file, uri, document, fileName)
                                }
                            }
                        } else {
                            showErrorDialog("El archivo debe ser menor a 5MB.")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog("Error al leer el archivo: ${e.message}")
                }
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) { // Verificar si la columna existe
                    val displayName = it.getString(displayNameIndex)
                    if (!displayName.isNullOrEmpty()) {
                        fileName = displayName
                    }
                }
            }
        }

        if (fileName.isEmpty()) {
            fileName = uri.lastPathSegment ?: "document.pdf"
        }

        return fileName
    }

    private fun uploadFile(document: StudentDocument) {
        openFilePicker(document)
    }

    private fun uploadFile(file: File, uri: Uri, document: StudentDocument, fileName: String) {
        showLoading(true)

        val aspiranteId = obtenerAspiranteId()
        val requestFile = RequestBody.create(MediaType.parse("application/pdf"), file)
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile) // Usar fileName
        val aspiranteIdBody = RequestBody.create(MediaType.parse("text/plain"), aspiranteId)
        val documentTypeBody = RequestBody.create(MediaType.parse("text/plain"), document.type)
        val documentNameBody = RequestBody.create(MediaType.parse("text/plain"), fileName) // Usar fileName

        // Verificar los valores reales
        println("Vamos a ver lo que le falta:")
        println("aspiranteId: $aspiranteId")
        println("documentType: ${document.type}")
        println("documentName: $fileName") // Usar fileName

        ApiClient.studentDocService.uploadFile(aspiranteIdBody, filePart, documentTypeBody, documentNameBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        showSuccessDialog("Archivo subido correctamente")

                        println("Sí se subió")

                        // Refrescar la lista de documentos
                        getBackendDocuments { backendDocuments ->
                            studentDocuments.clear()
                            tutorDocuments.clear()

                            // Filtrar y combinar documentos
                            val studentDocumentsFiltered = backendDocuments.filter {
                                !it.type.contains("_tutor") && it.type != "Solicitud Ingreso"
                            }
                            studentDocuments.addAll(
                                combineDocuments(
                                    studentDocumentsDefault,
                                    studentDocumentsFiltered
                                )
                            )

                            val tutorDocumentsFiltered = backendDocuments.filter {
                                it.type.contains("_tutor")
                            }
                            tutorDocuments.addAll(
                                combineDocuments(
                                    tutorDocumentsDefault,
                                    tutorDocumentsFiltered
                                )
                            )

                            // Notificar a los adapters
                            studentAdapter.notifyDataSetChanged()
                            tutorAdapter.notifyDataSetChanged()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        println("Error al subir el archivo: $errorBody")
                        val errorMessage = errorBody?.let {
                            try {
                                JSONObject(it).getString("message")
                            } catch (e: Exception) {
                                "Error al subir el archivo."
                            }
                        } ?: "Error al subir el archivo."
                        showErrorDialog(errorMessage)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showLoading(false)
                    showErrorDialog("Error de conexión: ${t.message}")
                    println("Estamos en uploadfile $t")
                }
            })
    }

    fun updateDocumentStatus(document: StudentDocument, newStatus: String) {
        if (::adapter.isInitialized) { // Verifica si el adapter está inicializado
            val index = studentDocuments.indexOfFirst { it.type == document.type }
            if (index != -1) {
                studentDocuments[index].status = newStatus
                adapter.notifyItemChanged(index)
            }
        }
    }

    private fun viewFile(document: StudentDocument) {
        val link = document.link

        if (link.isNullOrEmpty()) {
            showErrorDialog("El documento no tiene un enlace válido.")
            return
        }

        // Crear un Intent para abrir el enlace sin forzar un tipo MIME específico
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Asegura que el selector se abra correctamente
        }

        // Crear un chooser para mostrar una lista de aplicaciones disponibles
        val chooser = Intent.createChooser(intent, "Abrir con...")

        try {
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            showErrorDialog("No hay una aplicación para abrir este enlace.")
        }
    }

    private fun editFile(document: StudentDocument) {
        openFilePicker(document)
    }

    private fun editFile(file: File, uri: Uri, document: StudentDocument, fileName: String) {
        showLoading(true)

        val aspiranteId = obtenerAspiranteId()
        val requestFile = RequestBody.create(MediaType.parse("application/pdf"), file)
        val filePart = MultipartBody.Part.createFormData("document", fileName, requestFile)  // Cambiar el nombre a "document"
        val aspiranteIdBody = RequestBody.create(MediaType.parse("text/plain"), aspiranteId)
        val documentTypeBody = RequestBody.create(MediaType.parse("text/plain"), document.type)
        val documentNameBody = RequestBody.create(MediaType.parse("text/plain"), fileName)

        ApiClient.studentDocService.editFile(aspiranteIdBody, filePart, documentTypeBody, documentNameBody)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        showSuccessDialog("Archivo editado correctamente")

                        // Refrescar la lista de documentos
                        getBackendDocuments { backendDocuments ->
                            studentDocuments.clear()
                            tutorDocuments.clear()

                            // Filtrar y combinar documentos
                            val studentDocumentsFiltered = backendDocuments.filter {
                                !it.type.contains("_tutor") && it.type != "Solicitud Ingreso"
                            }
                            studentDocuments.addAll(
                                combineDocuments(
                                    studentDocumentsDefault,
                                    studentDocumentsFiltered
                                )
                            )

                            val tutorDocumentsFiltered = backendDocuments.filter {
                                it.type.contains("_tutor")
                            }
                            tutorDocuments.addAll(
                                combineDocuments(
                                    tutorDocumentsDefault,
                                    tutorDocumentsFiltered
                                )
                            )

                            // Notificar a los adapters
                            studentAdapter.notifyDataSetChanged()
                            tutorAdapter.notifyDataSetChanged()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody?.let {
                            try {
                                JSONObject(it).getString("message")
                            } catch (e: Exception) {
                                "Error al editar el archivo."
                            }
                        } ?: "Error al editar el archivo."
                        showErrorDialog(errorMessage)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showLoading(false)
                    showErrorDialog("Error de conexión: ${t.message}")
                }
            })
    }

    private fun deleteFile(document: StudentDocument) {
        // Mostrar un diálogo de confirmación antes de eliminar
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Estás seguro?")
            .setContentText("Esta acción no se puede deshacer.")
            .setConfirmText("Sí")
            .setCancelText("Cancelar")
            .setConfirmClickListener { dialog ->
                dialog.dismiss()
                performDeleteFile(document)
            }
            .setCancelClickListener { dialog ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performDeleteFile(document: StudentDocument) {
        showLoading(true) // Mostrar el loading

        val aspiranteId = obtenerAspiranteId()
        val documentType = document.type

        // Llamar al servicio para eliminar el archivo
        ApiClient.studentDocService.deleteFile(aspiranteId, documentType)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    showLoading(false) // Ocultar el loading

                    if (response.isSuccessful) {
                        showSuccessDialog("Documento eliminado correctamente")

                        // Refrescar la lista de documentos
                        getBackendDocuments { backendDocuments ->
                            studentDocuments.clear()
                            tutorDocuments.clear()

                            // Filtrar y combinar documentos
                            val studentDocumentsFiltered = backendDocuments.filter {
                                !it.type.contains("_tutor") && it.type != "Solicitud Ingreso"
                            }
                            studentDocuments.addAll(
                                combineDocuments(
                                    studentDocumentsDefault,
                                    studentDocumentsFiltered
                                )
                            )

                            val tutorDocumentsFiltered = backendDocuments.filter {
                                it.type.contains("_tutor")
                            }
                            tutorDocuments.addAll(
                                combineDocuments(
                                    tutorDocumentsDefault,
                                    tutorDocumentsFiltered
                                )
                            )

                            // Notificar a los adapters
                            studentAdapter.notifyDataSetChanged()
                            tutorAdapter.notifyDataSetChanged()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = errorBody?.let {
                            try {
                                JSONObject(it).getString("message")
                            } catch (e: Exception) {
                                "Error al eliminar el documento."
                            }
                        } ?: "Error al eliminar el documento."
                        showErrorDialog(errorMessage)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showLoading(false) // Ocultar el loading
                    showErrorDialog("Error de conexión: ${t.message}")
                }
            })
    }

    private fun openFilePicker(document: StudentDocument) {
        currentDocument = document
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_PDF)
    }

    private fun getComments() {
        showLoading(true) // Mostrar el loading

        val aspiranteId = obtenerAspiranteId()

        // Llamar al servicio para obtener los comentarios
        ApiClient.studentDocService.getComments(aspiranteId)
            .enqueue(object : Callback<List<Comment>> {
                override fun onResponse(
                    call: Call<List<Comment>>,
                    response: Response<List<Comment>>
                ) {
                    showLoading(false) // Ocultar el loading

                    if (response.isSuccessful && response.body() != null) {
                        val commentsResponse = response.body()!!

                        // Verificar si hay comentarios
                        if (commentsResponse.isNotEmpty()) {
                            comments.clear()
                            comments.addAll(commentsResponse)
                            showComments() // Mostrar los comentarios en la UI
                        } else {
                            // No hay comentarios
                            hideComments()
                        }
                    } else {
//                        showErrorDialog("Error al obtener los comentarios.")
                        println("Error al obtener comentarios: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                    showLoading(false) // Ocultar el loading
                    showErrorDialog("Error de conexión: ${t.message}")
                }
            })
    }

    private fun showComments() {
        val commentsContainer = view?.findViewById<LinearLayout>(R.id.commentsContainer)


        // Eliminar solo los comentarios previos (manteniendo el título)
        if (commentsContainer?.childCount ?: 0 > 1) {
            commentsContainer?.removeViews(1, commentsContainer.childCount - 1)
        }

        // Agregar cada comentario
        for (comment in comments) {
            val commentView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_comment, commentsContainer, false)

            val tvComment = commentView.findViewById<TextView>(R.id.tvComment)
            tvComment.text = comment.comment

            val tvDate = commentView.findViewById<TextView>(R.id.tvDate)
            tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(comment.createdAt)

            commentsContainer?.addView(commentView)
        }

        commentsContainer?.visibility = View.VISIBLE
    }


    private fun hideComments() {
        val commentsContainer = view?.findViewById<LinearLayout>(R.id.commentsContainer)
        commentsContainer?.visibility = View.GONE
    }

    private fun showErrorDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog -> dialog.dismiss() }
            .show()
    }

    private fun showSuccessDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Éxito")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog -> dialog.dismiss() }
            .show()
    }

    private fun showLoading(show: Boolean) {
        val loadingOverlay = view?.findViewById<FrameLayout>(R.id.loadingOverlay)
        if (show) {
            loadingOverlay?.visibility = View.VISIBLE // Mostrar el overlay de carga
            lottieLoading?.playAnimation() // Iniciar la animación
        } else {
            loadingOverlay?.visibility = View.GONE // Ocultar el overlay de carga
            lottieLoading?.pauseAnimation() // Pausar la animación
        }
    }


    companion object {
        private const val REQUEST_CODE_PICK_PDF = 1001
    }
}