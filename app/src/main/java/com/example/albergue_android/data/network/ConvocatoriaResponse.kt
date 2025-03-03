package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.Convocatoria
import com.google.gson.annotations.SerializedName

data class ConvocatoriaResponse(
    @SerializedName("message") val message: String,
    @SerializedName("convocatoria") val convocatoria: Convocatoria? = null,
)