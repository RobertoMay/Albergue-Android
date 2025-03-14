package com.example.albergue_android.data.repository

import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.LoginResponse
import com.example.albergue_android.domain.models.ILogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    fun login(credentials: ILogin, onResult: (LoginResponse?) -> Unit, onError: (String) -> Unit) {
        ApiClient.authenticationService.login(credentials).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }
}
