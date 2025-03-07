package com.example.albergue_android.domain.models

data class IRegistration(
    val nombresCompletos: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val curp: String,
    val correo: String,
    val periodoInscripcion: String? = null,
    val statusInscripcion: Boolean? = null
)
