package com.example.albergue_android.domain.models

import java.util.Date

data class StudentDocDocument(
    val id: String? = null,
    val aspiranteId: String,
    val name: String,
    val lastName1: String,
    val lastName2: String,
    val email: String,
    val curp: String,
    val enrollmentPeriod: String,
    val enrollmentStatus: Boolean,
    val convocatoriaId: String,
    val Documents: List<StudentDocument>? = null,
    val fecha: Date,
    val hora: String
)

data class StudentDocument(
    val name: String,
    val type: String,
    val link: String,
    val date: Date,
    var status: String,
    val displayName: String
)
