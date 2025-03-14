package com.example.albergue_android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.albergue_android.data.repository.LoginRepository
import com.example.albergue_android.data.network.LoginResponse
import com.example.albergue_android.domain.models.ILogin

class LoginViewModel : ViewModel() {

    private val loginRepository = LoginRepository()

    private val _loginResponse = MutableLiveData<LoginResponse?>()
    val loginResponse: LiveData<LoginResponse?> = _loginResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun login(credentials: ILogin) {
        loginRepository.login(credentials, { response ->
            _loginResponse.value = response
        }, { errorMessage ->
            _error.value = errorMessage
        })
    }
}
