package com.example.albergue_android.ui.components

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.Toast
import com.example.albergue_android.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Paso2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Paso2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btnStart: Button
    private lateinit var scrollFormulario: ScrollView
    private lateinit var btnSiguiente: Button
    private lateinit var btnRetroceder: ImageButton





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
        val view = inflater.inflate(R.layout.fragment_paso2, container, false)


        // Inicializar vistas
        btnStart = view.findViewById(R.id.btnStart)
        scrollFormulario = view.findViewById(R.id.scrollFormulario)
        btnSiguiente = view.findViewById(R.id.btn_siguiente)
        btnRetroceder = view.findViewById(R.id.btnRetroceder)

        // Sección Datos Académicos
        val tipoEscuelaSpinner = view.findViewById<Spinner>(R.id.tipoEscuela)
        val cctContainer = view.findViewById<LinearLayout>(R.id.cctContainer)
        val cctEditText = view.findViewById<EditText>(R.id.cct)
        val escolaridadSpinner = view.findViewById<Spinner>(R.id.escolaridad)
        val anosCursadosContainer = view.findViewById<LinearLayout>(R.id.anosCursadosContainer)
        val anosCursadosGroup = view.findViewById<RadioGroup>(R.id.anosCursadosGroup)

        // Sección Antecedentes de Salud
        val alergiaGroup = view.findViewById<RadioGroup>(R.id.alergiaGroup)
        val alergiaDetallesContainer = view.findViewById<LinearLayout>(R.id.alergiaDetallesContainer)
        val respirarGroup = view.findViewById<RadioGroup>(R.id.respirarGroup)
        val respirarDetallesContainer = view.findViewById<LinearLayout>(R.id.respirarDetallesContainer)
        val tratamientoGroup = view.findViewById<RadioGroup>(R.id.tratamientoGroup)
        val tratamientoDetallesContainer = view.findViewById<LinearLayout>(R.id.tratamientoDetallesContainer)

        val solicitudGroup = view.findViewById<RadioGroup>(R.id.solicitudGroup)

        // Mostrar/ocultar campo CCT según la selección de Tipo de Escuela
        tipoEscuelaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                cctContainer.visibility = if (selectedItem == "Pública") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Mostrar/ocultar RadioGroup de Años Cursados según la selección de Escolaridad
        escolaridadSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                anosCursadosContainer.visibility = if (selectedItem != "Seleccione una opción") View.VISIBLE else View.GONE

                // Generar dinámicamente RadioButtons para Años Cursados
                anosCursadosGroup.removeAllViews()
                val anos = when (selectedItem) {
                    "Prescolar", "Secundaria", "Bachillerato" -> 3
                    "Primaria" -> 6
                    "Superior" -> 7
                    else -> 0
                }

                for (i in 1..anos) {
                    val radioButton = RadioButton(context)
                    radioButton.text = i.toString()
                    anosCursadosGroup.addView(radioButton)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        alergiaGroup.setOnCheckedChangeListener { _, checkedId ->
            alergiaDetallesContainer.visibility = if (checkedId == R.id.alergiaSi) View.VISIBLE else View.GONE
        }

        respirarGroup.setOnCheckedChangeListener { _, checkedId ->
            respirarDetallesContainer.visibility = if (checkedId == R.id.respirarSi) View.VISIBLE else View.GONE
        }

        tratamientoGroup.setOnCheckedChangeListener { _, checkedId ->
            tratamientoDetallesContainer.visibility = if (checkedId == R.id.tratamientoSi) View.VISIBLE else View.GONE
        }


        // Mostrar el formulario al hacer clic en "Comenzar Paso 2"
        btnStart.setOnClickListener {
            mostrarFormulario()
        }


        // Mostrar u ocultar opciones de discapacidad
        val rgDiscapacidad = view.findViewById<RadioGroup>(R.id.rgDiscapacidad)
        val contenedorTipo = view.findViewById<LinearLayout>(R.id.contenedorTipoDiscapacidad)

        rgDiscapacidad.setOnCheckedChangeListener { _, checkedId ->
            contenedorTipo.visibility = if (checkedId == R.id.rbDiscapacidadSi) View.VISIBLE else View.GONE
        }

        // Mostrar campo "Otro" si se selecciona
        val rgTipoDiscapacidad = view.findViewById<RadioGroup>(R.id.rgTipoDiscapacidad)
        val etEspecifDiscapacidad = view.findViewById<EditText>(R.id.etEspecifDiscapacidad)

        rgTipoDiscapacidad.setOnCheckedChangeListener { _, checkedId ->
            etEspecifDiscapacidad.visibility = if (checkedId == R.id.rbOtroDiscapacidad) View.VISIBLE else View.GONE
        }



        // Avanzar al siguiente paso al hacer clic en "Siguiente"
        btnSiguiente.setOnClickListener {
            if (validarFormulario()) {
                // Pasar los datos al InscriptionFragment
                val datosPaso2 = obtenerDatosFormulario()
                (parentFragment as InscriptionFragment).setDatosPaso2(datosPaso2)
                (parentFragment as InscriptionFragment).nextStep()
                (parentFragment as InscriptionFragment).enviarFormulario() // Llamada adicional si es necesario
            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        btnRetroceder.setOnClickListener {
            (parentFragment as InscriptionFragment).previousStep()
        }

        return view
    }

    private fun mostrarFormulario() {
        // Mostrar el formulario
        scrollFormulario.visibility = View.VISIBLE
    }

    private fun validarFormulario(): Boolean {
        var isValid = true

        // Validar Tipo de Escuela
        val tipoEscuelaSpinner = view?.findViewById<Spinner>(R.id.tipoEscuela)
        if (tipoEscuelaSpinner?.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Seleccione el tipo de escuela", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar CCT si la escuela es pública
        val cctEditText = view?.findViewById<EditText>(R.id.cct)
        if (tipoEscuelaSpinner?.selectedItem.toString() == "Pública" && cctEditText?.text.isNullOrEmpty()) {
            cctEditText?.error = "CCT requerido"
            isValid = false
        } else {
            cctEditText?.error = null
        }

        // Validar Nombre de la Escuela
        val nombreEscuela = view?.findViewById<EditText>(R.id.nombreEscuela)?.text.toString()
        if (nombreEscuela.isEmpty()) {
            view?.findViewById<EditText>(R.id.nombreEscuela)?.error = "Nombre de la escuela requerido"
            isValid = false
        } else {
            view?.findViewById<EditText>(R.id.nombreEscuela)?.error = null
        }

        // Validar Escolaridad
        val escolaridadSpinner = view?.findViewById<Spinner>(R.id.escolaridad)
        if (escolaridadSpinner?.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Seleccione la escolaridad", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Años Cursados
        val anosCursadosGroup = view?.findViewById<RadioGroup>(R.id.anosCursadosGroup)
        if (anosCursadosGroup?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione los años cursados", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Tipo de Curso
        val tipoCursoGroup = view?.findViewById<RadioGroup>(R.id.tipoCursoGroup)
        if (tipoCursoGroup?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione el tipo de curso", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Discapacidad
        val rgDiscapacidad = view?.findViewById<RadioGroup>(R.id.rgDiscapacidad)
        if (rgDiscapacidad?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione si tiene discapacidad", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Tipo de Discapacidad si es visible
        val rgTipoDiscapacidad = view?.findViewById<RadioGroup>(R.id.rgTipoDiscapacidad)
        if (rgDiscapacidad?.checkedRadioButtonId == R.id.rbDiscapacidadSi && rgTipoDiscapacidad?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione el tipo de discapacidad", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Alergia
        val alergiaGroup = view?.findViewById<RadioGroup>(R.id.alergiaGroup)
        if (alergiaGroup?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione si tiene alergia", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Detalles de Alergia si es visible
        val alergiaDetalles = view?.findViewById<EditText>(R.id.alergiaDetalles)
        if (alergiaGroup?.checkedRadioButtonId == R.id.alergiaSi && alergiaDetalles?.text.isNullOrEmpty()) {
            alergiaDetalles?.error = "Detalles de alergia requeridos"
            isValid = false
        } else {
            alergiaDetalles?.error = null
        }

        // Validar Dificultad para Respirar
        val respirarGroup = view?.findViewById<RadioGroup>(R.id.respirarGroup)
        if (respirarGroup?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione si tiene dificultad para respirar", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Detalles de Dificultad para Respirar si es visible
        val respirarDetalles = view?.findViewById<EditText>(R.id.respirarDetalles)
        if (respirarGroup?.checkedRadioButtonId == R.id.respirarSi && respirarDetalles?.text.isNullOrEmpty()) {
            respirarDetalles?.error = "Detalles de dificultad para respirar requeridos"
            isValid = false
        } else {
            respirarDetalles?.error = null
        }

        // Validar Tratamiento Médico
        val tratamientoGroup = view?.findViewById<RadioGroup>(R.id.tratamientoGroup)
        if (tratamientoGroup?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione si está bajo tratamiento médico", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        // Validar Detalles de Tratamiento Médico si es visible
        val tratamientoDetalles = view?.findViewById<EditText>(R.id.tratamientoDetalles)
        if (tratamientoGroup?.checkedRadioButtonId == R.id.tratamientoSi && tratamientoDetalles?.text.isNullOrEmpty()) {
            tratamientoDetalles?.error = "Detalles de tratamiento médico requeridos"
            isValid = false
        } else {
            tratamientoDetalles?.error = null
        }

        // Validar Tipo de Solicitud
        val solicitudGroup = view?.findViewById<RadioGroup>(R.id.solicitudGroup)
        if (solicitudGroup?.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione el tipo de solicitud", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }


    fun obtenerDatosFormulario(): Map<String, String> {
        // Obtener la vista del fragmento
        val view = requireView()

        // Obtener valores de los EditText existentes
        val comunidad = view.findViewById<EditText>(R.id.et_comunidad_casa).text.toString()
        val localidad = view.findViewById<EditText>(R.id.et_localidad_casa).text.toString()
        val nombreCasa = view.findViewById<EditText>(R.id.et_nombre_casa).text.toString()
        val centroCoordinador = view.findViewById<EditText>(R.id.et_centro_coordinador).text.toString()

        // Obtener selección del RadioGroup de Tipo de Casa
        val radioGroupCasa = view.findViewById<RadioGroup>(R.id.rg_tipo_casa)
        val tipoCasaId = radioGroupCasa.checkedRadioButtonId
        val tipoCasa = if (tipoCasaId != -1) {
            view.findViewById<RadioButton>(tipoCasaId).text.toString()
        } else {
            "No seleccionado"
        }

        // Obtener selección de Medio de Acceso
        val radioGroupAcceso = view.findViewById<RadioGroup>(R.id.medioAccesoGroup)
        val medioAccesoId = radioGroupAcceso.checkedRadioButtonId
        val medioAcceso = if (medioAccesoId != -1) {
            view.findViewById<RadioButton>(medioAccesoId).text.toString()
        } else {
            "No seleccionado"
        }

        // Verificar si se seleccionó "Otro" y obtener el valor del EditText si está visible
        val especifAcceso = view.findViewById<EditText>(R.id.especifAcceso)
        val medioAccesoEspecifico = if (especifAcceso.visibility == View.VISIBLE) {
            especifAcceso.text.toString()
        } else {
            ""
        }

        // Obtener selección de Riesgo de Acceso
        val radioGroupRiesgo = view.findViewById<RadioGroup>(R.id.riesgoAccesoGroup)
        val riesgoAccesoId = radioGroupRiesgo.checkedRadioButtonId
        val riesgoAcceso = if (riesgoAccesoId != -1) {
            view.findViewById<RadioButton>(riesgoAccesoId).text.toString()
        } else {
            "No seleccionado"
        }

        // Verificar si se seleccionó "Otro" y obtener el valor del EditText si está visible
        val especifRiesgo = view.findViewById<EditText>(R.id.especifRiesgo)
        val riesgoAccesoEspecifico = if (especifRiesgo.visibility == View.VISIBLE) {
            especifRiesgo.text.toString()
        } else {
            ""
        }

        // Obtener datos de la discapacidad
        val rgDiscapacidad = view.findViewById<RadioGroup>(R.id.rgDiscapacidad)
        val discapacidadId = rgDiscapacidad.checkedRadioButtonId
        val discapacidad = if (discapacidadId != -1) {
            view.findViewById<RadioButton>(discapacidadId).text.toString()
        } else {
            "No seleccionado"
        }

        // Obtener tipo de discapacidad si es visible
        val rgTipoDiscapacidad = view.findViewById<RadioGroup>(R.id.rgTipoDiscapacidad)
        val tipoDiscapacidadId = rgTipoDiscapacidad.checkedRadioButtonId
        val tipoDiscapacidad = if (tipoDiscapacidadId != -1) {
            view.findViewById<RadioButton>(tipoDiscapacidadId).text.toString()
        } else {
            "No seleccionado"
        }

        // Verificar si se especificó un "Otro" para discapacidad
        val etEspecifDiscapacidad = view.findViewById<EditText>(R.id.etEspecifDiscapacidad)
        val discapacidadEspecifica = if (etEspecifDiscapacidad.visibility == View.VISIBLE) {
            etEspecifDiscapacidad.text.toString()
        } else {
            ""
        }

        // Obtener datos académicos
        val tipoEscuelaSpinner = view.findViewById<Spinner>(R.id.tipoEscuela)
        val tipoEscuela = tipoEscuelaSpinner.selectedItem.toString()

        val cctEditText = view.findViewById<EditText>(R.id.cct)
        val cct = if (cctEditText.visibility == View.VISIBLE) cctEditText.text.toString() else ""

        val nombreEscuela = view.findViewById<EditText>(R.id.nombreEscuela).text.toString()

        val escolaridadSpinner = view.findViewById<Spinner>(R.id.escolaridad)
        val escolaridad = escolaridadSpinner.selectedItem.toString()

        val anosCursadosGroup = view.findViewById<RadioGroup>(R.id.anosCursadosGroup)
        val anosCursadosId = anosCursadosGroup.checkedRadioButtonId
        val anosCursados = if (anosCursadosId != -1) {
            view.findViewById<RadioButton>(anosCursadosId).text.toString()
        } else {
            "No seleccionado"
        }

        val tipoCursoGroup = view.findViewById<RadioGroup>(R.id.tipoCursoGroup)
        val tipoCursoId = tipoCursoGroup.checkedRadioButtonId
        val tipoCurso = if (tipoCursoId != -1) {
            view.findViewById<RadioButton>(tipoCursoId).text.toString()
        } else {
            "No seleccionado"
        }

        // Antecedentes de Salud
        val alergiaGroup = view.findViewById<RadioGroup>(R.id.alergiaGroup)
        val alergiaId = alergiaGroup.checkedRadioButtonId
        val alergia = if (alergiaId != -1) view.findViewById<RadioButton>(alergiaId).text.toString() else "No seleccionado"

        val alergiaDetalles = view.findViewById<EditText>(R.id.alergiaDetalles)
        val alergiaDetallesText = if (alergiaDetalles.visibility == View.VISIBLE) alergiaDetalles.text.toString() else ""

        val respirarGroup = view.findViewById<RadioGroup>(R.id.respirarGroup)
        val respirarId = respirarGroup.checkedRadioButtonId
        val respirar = if (respirarId != -1) view.findViewById<RadioButton>(respirarId).text.toString() else "No seleccionado"

        val respirarDetalles = view.findViewById<EditText>(R.id.respirarDetalles)
        val respirarDetallesText = if (respirarDetalles.visibility == View.VISIBLE) respirarDetalles.text.toString() else ""

        val tratamientoGroup = view.findViewById<RadioGroup>(R.id.tratamientoGroup)
        val tratamientoId = tratamientoGroup.checkedRadioButtonId
        val tratamiento = if (tratamientoId != -1) view.findViewById<RadioButton>(tratamientoId).text.toString() else "No seleccionado"

        val tratamientoDetalles = view.findViewById<EditText>(R.id.tratamientoDetalles)
        val tratamientoDetallesText = if (tratamientoDetalles.visibility == View.VISIBLE) tratamientoDetalles.text.toString() else ""

        // Tipo de Solicitud
        val solicitudGroup = view.findViewById<RadioGroup>(R.id.solicitudGroup)
        val solicitudId = solicitudGroup.checkedRadioButtonId
        val solicitud = when (solicitudId) {
            R.id.solicitud3 -> "3"
            R.id.solicitud2 -> "2"
            R.id.solicitud1 -> "1"
            else -> "No seleccionado"
        }

        // Devolver un mapa con todos los datos
        return mapOf(
            "comunidad" to comunidad,
            "localidad" to localidad,
            "nombre_casa_comedor" to nombreCasa,
            "centro_coordinador" to centroCoordinador,
            "tipo_casa_comedor" to tipoCasa,
            "medio_acceso" to medioAcceso,
            "medio_acceso_especifico" to medioAccesoEspecifico,
            "riesgo_acceso" to riesgoAcceso,
            "riesgo_acceso_especifico" to riesgoAccesoEspecifico,
            "discapacidad" to discapacidad,
            "tipo_discapacidad" to tipoDiscapacidad,
            "discapacidad_especifica" to discapacidadEspecifica,
            "tipo_escuela" to tipoEscuela,
            "cct" to cct,
            "nombre_escuela" to nombreEscuela,
            "escolaridad" to escolaridad,
            "anos_cursados" to anosCursados,
            "tipo_curso" to tipoCurso,
            "alergia" to alergia,
            "alergia_detalles" to alergiaDetallesText,
            "respirar" to respirar,
            "respirar_detalles" to respirarDetallesText,
            "tratamiento" to tratamiento,
            "tratamiento_detalles" to tratamientoDetallesText,
            "solicitud" to solicitud
        )
    }



}