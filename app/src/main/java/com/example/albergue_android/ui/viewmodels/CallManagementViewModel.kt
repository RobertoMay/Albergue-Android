package com.example.albergue_android.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.domain.models.Call
import kotlinx.coroutines.launch

class CallManagementViewModel : ViewModel() {

    // LiveData para observar la lista de convocatorias
    private val _calls = MutableLiveData<List<Call>>()
    val calls: LiveData<List<Call>> get() = _calls

    // LiveData para manejar errores
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Lista original de convocatorias (sin filtrar)
    private var originalCalls: List<Call> = emptyList()

    // Filtro por año
    private var filterYear: Int? = null

    init {
        loadCalls()
    }

    // Cargar todas las convocatorias
    fun loadCalls() {
        viewModelScope.launch {
            try {
                val response = ApiClient.callService.getAllCalls()
                if (response.isSuccessful) {
                    val callResponse = response.body()
                    if (callResponse != null) {
                        originalCalls = callResponse.convocatorias
                        _calls.value = originalCalls.sortedByDescending { it.startDate }
                    } else {
                        _errorMessage.value = "No se encontraron convocatorias"
                    }
                } else {
                    _errorMessage.value = "Error al cargar las convocatorias"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // Función para crear una nueva convocatoria
    fun createCall(title: String, cupo: Int, startDate: String, endDate: String) {
        val newCall = Call(
            title = title,
            status = true,
            startDate = startDate,
            endDate = endDate,
            cupo = cupo
        )
        _calls.value = _calls.value?.plus(newCall) ?: listOf(newCall)
    }

    // Aplicar filtro por año
    fun applyYearFilter(year: Int?) {
        filterYear = year
        if (year == null) {
            _calls.value = originalCalls // Mostrar todas las convocatorias si no hay filtro
        } else {
            _calls.value = originalCalls.filter { call ->
                val startYear = call.startDate.substring(0, 4).toIntOrNull() // Extraer el año de startDate
                val endYear = call.endDate.substring(0, 4).toIntOrNull() // Extraer el año de endDate
                startYear == year || endYear == year // Filtrar por año
            }
        }
    }

    // Eliminar una convocatoria
    fun deleteCall(callId: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.callService.deleteCall(callId)
                if (response.isSuccessful) {
                    _errorMessage.value = "Convocatoria eliminada exitosamente"
                    loadCalls() // Recargar la lista después de eliminar
                } else {
                    _errorMessage.value = "Error al eliminar la convocatoria"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    // Restablecer el filtro
    fun resetFilter() {
        filterYear = null
        _calls.value = originalCalls // Mostrar todas las convocatorias
    }
}