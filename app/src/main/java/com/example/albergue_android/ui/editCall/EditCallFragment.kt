package com.example.albergue_android.ui.editCall

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.albergue_android.R
import com.example.albergue_android.ui.viewmodels.EditCallViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditCallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditCallFragment : Fragment() {

    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var errorMessageTextView: TextView
    private lateinit var updateButton: Button

    private val calendar = Calendar.getInstance()
    private val dateFormatMexicano = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dateFormatEstadounidense = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato para enviar
    // Inicializar ViewModel
    private val viewModel: EditCallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        startDateText = view.findViewById(R.id.startDateText)
        endDateText = view.findViewById(R.id.endDateText)
        errorMessageTextView = view.findViewById(R.id.errorMessage)
        updateButton = view.findViewById(R.id.updateButton)

        // Configurar listeners
        setupListeners()

        // Observar la convocatoria activa
        viewModel.activeCall.observe(viewLifecycleOwner) { call ->
            if (call != null) {
                // Mostrar las fechas en formato mexicano
                startDateText.text = formatDate(call.startDate, "yyyy-MM-dd", "dd/MM/yyyy")
                endDateText.text = formatDate(call.endDate, "yyyy-MM-dd", "dd/MM/yyyy")
            }
        }

        // Observar errores
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                println("No se que pasa $errorMessage")
            }
        }

        // Observar éxito de actualización
        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                showSuccessDialog("Convocatoria actualizada correctamente")
            }
        }

        // Cargar la convocatoria activa al iniciar
        viewModel.loadActiveCall()
    }

    private fun setupListeners() {
        val startDateButton: Button = requireView().findViewById(R.id.startDateButton)
        val endDateButton: Button = requireView().findViewById(R.id.endDateButton)

        startDateButton.setOnClickListener {
            showDatePicker { selectedDate ->
                startDateText.text = dateFormatMexicano.format(selectedDate.time) // Mostrar en formato mexicano
            }
        }

        endDateButton.setOnClickListener {
            showDatePicker { selectedDate ->
                endDateText.text = dateFormatMexicano.format(selectedDate.time) // Mostrar en formato mexicano
            }
        }

        updateButton.setOnClickListener {
            validateDates()
        }
    }

    private fun showDatePicker(onDateSelected: (Calendar) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                onDateSelected(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun validateDates() {
        val startDateStr = startDateText.text.toString()
        val endDateStr = endDateText.text.toString()

        if (startDateStr == "Seleccionar fecha" || endDateStr == "Seleccionar fecha") {
            Toast.makeText(requireContext(), "Ambas fechas son requeridas", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir las fechas a formato estadounidense antes de validar
        val startDate = dateFormatMexicano.parse(startDateStr)
        val endDate = dateFormatMexicano.parse(endDateStr)

        if (startDate != null && endDate != null && startDate.before(endDate)) {
            errorMessageTextView.visibility = View.GONE
            // Convertir las fechas a formato estadounidense antes de enviar
            val startDateFormatted = dateFormatEstadounidense.format(startDate)
            val endDateFormatted = dateFormatEstadounidense.format(endDate)
            updateCall(startDateFormatted, endDateFormatted)
        } else {
            errorMessageTextView.visibility = View.VISIBLE
        }
    }

    private fun updateCall(startDate: String, endDate: String) {
        val activeCall = viewModel.activeCall.value
        if (activeCall != null) {
            val updatedCall = activeCall.copy(startDate = startDate, endDate = endDate)
            viewModel.updateCall(activeCall.id!!, updatedCall)
        } else {
            Toast.makeText(requireContext(), "No hay una convocatoria activa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuccessDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Éxito")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog -> dialog.dismiss() }
            .show()
    }

    // Función para formatear la fecha de un formato a otro
    private fun formatDate(date: String, inputPattern: String, outputPattern: String): String {
        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
        return outputFormat.format(inputFormat.parse(date)!!)
    }
}