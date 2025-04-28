package com.example.albergue_android.data.network

data class DashboardDataResponse(
    val adminName: String,
    val alumnosInscritos: Int,
    val alumnos: Alumnos,
    val documentacion: Documentacion,
    val albergue: Albergue,
    val distribucionGenero: DistribucionGenero
)

data class Alumnos(
    val total: Int,
    val inscritos: Int,
    val porInscribirse: Int
)

data class Documentacion(
    val totalPorInscribirse: Int,
    val encuestaContestada: Int,
    val encuestaNoContestada: Int,
    val documentosCompletos: Int
)

data class Albergue(
    val cupoTotal: Int,
    val plazasOcupadas: Int,
    val plazasDisponibles: Int
)

data class DistribucionGenero(
    val hombres: Int,
    val mujeres: Int,
    val otros: Int
)