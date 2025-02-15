package com.example.albergue_android.ui.registration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.albergue_android.R
import com.example.albergue_android.ui.registrationform.RegistrationFormFragment
import androidx.appcompat.app.AppCompatDelegate

class registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        // Llamar al Fragment que contiene el formulario
        if (savedInstanceState == null) {
            val fragment = RegistrationFormFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}