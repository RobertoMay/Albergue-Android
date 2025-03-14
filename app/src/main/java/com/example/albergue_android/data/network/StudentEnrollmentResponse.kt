package com.example.albergue_android.data.network

data class StudentEnrollmentResponse(
    val error: Boolean,
    val msg: String,
    val data: StudentData?
)

data class StudentEnrollmentData(
    val aspiranteId: String,
    val aspiranteCurp: String,
    val data: StudentData
)

data class StudentData(
    val curp: String,
    val nombre: String,
    val primerApellido: String,
    val segundoApellido: String,
    val sexo: String,
    val telefonoFijo: String,
    val telefonoMovil: String,
    val correoElectronico: String,
    val fechaNacimiento: String,
    val puebloIndigena: String,
    val lenguaIndigena: String,
    val estado: String,
    val municipio: String,
    val localidad: String,
    val comunidad: String,
    val comunidadCasa: String,
    val localidadCasa: String,
    val estadoMadre: String,
    val curpMadre: String,
    val nombreMadre: String,
    val primerApellidoMadre: String,
    val segundoApellidoMadre: String,
    val fechaNacimientoMadre: String,
    val edoNacimientoMadre: String,
    val estadoPadre: String,
    val curpPadre: String,
    val nombrePadre: String,
    val primerApellidoPadre: String,
    val segundoApellidoPadre: String,
    val fechaNacimientoPadre: String,
    val edoNacimientoPadre: String,
    val parentescoTutor: String,
    val curpTutor: String,
    val nombreTutor: String,
    val primerApellidoTutor: String,
    val segundoApellidoTutor: String,
    val fechaNacimientoTutor: String,
    val edoNacimientoTutor: String,
    val centroCoordinador: String,
    val tipoCasa: String,
    val nombreCasa: String,
    val medioAcceso: String,
    val especifAcceso: String? = null,
    val riesgoAcceso: String,
    val especifRiesgo: String? = null,
    val discapacidad: String,
    val tipoDiscapacidad: String,
    val especifDiscapacidad: String? = null,
    val alergia: String,
    val alergiaDetalles: String? = null,
    val respirar: String,
    val respirarDetalles: String? = null,
    val tratamiento: String,
    val tratamientoDetalles: String? = null,
    val tipoEscuela: String,
    val cct: String,
    val nombreEscuela: String,
    val escolaridad: String,
    val otraesco: String? = null,
    val semestreoanosCursados: String,
    val tipoCurso: String,
    val solicitud: String
)