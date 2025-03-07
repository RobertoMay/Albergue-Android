package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.DataStudent
import com.google.gson.annotations.SerializedName

data class StudentResponse(
    @SerializedName("error") val error: Boolean, // Indica si hubo un error
    @SerializedName("msg") val message: String,  // Mensaje de la respuesta
    @SerializedName("data") val data: DataStudent? = null // Datos del estudiante (opcional)
)