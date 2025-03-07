package com.example.albergue_android.ui.components

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
 * Use the [Paso1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Paso1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var checkTerms: CheckBox
    private lateinit var btnStart: Button
    private lateinit var scrollFormulario: ScrollView
    private lateinit var btnSiguiente: Button

    // Campos del formulario
    private lateinit var etCurp: EditText
    private lateinit var etNombre: EditText
    private lateinit var etPrimerApellido: EditText
    private lateinit var etSegundoApellido: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var rgSexo: RadioGroup
    private lateinit var etTelefonoFijo: EditText
    private lateinit var etTelefonoMovil: EditText
    private lateinit var etCorreoElectronico: EditText
    private lateinit var spinnerPuebloIndigena: Spinner
    private lateinit var etLenguaIndigena: EditText
    private lateinit var etEstado: EditText
    private lateinit var etMunicipio: EditText
    private lateinit var etLocalidad: EditText
    private lateinit var etComunidad: EditText
    private lateinit var spinnerEstadoMadre: Spinner
    private lateinit var etCurpMadre: EditText
    private lateinit var etNombreMadre: EditText
    private lateinit var etPrimerApellidoMadre: EditText
    private lateinit var etSegundoApellidoMadre: EditText
    private lateinit var etFechaNacimientoMadre: EditText
    private lateinit var etEdoNacimientoMadre: EditText
    private lateinit var spinnerEstadoPadre: Spinner
    private lateinit var etCurpPadre: EditText
    private lateinit var etNombrePadre: EditText
    private lateinit var etPrimerApellidoPadre: EditText
    private lateinit var etSegundoApellidoPadre: EditText
    private lateinit var etFechaNacimientoPadre: EditText
    private lateinit var etEdoNacimientoPadre: EditText
    private lateinit var spinnerParentescoTutor: Spinner
    private lateinit var etCurpTutor: EditText
    private lateinit var etNombreTutor: EditText
    private lateinit var etPrimerApellidoTutor: EditText
    private lateinit var etSegundoApellidoTutor: EditText
    private lateinit var etFechaNacimientoTutor: EditText
    private lateinit var etEdoNacimientoTutor: EditText

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
        val view = inflater.inflate(R.layout.fragment_paso1, container, false)

        // Inicializar vistas
        checkTerms = view.findViewById(R.id.checkTerms)
        btnStart = view.findViewById(R.id.btnStart)
        scrollFormulario = view.findViewById(R.id.scrollFormulario)
        btnSiguiente = view.findViewById(R.id.btn_siguiente)

        // Inicializar campos del formulario
        etCurp = view.findViewById(R.id.et_curp)
        etNombre = view.findViewById(R.id.et_nombre)
        etPrimerApellido = view.findViewById(R.id.et_primer_apellido)
        etSegundoApellido = view.findViewById(R.id.et_segundo_apellido)
        etFechaNacimiento = view.findViewById(R.id.et_fecha_nacimiento)
        rgSexo = view.findViewById(R.id.rg_sexo)
        etTelefonoFijo = view.findViewById(R.id.et_telefono_fijo)
        etTelefonoMovil = view.findViewById(R.id.et_telefono_movil)
        etCorreoElectronico = view.findViewById(R.id.et_correo_electronico)
        spinnerPuebloIndigena = view.findViewById(R.id.spinner_pueblo_indigena)
        etLenguaIndigena = view.findViewById(R.id.et_lengua_indigena)
        etEstado = view.findViewById(R.id.et_estado)
        etMunicipio = view.findViewById(R.id.et_municipio)
        etLocalidad = view.findViewById(R.id.et_localidad)
        etComunidad = view.findViewById(R.id.et_comunidad)
        spinnerEstadoMadre = view.findViewById(R.id.spinner_estado_madre)
        etCurpMadre = view.findViewById(R.id.et_curp_madre)
        etNombreMadre = view.findViewById(R.id.et_nombre_madre)
        etPrimerApellidoMadre = view.findViewById(R.id.et_primer_apellido_madre)
        etSegundoApellidoMadre = view.findViewById(R.id.et_segundo_apellido_madre)
        etFechaNacimientoMadre = view.findViewById(R.id.et_fecha_nacimiento_madre)
        etEdoNacimientoMadre = view.findViewById(R.id.et_edo_nacimiento_madre)
        spinnerEstadoPadre = view.findViewById(R.id.spinner_estado_padre)
        etCurpPadre = view.findViewById(R.id.et_curp_padre)
        etNombrePadre = view.findViewById(R.id.et_nombre_padre)
        etPrimerApellidoPadre = view.findViewById(R.id.et_primer_apellido_padre)
        etSegundoApellidoPadre = view.findViewById(R.id.et_segundo_apellido_padre)
        etFechaNacimientoPadre = view.findViewById(R.id.et_fecha_nacimiento_padre)
        etEdoNacimientoPadre = view.findViewById(R.id.et_edo_nacimiento_padre)
        spinnerParentescoTutor = view.findViewById(R.id.spinner_parentesco_tutor)
        etCurpTutor = view.findViewById(R.id.et_curp_tutor)
        etNombreTutor = view.findViewById(R.id.et_nombre_tutor)
        etPrimerApellidoTutor = view.findViewById(R.id.et_primer_apellido_tutor)
        etSegundoApellidoTutor = view.findViewById(R.id.et_segundo_apellido_tutor)
        etFechaNacimientoTutor = view.findViewById(R.id.et_fecha_nacimiento_tutor)
        etEdoNacimientoTutor = view.findViewById(R.id.et_edo_nacimiento_tutor)

        // Habilitar o deshabilitar el botón según la casilla de verificación
        checkTerms.setOnCheckedChangeListener { _, isChecked ->
            btnStart.isEnabled = isChecked
        }

        // Acción al presionar el botón de inicio
        btnStart.setOnClickListener {
            // Navegar al siguiente paso o mostrar el formulario
            mostrarFormulario()
        }

        btnSiguiente.setOnClickListener {
            if (validarFormulario()) {
                // Pasar los datos al InscriptionFragment
                val datosPaso1 = obtenerDatosFormulario()
                (parentFragment as InscriptionFragment).setDatosPaso1(datosPaso1)
                (parentFragment as InscriptionFragment).nextStep()
            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun validarFormulario(): Boolean {
        var isValid = true // Variable para rastrear si el formulario es válido

        // Validar CURP
        val curp = etCurp.text.toString()
        val curpRegex = "^[A-Z]{4}\\d{6}[HM]{1}(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)[A-Z]{3}[A-Z\\d]{1}\\d{1}$".toRegex()
        if (curp.isEmpty()) {
            etCurp.error = "CURP requerida"
            isValid = false
        } else if (!curpRegex.matches(curp)) {
            etCurp.error = "CURP inválida"
            isValid = false
        } else {
            etCurp.error = null // Limpiar el error si es válido
        }

        // Validar correo electrónico
        val email = etCorreoElectronico.text.toString()
        if (email.isEmpty()) {
            etCorreoElectronico.error = "Correo requerido"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etCorreoElectronico.error = "Correo inválido"
            isValid = false
        } else {
            etCorreoElectronico.error = null // Limpiar el error si es válido
        }

        // Validar que los demás campos no estén vacíos
        if (etNombre.text.isEmpty()) {
            etNombre.error = "Nombre requerido"
            isValid = false
        } else {
            etNombre.error = null
        }

        if (etPrimerApellido.text.isEmpty()) {
            etPrimerApellido.error = "Primer apellido requerido"
            isValid = false
        } else {
            etPrimerApellido.error = null
        }

        if (etSegundoApellido.text.isEmpty()) {
            etSegundoApellido.error = "Segundo apellido requerido"
            isValid = false
        } else {
            etSegundoApellido.error = null
        }

        if (etFechaNacimiento.text.isEmpty()) {
            etFechaNacimiento.error = "Fecha de nacimiento requerida"
            isValid = false
        } else {
            etFechaNacimiento.error = null
        }

        if (rgSexo.checkedRadioButtonId == -1) {
            Toast.makeText(requireContext(), "Seleccione un sexo", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (etTelefonoFijo.text.isEmpty()) {
            etTelefonoFijo.error = "Teléfono fijo requerido"
            isValid = false
        } else {
            etTelefonoFijo.error = null
        }

        if (etTelefonoMovil.text.isEmpty()) {
            etTelefonoMovil.error = "Teléfono móvil requerido"
            isValid = false
        } else {
            etTelefonoMovil.error = null
        }

        if (spinnerPuebloIndigena.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Seleccione un pueblo indígena", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (etLenguaIndigena.text.isEmpty()) {
            etLenguaIndigena.error = "Lengua indígena requerida"
            isValid = false
        } else {
            etLenguaIndigena.error = null
        }

        if (etEstado.text.isEmpty()) {
            etEstado.error = "Estado requerido"
            isValid = false
        } else {
            etEstado.error = null
        }

        if (etMunicipio.text.isEmpty()) {
            etMunicipio.error = "Municipio requerido"
            isValid = false
        } else {
            etMunicipio.error = null
        }

        if (etLocalidad.text.isEmpty()) {
            etLocalidad.error = "Localidad requerida"
            isValid = false
        } else {
            etLocalidad.error = null
        }

        if (etComunidad.text.isEmpty()) {
            etComunidad.error = "Comunidad requerida"
            isValid = false
        } else {
            etComunidad.error = null
        }

        // Validar datos de la madre
        if (spinnerEstadoMadre.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Seleccione el estado de la madre", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val curpMadre = etCurpMadre.text.toString()
        if (curpMadre.isEmpty()) {
            etCurpMadre.error = "CURP de la madre requerida"
            isValid = false
        } else if (!curpRegex.matches(curpMadre)) {
            etCurpMadre.error = "CURP de la madre inválida"
            isValid = false
        } else {
            etCurpMadre.error = null
        }

        if (etNombreMadre.text.isEmpty()) {
            etNombreMadre.error = "Nombre de la madre requerido"
            isValid = false
        } else {
            etNombreMadre.error = null
        }


        // Validar datos de la madre
        if (etPrimerApellidoMadre.text.isEmpty()) {
            etPrimerApellidoMadre.error = "Primer apellido de la madre requerido"
            isValid = false
        } else {
            etPrimerApellidoMadre.error = null
        }

        if (etSegundoApellidoMadre.text.isEmpty()) {
            etSegundoApellidoMadre.error = "Segundo apellido de la madre requerido"
            isValid = false
        } else {
            etSegundoApellidoMadre.error = null
        }

        if (etFechaNacimientoMadre.text.isEmpty()) {
            etFechaNacimientoMadre.error = "Fecha de nacimiento de la madre requerida"
            isValid = false
        } else {
            etFechaNacimientoMadre.error = null
        }

        if (etEdoNacimientoMadre.text.isEmpty()) {
            etEdoNacimientoMadre.error = "Entidad de nacimiento de la madre requerida"
            isValid = false
        } else {
            etEdoNacimientoMadre.error = null
        }

// Validar datos del padre
        if (spinnerEstadoPadre.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Seleccione el estado del padre", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val curpPadre = etCurpPadre.text.toString()
        if (curpPadre.isEmpty()) {
            etCurpPadre.error = "CURP del padre requerida"
            isValid = false
        } else if (!curpRegex.matches(curpPadre)) {
            etCurpPadre.error = "CURP del padre inválida"
            isValid = false
        } else {
            etCurpPadre.error = null
        }


        if (etNombrePadre.text.isEmpty()) {
            etNombrePadre.error = "Nombre del padre requerido"
            isValid = false
        } else {
            etNombrePadre.error = null
        }

        if (etPrimerApellidoPadre.text.isEmpty()) {
            etPrimerApellidoPadre.error = "Primer apellido del padre requerido"
            isValid = false
        } else {
            etPrimerApellidoPadre.error = null
        }

        if (etSegundoApellidoPadre.text.isEmpty()) {
            etSegundoApellidoPadre.error = "Segundo apellido del padre requerido"
            isValid = false
        } else {
            etSegundoApellidoPadre.error = null
        }

        if (etFechaNacimientoPadre.text.isEmpty()) {
            etFechaNacimientoPadre.error = "Fecha de nacimiento del padre requerida"
            isValid = false
        } else {
            etFechaNacimientoPadre.error = null
        }

        if (etEdoNacimientoPadre.text.isEmpty()) {
            etEdoNacimientoPadre.error = "Entidad de nacimiento del padre requerida"
            isValid = false
        } else {
            etEdoNacimientoPadre.error = null
        }

// Validar datos del tutor
        if (spinnerParentescoTutor.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), "Seleccione el parentesco del tutor", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        val curpTutor = etCurpTutor.text.toString()
        if (curpTutor.isEmpty()) {
            etCurpTutor.error = "CURP del tutor requerida"
            isValid = false
        } else if (!curpRegex.matches(curpTutor)) {
            etCurpTutor.error = "CURP del tutor inválida"
            isValid = false
        } else {
            etCurpTutor.error = null
        }

        if (etNombreTutor.text.isEmpty()) {
            etNombreTutor.error = "Nombre del tutor requerido"
            isValid = false
        } else {
            etNombreTutor.error = null
        }

        if (etPrimerApellidoTutor.text.isEmpty()) {
            etPrimerApellidoTutor.error = "Primer apellido del tutor requerido"
            isValid = false
        } else {
            etPrimerApellidoTutor.error = null
        }

        if (etSegundoApellidoTutor.text.isEmpty()) {
            etSegundoApellidoTutor.error = "Segundo apellido del tutor requerido"
            isValid = false
        } else {
            etSegundoApellidoTutor.error = null
        }

        if (etFechaNacimientoTutor.text.isEmpty()) {
            etFechaNacimientoTutor.error = "Fecha de nacimiento del tutor requerida"
            isValid = false
        } else {
            etFechaNacimientoTutor.error = null
        }

        if (etEdoNacimientoTutor.text.isEmpty()) {
            etEdoNacimientoTutor.error = "Entidad de nacimiento del tutor requerida"
            isValid = false
        } else {
            etEdoNacimientoTutor.error = null
        }
        return isValid
    }

    private fun mostrarFormulario() {
        // Mostrar el formulario
        scrollFormulario.visibility = View.VISIBLE
    }

    private fun obtenerDatosFormulario(): Map<String, String> {
        // Crear un mapa con los datos del formulario
        return mapOf(
            "curp" to etCurp.text.toString(),
            "nombre" to etNombre.text.toString(),
            "primerApellido" to etPrimerApellido.text.toString(),
            "segundoApellido" to etSegundoApellido.text.toString(),
            "fechaNacimiento" to etFechaNacimiento.text.toString(),
            "sexo" to when (rgSexo.checkedRadioButtonId) {
                R.id.rb_hombre -> "Hombre"
                R.id.rb_mujer -> "Mujer"
                else -> "Otro"
            },
            "telefonoFijo" to etTelefonoFijo.text.toString(),
            "telefonoMovil" to etTelefonoMovil.text.toString(),
            "correoElectronico" to etCorreoElectronico.text.toString(),
            "puebloIndigena" to spinnerPuebloIndigena.selectedItem.toString(),
            "lenguaIndigena" to etLenguaIndigena.text.toString(),
            "estado" to etEstado.text.toString(),
            "municipio" to etMunicipio.text.toString(),
            "localidad" to etLocalidad.text.toString(),
            "comunidad" to etComunidad.text.toString(),
            "estadoMadre" to spinnerEstadoMadre.selectedItem.toString(),
            "curpMadre" to etCurpMadre.text.toString(),
            "nombreMadre" to etNombreMadre.text.toString(),
            "primerApellidoMadre" to etPrimerApellidoMadre.text.toString(),
            "segundoApellidoMadre" to etSegundoApellidoMadre.text.toString(),
            "fechaNacimientoMadre" to etFechaNacimientoMadre.text.toString(),
            "edoNacimientoMadre" to etEdoNacimientoMadre.text.toString(),
            "estadoPadre" to spinnerEstadoPadre.selectedItem.toString(),
            "curpPadre" to etCurpPadre.text.toString(),
            "nombrePadre" to etNombrePadre.text.toString(),
            "primerApellidoPadre" to etPrimerApellidoPadre.text.toString(),
            "segundoApellidoPadre" to etSegundoApellidoPadre.text.toString(),
            "fechaNacimientoPadre" to etFechaNacimientoPadre.text.toString(),
            "edoNacimientoPadre" to etEdoNacimientoPadre.text.toString(),
            "parentescoTutor" to spinnerParentescoTutor.selectedItem.toString(),
            "curpTutor" to etCurpTutor.text.toString(),
            "nombreTutor" to etNombreTutor.text.toString(),
            "primerApellidoTutor" to etPrimerApellidoTutor.text.toString(),
            "segundoApellidoTutor" to etSegundoApellidoTutor.text.toString(),
            "fechaNacimientoTutor" to etFechaNacimientoTutor.text.toString(),
            "edoNacimientoTutor" to etEdoNacimientoTutor.text.toString()
        )
    }

}