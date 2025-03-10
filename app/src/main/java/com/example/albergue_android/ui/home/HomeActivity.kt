package com.example.albergue_android.ui.home
import com.example.albergue_android.ui.about.AboutFragment
import com.example.albergue_android.ui.activities.ActivitiesFragment
import com.example.albergue_android.ui.registrationform.RegistrationFormFragment
import android.widget.ImageView


import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat

import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.albergue_android.R
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.albergue_android.ui.applicantslist.ApplicantsListFragment
import com.example.albergue_android.ui.editCall.EditCallFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Configura el Toolbar de la actividad
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Esto quita el título predeterminado



        // Configurar DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)


        // Encontrar el logo del INPI
        val logoInpi: ImageView = findViewById(R.id.logo_inpi)

        // Agregar clic para redirigir al HomeFragment
        logoInpi.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null) // Permite volver atrás con el botón de retroceso
                .commit()
        }

        // Configurar NavigationView
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_about_us -> AboutFragment()
                R.id.nav_activities -> ActivitiesFragment()
               // R.id.nav_registration -> RegistrationFormFragment()
                // R.id.nav_login -> LoginFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .addToBackStack(null) // Permite volver atrás
                    .commit()
            }

            // Cierra el menú después de hacer clic en una opción
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        // Habilitar botón de menú hamburguesa
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)  // Ícono del menú hamburguesa
        }


        //  Verifica si ya hay un fragmento cargado antes de reemplazarlo
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

    }



   /** override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val fragment = when (item.itemId) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_about_us -> AboutFragment()
            R.id.nav_activities -> ActivitiesFragment()
            R.id.nav_registration -> RegistrationFormFragment()
            // R.id.nav_login -> LoginFragment()
            else -> null
        }

        fragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it)
                .addToBackStack(null) // Permite volver atrás
                .commit()
        }

        return super.onOptionsItemSelected(item)
    }**/





    // Manejar clic en el botón de menú hamburguesa
    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }



}