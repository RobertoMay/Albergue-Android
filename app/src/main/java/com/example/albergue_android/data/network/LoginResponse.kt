package com.example.albergue_android.data.network

import com.google.gson.annotations.SerializedName

data class LoginResponse (

    @SerializedName("message") val message: String,
    @SerializedName("token") val token: String,
    @SerializedName("id") val id: String,
    @SerializedName("nombresCompletos") val nombresCompletos: String,
    @SerializedName("esAdministrador") val esAdministrador: Boolean


)

