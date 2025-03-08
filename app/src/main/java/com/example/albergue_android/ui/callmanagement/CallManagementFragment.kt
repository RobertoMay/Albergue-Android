package com.example.albergue_android.ui.callmanagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.databinding.FragmentCallManagementBinding
import com.example.albergue_android.domain.models.Call
import com.example.albergue_android.domain.models.CallResponse
import com.example.albergue_android.ui.adapters.CallsAdapter
import com.example.albergue_android.ui.viewmodels.CallManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CallManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallManagementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: CallManagementViewModel
    private lateinit var binding: FragmentCallManagementBinding
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var callsAdapter: CallsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        binding = FragmentCallManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this).get(CallManagementViewModel::class.java)

        // Configurar RecyclerView
        callsAdapter = CallsAdapter { call -> showDeleteConfirmationDialog(call) }
        binding.callsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.callsRecyclerView.adapter = callsAdapter

        // Observar cambios en la lista de convocatorias
        viewModel.calls.observe(viewLifecycleOwner) { calls ->
            callsAdapter.submitList(calls)
        }


        // Observar errores y mostrar diálogos de SweetAlert
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage == "Convocatoria eliminada exitosamente") {
                SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Éxito")
                    .setContentText(errorMessage)
                    .setConfirmText("OK")
                    .setConfirmClickListener { dialog -> dialog.dismiss() }
                    .show()
            } else if (errorMessage.startsWith("Error")) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText(errorMessage)
                    .setConfirmText("OK")
                    .setConfirmClickListener { dialog -> dialog.dismiss() }
                    .show()
            }
        }

        // Configurar botones de filtro
        binding.applyFilterButton.setOnClickListener {
            val year = binding.filterYearInput.text.toString().toIntOrNull()
            viewModel.applyYearFilter(year)
        }

        binding.resetFilterButton.setOnClickListener {
            binding.filterYearInput.text.clear()
            viewModel.resetFilter()
        }

        viewModel.loadCalls()


        // Configurar botones y campos de texto
        binding.createButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val cupo = binding.cupoInput.text.toString().toIntOrNull() ?: 0
            val startDate = binding.startDateInput.text.toString()
            val endDate = binding.endDateInput.text.toString()

            if (title.isNotEmpty() && cupo > 0 && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                viewModel.createCall(title, cupo, startDate, endDate)
                binding.titleInput.text.clear()
                binding.cupoInput.text.clear()
                binding.startDateInput.text.clear()
                binding.endDateInput.text.clear()
            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Capitalizar la primera letra del título
        binding.titleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s[0].isLowerCase()) {
                    binding.titleInput.setText(s.toString().replaceFirstChar { it.uppercase() })
                    binding.titleInput.setSelection(binding.titleInput.text.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        binding.cancelButton.setOnClickListener {
            binding.titleInput.text.clear()
            binding.cupoInput.text.clear()
            binding.startDateInput.text.clear()
            binding.endDateInput.text.clear()
        }

        // Botón para crear convocatoria
        binding.createButton.setOnClickListener {
            createCall()
        }
    }

    private fun convertToEnglishDate(mexicanDate: String): String? {
        return try {
            // Definir los formatos de entrada posibles
            val mexicanFormatShort = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val mexicanFormatLong = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Intentar parsear la fecha en ambos formatos
            val date = mexicanFormatShort.parse(mexicanDate) ?: mexicanFormatLong.parse(mexicanDate)

            // Formatear la fecha al formato inglés (MM/dd/yyyy)
            val englishFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            englishFormat.format(date!!)
        } catch (e: Exception) {
            null // Si la fecha no es válida, retornar null
        }
    }

    private fun createCall() {
        val title = binding.titleInput.text.toString()
        val cupo = binding.cupoInput.text.toString().toIntOrNull() ?: 0
        val startDate = binding.startDateInput.text.toString()
        val endDate = binding.endDateInput.text.toString()

        // Convertir fechas al formato inglés
        val startDateEnglish = convertToEnglishDate(startDate)
        val endDateEnglish = convertToEnglishDate(endDate)

        // Validar campos
        if (title.isEmpty() || cupo <= 0 || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar fechas
        if (startDateEnglish == null || endDateEnglish == null) {
            Toast.makeText(requireContext(), "Formato de fecha inválido. Use dd/MM/yy o dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        if (startDateEnglish > endDateEnglish) {
            Toast.makeText(requireContext(), "La fecha de inicio no puede ser mayor a la fecha de cierre", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto Call
        val call = Call(
            title = title,
            status = true,
            startDate = startDateEnglish.toString(),
            endDate = endDateEnglish.toString(),
            cupo = cupo
        )
        // Mostrar animación de carga
        showLoading(true)

        // Llamar al servicio para crear la convocatoria
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<CallResponse> = ApiClient.callService.createCall(call)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    handleResponse(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                    showErrorDialog("Error de conexión: ${e.message}")

                }
            }
        }
    }

    private fun handleResponse(response: Response<CallResponse>) {
        if (response.isSuccessful) {
            val callResponse = response.body()
            if (callResponse != null) {
                showSuccessDialog(callResponse.message)
                // Limpiar formulario
                binding.titleInput.text.clear()
                binding.cupoInput.text.clear()
                binding.startDateInput.text.clear()
                binding.endDateInput.text.clear()
                viewModel.loadCalls()
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
            showErrorDialog("Error: $errorMessage")
        }
    }

    // Mostrar u ocultar animación de carga
    private fun showLoading(show: Boolean) {
        if (show) {
            binding.lottieLoading.visibility = View.VISIBLE
            binding.lottieLoading.playAnimation()
        } else {
            binding.lottieLoading.visibility = View.GONE
            binding.lottieLoading.cancelAnimation()
        }
        binding.createButton.isEnabled = !show
    }

    // Mostrar diálogo de éxito
    private fun showSuccessDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Convocatoria creada")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog -> dialog.dismiss() }
            .show()
    }

    // Mostrar diálogo de error
    private fun showErrorDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog -> dialog.dismiss() }
            .show()
    }

    private fun showDeleteConfirmationDialog(callId: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Estás seguro?")
            .setContentText("Esta acción eliminará la convocatoria.")
            .setConfirmText("Sí")
            .setCancelText("Cancelar")
            .setConfirmClickListener { dialog ->
                viewModel.deleteCall(callId)
                dialog.dismissWithAnimation()
            }
            .setCancelClickListener { it.dismissWithAnimation() }
            .show()
    }

}