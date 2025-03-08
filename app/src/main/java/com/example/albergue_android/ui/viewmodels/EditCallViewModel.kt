package com.example.albergue_android.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.domain.models.Call
import com.example.albergue_android.domain.models.CallResponse
import kotlinx.coroutines.launch

class EditCallViewModel : ViewModel() {

    private val _activeCall = MutableLiveData<Call?>()
    val activeCall: LiveData<Call?> get() = _activeCall

    // LiveData para manejar errores
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    // Cargar la convocatoria activa
    fun loadActiveCall() {
        viewModelScope.launch {
            try {
                val response = ApiClient.callService.getActiveCall()
                if (response.isSuccessful) {
                    val callResponse = response.body()
                    if (callResponse != null && callResponse.convocatoria != null) {
                        _activeCall.value = callResponse.convocatoria
                    } else {
                        _errorMessage.value = "No se encontró una convocatoria activa"
                    }
                } else {
                    _errorMessage.value = "Error al cargar la convocatoria activa"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // Actualizar la convocatoria
    fun updateCall(id: String, call: Call) {
        viewModelScope.launch {
            try {
                val response = ApiClient.callService.updateCall(id, call)
                if (response.isSuccessful) {
                    _updateSuccess.value = true
                    _errorMessage.value = "Convocatoria actualizada correctamente"
                } else {
                    _errorMessage.value = "Error al actualizar la convocatoria"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}