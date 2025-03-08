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
import com.example.albergue_android.R
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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var errorMessageTextView: TextView
    private lateinit var updateButton: Button

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_call, container, false)
        // Inicializar vistas
        startDateText = view.findViewById(R.id.startDateText)
        endDateText = view.findViewById(R.id.endDateText)
        errorMessageTextView = view.findViewById(R.id.errorMessage)
        updateButton = view.findViewById(R.id.updateButton)

        val startDateButton: Button = view.findViewById(R.id.startDateButton)
        val endDateButton: Button = view.findViewById(R.id.endDateButton)

        // Configurar el listener del botón de fecha de inicio
        startDateButton.setOnClickListener {
            showDatePicker { selectedDate ->
                startDateText.text = dateFormat.format(selectedDate.time)
            }
        }

        // Configurar el listener del botón de fecha de fin
        endDateButton.setOnClickListener {
            showDatePicker { selectedDate ->
                endDateText.text = dateFormat.format(selectedDate.time)
            }
        }

        // Configurar el listener del botón de actualizar
        updateButton.setOnClickListener {
            validateDates()
        }

        return view
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

        val startDate = dateFormat.parse(startDateStr)
        val endDate = dateFormat.parse(endDateStr)

        if (startDate != null && endDate != null && startDate.before(endDate)) {
            errorMessageTextView.visibility = View.GONE
            Toast.makeText(requireContext(), "Fechas actualizadas correctamente", Toast.LENGTH_SHORT).show()
        } else {
            errorMessageTextView.visibility = View.VISIBLE
        }
    }
}