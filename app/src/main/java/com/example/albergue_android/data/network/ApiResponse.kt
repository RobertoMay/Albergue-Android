package com.example.albergue_android.data.network

import com.example.albergue_android.domain.models.IRegistration
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("message") val message: String,
    @SerializedName("aspirante") val aspirante: IRegistration? = null,
    @SerializedName("details") val details: String? = null
)
