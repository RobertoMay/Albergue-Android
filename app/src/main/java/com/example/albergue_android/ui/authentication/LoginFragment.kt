package com.example.albergue_android.ui.authentication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.albergue_android.R
import com.example.albergue_android.domain.models.ILogin
import com.example.albergue_android.ui.viewmodel.LoginViewModel
import com.example.albergue_android.ui.admin.AdminFragment
import com.example.albergue_android.ui.CallFragment
import com.example.albergue_android.ui.alumn.AlumnoFragment
import com.example.albergue_android.ui.components.InscriptionFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.airbnb.lottie.LottieAnimationView
import cn.pedant.SweetAlert.SweetAlertDialog
import android.util.Log

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var emailEditText: EditText
    private lateinit var curpEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var loginButton: Button
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailEditText = view.findViewById(R.id.emailEditText)
        curpEditText = view.findViewById(R.id.curpEditText)
        passwordLayout = view.findViewById(R.id.passwordLayout)
        loginButton = view.findViewById(R.id.loginButton)
        lottieLoading = view.findViewById(R.id.lottieLoading)

        sharedPreferences = requireActivity().getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)

        // Verificar si ya hay sesión guardada
        checkSession()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val curp = curpEditText.text.toString()

            if (email.isNotEmpty() && curp.isNotEmpty()) {
                if (curp.length < 18) {
                    showErrorDialog("Error", "La CURP debe tener al menos 18 caracteres")
                } else {
                    startLoading()
                    loginViewModel.login(ILogin(email, curp))
                }
            } else {
                showErrorDialog("Error", "Por favor ingresa correo y CURP")
            }
        }

        loginViewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            stopLoading()
            response?.let {
                showSuccessDialog("Bienvenido", it.message)
                saveUserSession(it.id, it.esAdministrador, it.nombresCompletos)
                navigateToFragment(it.esAdministrador)
            }
        }

        loginViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            stopLoading()
            showErrorDialog("Error de autenticación", errorMessage)
        }

        return view
    }

    private fun checkSession() {
        val userId = sharedPreferences.getString("USER_ID", null)
        val isAdmin = sharedPreferences.getBoolean("IS_ADMIN", false)
        // 📌 Mostrar el ID guardado en el Logcat
        Log.d("SharedPreferences", "ID guardado en sesión: ${userId ?: "No hay ID guardado"}")

        if (userId != null) {
            navigateToFragment(isAdmin)
        }
    }

    private fun saveUserSession(userId: String, isAdmin: Boolean, userName: String) {
        sharedPreferences.edit()
            .putString("USER_ID", userId)
            .putString("USER_NAME", userName)
            .putBoolean("IS_ADMIN", isAdmin)
            .apply()
        // 📌 Confirmar que el ID se guardó correctamente en Logcat
        Log.d("SharedPreferences", "ID guardado correctamente: $userId")
        Log.d("SharedPreferences", "Nombre guardado: $userName")
        Log.d("SharedPreferences", "Es administrador: $isAdmin")

    }

    private fun navigateToFragment(isAdmin: Boolean) {
        val fragment = if (isAdmin) AdminFragment() else CallFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun startLoading() {
        lottieLoading.visibility = View.VISIBLE
        lottieLoading.playAnimation()
        loginButton.isEnabled = false
    }

    private fun stopLoading() {
        lottieLoading.visibility = View.GONE
        lottieLoading.cancelAnimation()
        loginButton.isEnabled = true
    }

    private fun showSuccessDialog(title: String, message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmText("OK")
            .show()
    }

    private fun showErrorDialog(title: String, message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText(title)
            .setContentText(message)
            .setConfirmText("Entendido")
            .show()
    }
}
