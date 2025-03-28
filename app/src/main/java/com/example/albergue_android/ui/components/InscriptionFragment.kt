    package com.example.albergue_android.ui.components

import android.content.Context
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.DataStudentService
import com.example.albergue_android.data.network.StudentEnrollmentResponse
import com.example.albergue_android.data.network.StudentResponse
import com.example.albergue_android.domain.models.DataStudent
import com.example.albergue_android.domain.models.StudentData
import com.example.albergue_android.domain.models.StudentDocDocument
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

    // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InscriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InscriptionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var datosPaso1: Map<String, String>? = null
    private var datosPaso2: Map<String, String>? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var stepNumber1: TextView
    private lateinit var stepNumber2: TextView
    private lateinit var stepNumber3: TextView
    private lateinit var stepText1: TextView
    private lateinit var stepText2: TextView
    private lateinit var stepText3: TextView
    private lateinit var dataStudentService: DataStudentService
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var aspiranteId: String
    private var isAccepted: Boolean = false


    private var currentStep = 1

    private lateinit var sharedd : SharedPreferences;
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
        val view = inflater.inflate(R.layout.fragment_inscription, container, false)
        sharedd = requireActivity().getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        aspiranteId = obtenerAspiranteId()

        Toast.makeText(context, sharedd.getString("USER_ID","no") + " : " + aspiranteId, Toast.LENGTH_SHORT).show()

        // Inicializar vistas
        progressBar = view.findViewById(R.id.progressBar)
        stepNumber1 = view.findViewById(R.id.stepNumber1)
        stepNumber2 = view.findViewById(R.id.stepNumber2)
        stepNumber3 = view.findViewById(R.id.stepNumber3)
        stepText1 = view.findViewById(R.id.stepText1)
        stepText2 = view.findViewById(R.id.stepText2)
        stepText3 = view.findViewById(R.id.stepText3)

        lottieLoading = view.findViewById(R.id.lottieLoading)

        updateUIForAcceptedStatus()

        // Actualizar la barra de progreso al inicio
        updateProgressBar()

        getEnrollmentForm(aspiranteId)


        getBackendDocuments()


        loadPaso1Fragment()

        // Inicializar servicio
        dataStudentService = ApiClient.dataStudentService

        return view
    }

    private fun updateProgressBar() {

        if (isAccepted) {
            return
        }
        // Actualizar el progreso de la barra
        val progress = (currentStep / 3.0) * 100
        progressBar.progress = progress.toInt()

        // Actualizar los pasos activos
        when (currentStep) {
            1 -> {
                setStepActive(stepNumber1, stepText1)
                setStepInactive(stepNumber2, stepText2)
                setStepInactive(stepNumber3, stepText3)
            }
            2 -> {
                setStepActive(stepNumber1, stepText1)
                setStepActive(stepNumber2, stepText2)
                setStepInactive(stepNumber3, stepText3)
            }
            3 -> {
                setStepActive(stepNumber1, stepText1)
                setStepActive(stepNumber2, stepText2)
                setStepActive(stepNumber3, stepText3)
            }
        }
    }

    private fun setStepActive(stepNumber: TextView, stepText: TextView) {
        stepNumber.setBackgroundResource(R.drawable.step_circle_active)
        stepText.setTextColor(ContextCompat.getColor(requireContext(), R.color.b41e46))
    }

    private fun setStepInactive(stepNumber: TextView, stepText: TextView) {
        stepNumber.setBackgroundResource(R.drawable.step_circle)
        stepText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun loadPaso1Fragment() {
        val paso1Fragment = Paso1Fragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, paso1Fragment)
            .commit()
    }

    private fun updateUIForAcceptedStatus() {
        val tvAcceptedMessage = view?.findViewById<TextView>(R.id.tvAcceptedMessage)

        if (isAccepted) {
            // Cambiar el color de la barra de progreso
            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.progress_bar_success),
                PorterDuff.Mode.SRC_IN
            )

            // Cambiar el color del paso 1
            stepNumber1.background = ContextCompat.getDrawable(requireContext(), R.drawable.step_circle_white)
            stepNumber1.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_number_text))
            stepText1.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_text))

            // Cambiar el color del paso 2
            stepNumber2.background = ContextCompat.getDrawable(requireContext(), R.drawable.step_circle_white)
            stepNumber2.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_number_text))
            stepText2.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_text))

            // Cambiar el color del paso 3
            stepNumber3.background = ContextCompat.getDrawable(requireContext(), R.drawable.step_circle_green)
            stepNumber3.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            stepText3.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_text))

            // Mostrar el mensaje de aceptación
            tvAcceptedMessage?.visibility = View.VISIBLE
        } else {
            // Ocultar el mensaje de aceptación si no está aceptado
            tvAcceptedMessage?.visibility = View.GONE
        }
    }

    private fun getBackendDocuments() {
        val aspiranteId = obtenerAspiranteId()
        println("Este es el aspirante id "+ aspiranteId)

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

                        // Obtener el estado de inscripción
                        val enrollmentStatus = studentDoc.enrollmentStatus
                        updateEnrollmentStatus(enrollmentStatus)
                    }


                } else {
                    // Manejar el error si la respuesta no es exitosa
//                    showErrorDialog("Error al obtener los documentos: ${response.message()}")
                    println("Error al obtener documentos: $response")
                }
            }

            override fun onFailure(call: Call<List<StudentDocDocument>>, t: Throwable) {
                // Manejar excepciones
                showErrorDialog("Error de conexión: ${t.message}")
            }
        })
    }



    fun updateEnrollmentStatus(status: Boolean) {
        isAccepted = status
        updateUIForAcceptedStatus()
    }

    // Método para recibir los datos del Paso 1
    fun setDatosPaso1(datos: Map<String, String>) {
        this.datosPaso1 = datos
    }

    fun setDatosPaso2(datos: Map<String, String>) {
        this.datosPaso2 = datos
    }


    // Método para avanzar al siguiente paso
    fun nextStep(targetStep: Int? = null) {
        if (targetStep != null) {
            currentStep = targetStep // Ir directo al paso indicado
        } else if (currentStep < 3) {
            currentStep++ // Avanzar un paso
        }

        updateProgressBar()

        when (currentStep) {
            2 -> loadPaso2Fragment()
            3 -> loadPaso3Fragment()
        }
    }


    // Método para cargar el Paso 2
    private fun loadPaso2Fragment() {
        val paso2Fragment = Paso2Fragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, paso2Fragment)
            .commit()
    }

    // Método para cargar el Paso 3
    private fun loadPaso3Fragment() {
        val paso3Fragment = Paso3Fragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, paso3Fragment)
            .commit()
    }

    fun previousStep() {
        if (currentStep > 1) {
            currentStep--
            updateProgressBar()

            // Cargar el fragmento correspondiente al paso actual
            when (currentStep) {
                1 -> loadPaso1Fragment()
                2 -> loadPaso2Fragment()
                3 -> loadPaso3Fragment()
            }
        }
    }


    fun enviarFormulario() {
        if (validateForm()) {
            showLoading(true) // Mostrar animación de carga
            sendData() // Enviar datos al backend
        } else {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateForm(): Boolean {
        // Aquí puedes agregar validaciones adicionales si es necesario
        return datosPaso1 != null && datosPaso2 != null
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            lottieLoading.visibility = View.VISIBLE
            lottieLoading.playAnimation()
        } else {
            lottieLoading.visibility = View.GONE
            lottieLoading.cancelAnimation()
        }
//        btnEnviar.isEnabled = !show
    }

    private fun obtenerAspiranteId(): String {
        val sharedPreferences = requireContext().getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        println("Id que devuelve " + sharedPreferences.getString("USER_ID", "0eNvExukbTSRglFVkSzH"))
        return sharedPreferences.getString("USER_ID", "0eNvExukbTSRglFVkSzH") ?: "0eNvExukbTSRglFVkSzH"
    }

    private fun getEnrollmentForm(aspiranteId: String) {
        println("Se esta imprimiendo")
        var e : String? = sharedd.getString("USER_ID","no");

        val service = ApiClient.studentEnrollmentService
        e?.let {
            service.getById(it).enqueue(object : Callback<StudentEnrollmentResponse> {
                override fun onResponse(
                    call: Call<StudentEnrollmentResponse>,
                    response: Response<StudentEnrollmentResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val enrollmentResponse = response.body()!!
                        if (!enrollmentResponse.error) {

                            val studentName = enrollmentResponse.data?.nombre
                            avanzarAlPaso3(studentName)
                        } else {
                            // Hubo un error en la respuesta
                            println("Error: ${enrollmentResponse.msg}")
                        }
                    } else {
                        // Error en la llamada al servicio
                        println("Error en la llamada al servicio: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<StudentEnrollmentResponse>, t: Throwable) {
                    // Error de conexión
                    println("Error de conexión: ${t.message}")
                }
            })
        }
    }

    private fun avanzarAlPaso3(studentName: String?) {

        if (studentName != null) {
            guardarNombreEstudiante(studentName)
        }

        nextStep(3)
    }



    private fun guardarNombreEstudiante(nombre: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("studentName", nombre)
        editor.apply()
    }

    private fun sendData() {
        // Combinar los datos de Paso1 y Paso2
        val dataStudent = DataStudent(
            id = "",
            aspiranteId = aspiranteId,
            aspiranteCurp = datosPaso1?.get("curp") ?: "",
            data = StudentData(
                curp = datosPaso1?.get("curp") ?: "",
                nombre = datosPaso1?.get("nombre") ?: "",
                primerApellido = datosPaso1?.get("primerApellido") ?: "",
                segundoApellido = datosPaso1?.get("segundoApellido") ?: "",
                sexo = datosPaso1?.get("sexo") ?: "",
                telefonoFijo = datosPaso1?.get("telefonoFijo") ?: "",
                telefonoMovil = datosPaso1?.get("telefonoMovil") ?: "",
                correoElectronico = datosPaso1?.get("correoElectronico") ?: "",
                puebloIndigena = datosPaso1?.get("puebloIndigena") ?: "",
                lenguaIndigena = datosPaso1?.get("lenguaIndigena") ?: "",
                fechaNacimiento = datosPaso1?.get("fechaNacimiento") ?: "",
                estado = datosPaso1?.get("estado") ?: "",
                municipio = datosPaso1?.get("municipio") ?: "",
                localidad = datosPaso1?.get("localidad") ?: "",
                comunidad = datosPaso1?.get("comunidad") ?: "",
                estadoMadre = datosPaso1?.get("estadoMadre") ?: "",
                curpMadre = datosPaso1?.get("curpMadre") ?: "",
                nombreMadre = datosPaso1?.get("nombreMadre") ?: "",
                primerApellidoMadre = datosPaso1?.get("primerApellidoMadre") ?: "",
                segundoApellidoMadre = datosPaso1?.get("segundoApellidoMadre") ?: "",
                fechaNacimientoMadre = datosPaso1?.get("fechaNacimientoMadre") ?: "",
                edoNacimientoMadre = datosPaso1?.get("edoNacimientoMadre") ?: "",
                estadoPadre = datosPaso1?.get("estadoPadre") ?: "",
                curpPadre = datosPaso1?.get("curpPadre") ?: "",
                nombrePadre = datosPaso1?.get("nombrePadre") ?: "",
                primerApellidoPadre = datosPaso1?.get("primerApellidoPadre") ?: "",
                segundoApellidoPadre = datosPaso1?.get("segundoApellidoPadre") ?: "",
                fechaNacimientoPadre = datosPaso1?.get("fechaNacimientoPadre") ?: "",
                edoNacimientoPadre = datosPaso1?.get("edoNacimientoPadre") ?: "",
                parentescoTutor = datosPaso1?.get("parentescoTutor") ?: "",
                curpTutor = datosPaso1?.get("curpTutor") ?: "",
                nombreTutor = datosPaso1?.get("nombreTutor") ?: "",
                primerApellidoTutor = datosPaso1?.get("primerApellidoTutor") ?: "",
                segundoApellidoTutor = datosPaso1?.get("segundoApellidoTutor") ?: "",
                fechaNacimientoTutor = datosPaso1?.get("fechaNacimientoTutor") ?: "",
                edoNacimientoTutor = datosPaso1?.get("edoNacimientoTutor") ?: "",
                comunidadCasa = datosPaso2?.get("comunidadCasa") ?: "",
                localidadCasa = datosPaso2?.get("localidadCasa") ?: "",
                centroCoordinador = datosPaso2?.get("centroCoordinador") ?: "",
                tipoCasa = datosPaso2?.get("tipoCasa") ?: "",
                nombreCasa = datosPaso2?.get("nombreCasa") ?: "",
                medioAcceso = datosPaso2?.get("medioAcceso") ?: "",
                especifAcceso = datosPaso2?.get("especifAcceso") ?: "",
                riesgoAcceso = datosPaso2?.get("riesgoAcceso") ?: "",
                discapacidad = datosPaso2?.get("discapacidad") ?: "",
                tipoDiscapacidad = datosPaso2?.get("tipoDiscapacidad") ?: "",
                especifDiscapacidad = datosPaso2?.get("especifDiscapacidad") ?: "",
                alergia = datosPaso2?.get("alergia") ?: "",
                alergiaDetalles = datosPaso2?.get("alergiaDetalles") ?: "",
                respirar = datosPaso2?.get("respirar") ?: "",
                respirarDetalles = datosPaso2?.get("respirarDetalles") ?: "",
                tratamiento = datosPaso2?.get("tratamiento") ?: "",
                tratamientoDetalles = datosPaso2?.get("tratamientoDetalles") ?: "",
                tipoEscuela = datosPaso2?.get("tipoEscuela") ?: "",
                cct = datosPaso2?.get("cct") ?: "",
                nombreEscuela = datosPaso2?.get("nombreEscuela") ?: "",
                escolaridad = datosPaso2?.get("escolaridad") ?: "",
                semestreoanosCursados = datosPaso2?.get("semestreoanosCursados") ?: "",
                tipoCurso = datosPaso2?.get("tipoCurso") ?: "",
                solicitud = datosPaso2?.get("solicitud") ?: "",
                especifRiesgo = datosPaso2?.get("specifRiesgo") ?: "",
                otraesco = datosPaso2?.get("otraesco") ?: ""
            )
        )

        // Enviar los datos al backend
        dataStudentService.createDataStudent(dataStudent).enqueue(object :
            Callback<StudentResponse> {
            override fun onResponse(call: Call<StudentResponse>, response: Response<StudentResponse>) {
                showLoading(false) // Ocultar animación de carga
                if (response.isSuccessful && response.body() != null) {
                    showSuccessDialog() // Mostrar diálogo de éxito
                    nextStep()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error en el registro"
                    showErrorDialog(errorMessage) // Mostrar diálogo de error
                }
            }

            override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
                showLoading(false) // Ocultar animación de carga
                showErrorDialog(t.message ?: "Error de conexión") // Mostrar diálogo de error
            }
        })
    }

    private fun showSuccessDialog() {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Éxito")
            .setContentText("Los datos se han registrado exitosamente.")
            .setConfirmClickListener { it.dismissWithAnimation() }
            .show()
    }

    private fun showErrorDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .setConfirmClickListener { it.dismissWithAnimation() }
            .show()
    }
}