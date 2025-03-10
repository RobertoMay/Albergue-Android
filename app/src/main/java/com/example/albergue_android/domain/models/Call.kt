package com.example.albergue_android.domain.models

data class CallResponse(
    val message: String,
    val convocatoria: Call
)

data class CallResponse2(
    val message: String,
    val convocatorias: List<Call> // Lista de convocatorias
)

data class Call(
    val id: String? = null,
    val title: String,
    val status: Boolean,
    val startDate: String,
    val endDate: String,
    val cupo: Int,
    val occupiedCupo: Int? = null,
    val availableCupo: Int? = null
)