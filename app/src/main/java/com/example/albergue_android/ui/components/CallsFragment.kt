package com.example.albergue_android.ui.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.albergue_android.R
import com.example.albergue_android.domain.models.Convocatoria
import com.example.albergue_android.ui.registrationform.RegistrationFormFragment
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CallsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var convocatoria: Convocatoria

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
        val view = inflater.inflate(R.layout.fragment_calls, container, false)

        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val noConvocatoriaTextView = view.findViewById<TextView>(R.id.noConvocatoriaTextView)

        arguments?.let {
            convocatoria = it.getParcelable("convocatoria") ?: throw IllegalStateException("Convocatoria no encontrada")
        }

//        val convocatoria = Convocatoria(
//            id = "1",
//            title = "Convocatoria 2023",
//            startDate = "01/10/2023",
//            endDate = "31/10/2023",
//            cupo = 100,
//            status = true, // Cambia a false para simular que no hay convocatoria activa
//            occupiedCupo = 50,
//            availableCupo = 50
//        )

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        val startDateFormatted = dateFormat.format(SimpleDateFormat("yyyy-MM-dd").parse(convocatoria.startDate))
        val endDateFormatted = dateFormat.format(SimpleDateFormat("yyyy-MM-dd").parse(convocatoria.endDate))

        if (convocatoria.status) {
            titleTextView.text = convocatoria.title
            dateTextView.text = "Del $startDateFormatted al $endDateFormatted"
            registerButton.visibility = View.VISIBLE
            noConvocatoriaTextView.visibility = View.GONE
        } else {
            titleTextView.visibility = View.GONE
            dateTextView.visibility = View.GONE
            registerButton.visibility = View.GONE
            noConvocatoriaTextView.visibility = View.VISIBLE
        }

        // Configurar el botón de registro
        registerButton.setOnClickListener {
            // Abrir el modal de registro
            val registrationFormFragment = RegistrationFormFragment()
            registrationFormFragment.show(parentFragmentManager, "RegistrationFormFragment")
        }

        return view
    }


    companion object {
        fun newInstance(convocatoria: Convocatoria): CallsFragment {
            val fragment = CallsFragment()
            val args = Bundle()
            args.putParcelable("convocatoria", convocatoria)
            fragment.arguments = args
            return fragment
        }
    }

}