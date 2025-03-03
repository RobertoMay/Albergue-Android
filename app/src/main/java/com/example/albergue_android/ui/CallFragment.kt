package com.example.albergue_android.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.ConvocatoriaResponse
import com.example.albergue_android.data.network.ConvocatoriaService
import com.example.albergue_android.ui.components.InscriptionFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CallFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var convocatoriaService: ConvocatoriaService
    private lateinit var tvConvocatoriaTitle: TextView
    private lateinit var tvConvocatoriaDates: TextView
    private lateinit var tvConvocatoriaDescription: TextView
    private lateinit var btnRegister: Button


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
        val view = inflater.inflate(R.layout.fragment_call, container, false)

        tvConvocatoriaTitle = view.findViewById(R.id.tvConvocatoriaTitle)
        tvConvocatoriaDates = view.findViewById(R.id.tvConvocatoriaDates)
        tvConvocatoriaDescription = view.findViewById(R.id.tvConvocatoriaDescription)
        btnRegister = view.findViewById(R.id.btnInscribirse)

        convocatoriaService = ApiClient.createService(ConvocatoriaService::class.java)

        checkConvocatoriaStatus()

        return view
    }

    private fun checkConvocatoriaStatus() {
        convocatoriaService.getCurrentAnnouncement().enqueue(object : Callback<ConvocatoriaResponse> {
            override fun onResponse(call: Call<ConvocatoriaResponse>, response: Response<ConvocatoriaResponse>) {
                if (response.isSuccessful && response.body()?.convocatoria?.status == true) {
                    val convocatoria = response.body()?.convocatoria
                    tvConvocatoriaTitle.text = convocatoria?.title

                    val startDate = convertDateToMexicanFormat(convocatoria?.startDate)
                    val endDate = convertDateToMexicanFormat(convocatoria?.endDate)

                    tvConvocatoriaDates.text = "Del $startDate al $endDate"
                    tvConvocatoriaDescription.text = "El albergue ofrece alojamiento gratuito a estudiantes indígenas."


                    btnRegister.visibility = View.VISIBLE
                    btnRegister.setOnClickListener {
                        // Navegar al InscripcionFragment
                        val inscripcionFragment = InscriptionFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, inscripcionFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                } else {
                    Toast.makeText(requireContext(), "No hay convocatoria activa", Toast.LENGTH_SHORT).show()
                    btnRegister.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ConvocatoriaResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al cargar convocatoria", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun convertDateToMexicanFormat(date: String?): String {
        if (date.isNullOrEmpty()) return ""
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate ?: Date())
    }

}