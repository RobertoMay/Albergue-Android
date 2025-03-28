package com.example.albergue_android.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.albergue_android.R
import com.example.albergue_android.ui.CallFragment
import com.example.albergue_android.ui.about.AboutFragment
import com.example.albergue_android.ui.activities.ActivitiesFragment
import com.example.albergue_android.ui.admin.AdminFragment
import com.example.albergue_android.ui.applicantslist.ApplicantsListFragment
import com.example.albergue_android.ui.authentication.LoginFragment
import com.example.albergue_android.ui.editCall.EditCallFragment
import com.example.albergue_android.ui.components.InscriptionFragment
import com.example.albergue_android.ui.callmanagement.CallManagementFragment
import com.example.albergue_android.ui.registrationform.RegistrationFormFragment
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isInHome: Boolean = false
    private var isLoggedIn: Boolean = false
    private var isAdmin: Boolean = false
    private var isMenuGeneral: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        val appClosed = sharedPreferences.getBoolean("APP_CLOSED", false)

        if (appClosed) {
            isMenuGeneral = true
            sharedPreferences.edit().putBoolean("APP_CLOSED", false).apply()
        }

        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        checkAuthStatus()
        setupNavigationMenu()

        // REGISTRO DEL BROADCAST RECEIVER CORREGIDO (CAMBIOS AQUÍ)
        val filter = IntentFilter("UPDATE_NAV_MENU")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13+ (API 33+)
            registerReceiver(updateNavMenuReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            // Para versiones anteriores
            registerReceiver(updateNavMenuReceiver, filter)
        }

        val logoInpi: ImageView = findViewById(R.id.logo_inpi)
        logoInpi.setOnClickListener {
            goToHomeAndSetGeneralMenu()
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("APP_CLOSED", true).apply()
    }

    private val updateNavMenuReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println("DEBUG: Recibido evento UPDATE_NAV_MENU, actualizando menú...")
            checkAuthStatus()
            setupNavigationMenu()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(updateNavMenuReceiver)
    }

    private fun checkAuthStatus() {
        val sharedPreferences = getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        isLoggedIn = sharedPreferences.contains("USER_ID")
        isAdmin = sharedPreferences.getBoolean("IS_ADMIN", false)
        println("DEBUG: isLoggedIn = $isLoggedIn, isAdmin = $isAdmin")
    }

    private fun goToHomeAndSetGeneralMenu() {
        println("DEBUG: Cambiando al MENÚ GENERAL")
        isMenuGeneral = true
        isInHome = true

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()

        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        navigationView.menu.clear()

        when {
            isMenuGeneral -> {
                navigationView.inflateMenu(R.menu.navigation_menu)
                println("DEBUG: setupNavigationMenu -> Cargando MENÚ GENERAL")
            }
            isLoggedIn -> {
                println("DEBUG: setupNavigationMenu -> Usuario autenticado, isAdmin = $isAdmin")
                if (isAdmin) {
                    navigationView.inflateMenu(R.menu.menu_promotor)
                    println("DEBUG: setupNavigationMenu -> Cargando MENÚ ADMINISTRADOR")
                } else {
                    navigationView.inflateMenu(R.menu.menu_estudiante)
                    println("DEBUG: setupNavigationMenu -> Cargando MENÚ ESTUDIANTE")
                }
            }
            else -> {
                println("DEBUG: setupNavigationMenu -> Usuario NO autenticado, mostrando MENÚ GENERAL")
                navigationView.inflateMenu(R.menu.navigation_menu)
            }
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            println("DEBUG: Menú seleccionado -> ${menuItem.title}")
            val fragment = when (menuItem.itemId) {
                R.id.nav_home -> {
                    println("DEBUG: Opción seleccionada: Home")
                    val selectedFragment = when {
                        isMenuGeneral -> HomeFragment().also { isInHome = true }
                        isLoggedIn -> if (isAdmin) AdminFragment() else CallFragment()
                        else -> HomeFragment().also { isInHome = true }
                    }

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit()

                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_about_us -> AboutFragment().also { isInHome = false }
                R.id.nav_activities -> ActivitiesFragment().also { isInHome = false }
                R.id.nav_registration -> RegistrationFormFragment().also { isInHome = false }
                R.id.nav_login -> {
                    println("DEBUG: Opción seleccionada: Login")
                    if (isLoggedIn) {
                        isMenuGeneral = false
                        val fragment = if (isAdmin) AdminFragment() else CallFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                        setupNavigationMenu()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        return@setNavigationItemSelectedListener true
                    } else {
                        LoginFragment().also { isInHome = false }
                    }
                }
                R.id.nav_por_inscribirse -> ApplicantsListFragment().also { isInHome = false }
                R.id.nav_inscritos -> InscriptionFragment().also { isInHome = false }
                R.id.nav_crear -> CallManagementFragment().also { isInHome = false }
                R.id.nav_editar -> EditCallFragment().also { isInHome = false }
                R.id.nav_logout -> {
                    logout()
                    return@setNavigationItemSelectedListener true
                }
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it)
                    .addToBackStack(null)
                    .commit()
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun logout() {
        println("DEBUG: Cerrando sesión...")
        val sharedPreferences = getSharedPreferences("AlberguePrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        isMenuGeneral = false
        checkAuthStatus()
        setupNavigationMenu()

        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        drawerLayout.closeDrawer(GravityCompat.START)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }
}