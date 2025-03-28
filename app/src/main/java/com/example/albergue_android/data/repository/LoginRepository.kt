package com.example.albergue_android.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.LoginResponse
import com.example.albergue_android.domain.models.ILogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val sharedPreferences: SharedPreferences) {



    fun login(credentials: ILogin, onResult: (LoginResponse?) -> Unit, onError: (String) -> Unit) {
        ApiClient.authenticationService.login(credentials).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loginResponse?.let {
                        saveToken(it.token)  // Guarda el token en SharedPreferences
                        onResult(it)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error: ${response.code()}"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Fallo de conexión: ${t.message}")
            }
        })
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString("TOKEN", token)
            .apply()
    }
}
