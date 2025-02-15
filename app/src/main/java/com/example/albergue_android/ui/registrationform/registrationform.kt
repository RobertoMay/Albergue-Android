package com.example.albergue_android.ui.registrationform

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.albergue_android.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.os.Handler
import android.os.Looper


class RegistrationFormFragment : Fragment() {

    private lateinit var etFullNames: TextInputEditText
    private lateinit var etFirstLastName: TextInputEditText
    private lateinit var etSecondLastName: TextInputEditText
    private lateinit var etCurp: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnEnviar: Button
    private lateinit var lottieLoading: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrationform, container, false)

        // Inicializamos las vistas
        etFullNames = view.findViewById(R.id.etFullNames)
        etFirstLastName = view.findViewById(R.id.etFirstLastName)
        etSecondLastName = view.findViewById(R.id.etSecondLastName)
        etCurp = view.findViewById(R.id.etCurp)
        etEmail = view.findViewById(R.id.etEmail)
        btnEnviar = view.findViewById(R.id.btnEnviar)
        lottieLoading = view.findViewById(R.id.lottieLoading)

        // Validaciones en tiempo real para CURP
        etCurp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val curp = s.toString().uppercase()
                if (curp != s.toString()) {
                    etCurp.setText(curp)
                    etCurp.setSelection(curp.length) // Mantener cursor al final
                }
            }
        })

        // Acción al presionar el botón "Enviar"
        btnEnviar.setOnClickListener {
            if (validateForm()) {
                showLoading(true) // Mostrar animación Lottie
                Handler(Looper.getMainLooper()).postDelayed({
                    showLoading(false) // Ocultar después de 3 segundos
                    Toast.makeText(requireContext(), "Formulario enviado", Toast.LENGTH_SHORT).show()
                }, 3000)
            }
        }

        return view
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            lottieLoading.visibility = View.VISIBLE
            lottieLoading.playAnimation()
        } else {
            lottieLoading.visibility = View.GONE
            lottieLoading.cancelAnimation()
        }
        btnEnviar.isEnabled = !show
    }

    // Función para validar el formulario
    private fun validateForm(): Boolean {
        var isValid = true

        val fullNames = etFullNames.text.toString().trim()
        val firstLastName = etFirstLastName.text.toString().trim()
        val secondLastName = etSecondLastName.text.toString().trim()
        val curp = etCurp.text.toString().trim()
        val email = etEmail.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (fullNames.isEmpty()) {
            etFullNames.error = "Nombre requerido"
            isValid = false
        }

        if (firstLastName.isEmpty()) {
            etFirstLastName.error = "Apellido paterno requerido"
            isValid = false
        }

        if (secondLastName.isEmpty()) {
            etSecondLastName.error = "Apellido materno requerido"
            isValid = false
        }

        // Validar CURP con regex
        val curpRegex = "^[A-Z]{4}\\d{6}[HM]{1}(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)[A-Z]{3}[A-Z\\d]{1}\\d{1}$".toRegex()
        if (curp.isEmpty()) {
            etCurp.error = "CURP requerida"
            isValid = false
        } else if (!curpRegex.matches(curp)) {
            etCurp.error = "CURP inválida"
            isValid = false
        }

        // Validar correo electrónico
        if (email.isEmpty()) {
            etEmail.error = "Correo requerido"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Correo inválido"
            isValid = false
        }

        return isValid
    }
}
