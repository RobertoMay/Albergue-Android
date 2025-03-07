package com.example.albergue_android.ui.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.ConvocatoriaResponse
import com.example.albergue_android.data.network.ConvocatoriaService
import com.example.albergue_android.ui.components.CallsFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var convocatoriaService: ConvocatoriaService
    private lateinit var convocatoriaContainer: FrameLayout

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
        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        convocatoriaContainer = view.findViewById(R.id.convocatoriaContainer)

        convocatoriaService = ApiClient.createService(ConvocatoriaService::class.java)

        checkConvocatoriaStatus()

        return view
    }

    private fun checkConvocatoriaStatus() {
        convocatoriaService.getCurrentAnnouncement().enqueue(object : Callback<ConvocatoriaResponse> {
            override fun onResponse(call: Call<ConvocatoriaResponse>, response: Response<ConvocatoriaResponse>) {
                if (response.isSuccessful && response.body()?.convocatoria?.status == true) {
                    // Hay una convocatoria activa, mostramos el contenedor y agregamos el fragmento
                    convocatoriaContainer.visibility = View.VISIBLE

                    // Obtener la convocatoria del backend
                    val convocatoria = response.body()?.convocatoria
                    if (convocatoria != null) {
                        // Pasar la convocatoria al CallsFragment
                        val callsFragment = CallsFragment.newInstance(convocatoria)
                        childFragmentManager.beginTransaction().apply {
                            replace(R.id.convocatoriaContainer, callsFragment)
                            commit()
                        }
                    }
                } else {
                    // No hay convocatoria activa, ocultamos el contenedor
                    convocatoriaContainer.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ConvocatoriaResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al cargar convocatoria", Toast.LENGTH_SHORT).show()
                convocatoriaContainer.visibility = View.GONE
            }
        })
    }


}