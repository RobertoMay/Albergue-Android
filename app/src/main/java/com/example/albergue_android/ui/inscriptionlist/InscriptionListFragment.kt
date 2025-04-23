package com.example.albergue_android.ui.inscriptionlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.databinding.FragmentApplicantsListBinding
import com.example.albergue_android.domain.models.Call
import com.example.albergue_android.domain.models.StudentDocDocument
import com.example.albergue_android.ui.adapters.StudentsAdapter
import com.example.albergue_android.ui.documentsdialog.DocumentsDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InscriptionListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InscriptionListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentApplicantsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var studentsAdapter: StudentsAdapter
    private var currentPage = 1
    private val pageSize = 20
    private var searchNameInput: String? = null
    private lateinit var lottieLoading: LottieAnimationView

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
        _binding = FragmentApplicantsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lottieLoading = view.findViewById(R.id.lottieLoading)

        // Configurar RecyclerView
        studentsAdapter = StudentsAdapter(emptyList()) {student -> showDocumentsDialog(student)}
        binding.rvStudents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStudents.adapter = studentsAdapter

        loadActiveCall()

        // Configurar la barra de búsqueda
        setupSearchBar()


        // Configurar paginación
        setupPagination()

        // Simular carga inicial de datos
        loadStudents()
    }

    private fun setupSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchNameInput = binding.etSearch.text.toString().trim()
                currentPage = 1 //
                loadStudents()
                true
            } else {
                false
            }
        }
    }

    private fun setupPagination() {
        binding.btnPrevious.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePageNumber()
                loadStudents()
            }
        }

        binding.btnNext.setOnClickListener {
            currentPage++
            updatePageNumber()
            loadStudents()
        }

        updatePageNumber()
    }

    private fun updatePageNumber() {
        binding.tvPageNumber.text = currentPage.toString()

        binding.btnPrevious.isEnabled = currentPage > 1

        binding.btnNext.isEnabled = studentsAdapter.itemCount > 0
    }
    private fun loadActiveCall() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.callService.getActiveCall()
                if (response.isSuccessful) {
                    val callResponse = response.body()
                    if (callResponse != null && callResponse.convocatoria != null) {
                        // Actualizar la UI con los datos de la convocatoria
                        withContext(Dispatchers.Main) {
                            updateUI(callResponse.convocatoria)
                        }
                    } else {
                        // No se encontró una convocatoria activa
                        withContext(Dispatchers.Main) {
                            showError("No se encontró una convocatoria activa")
                        }
                    }
                } else {
                    // Error en la respuesta del servidor
                    withContext(Dispatchers.Main) {
                        showError("Error al obtener la convocatoria activa")
                    }
                }
            } catch (e: Exception) {
                // Error de conexión
                withContext(Dispatchers.Main) {
                    showError("Error de conexión: ${e.message}")
                }
            }
        }
    }

    private fun updateUI(call: Call) {
        // Calcular el porcentaje de ocupación
        val occupiedPercentage = calculateOccupiedPercentage(call.occupiedCupo, call.availableCupo)

        // Actualizar la barra de progreso
        binding.progressBar.progress = occupiedPercentage

        // Actualizar las estadísticas de cupos
        binding.tvAvailableCupos.text = "Cupos Disponibles: ${call.availableCupo}"
        binding.tvOccupiedCupos.text = "Cupos Ocupados: ${call.occupiedCupo}"
    }

    private fun calculateOccupiedPercentage(occupiedCupo: Int?, availableCupo: Int?): Int {
        if (occupiedCupo == null || availableCupo == null) return 0
        val totalCupo = occupiedCupo + availableCupo
        return ((occupiedCupo.toDouble() / totalCupo) * 100).toInt()
    }

    private fun showError(message: String) {
        // Mostrar un mensaje de error (puedes usar un Toast o un Snackbar)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun loadStudents() {
        showLoading(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.studentService.getEnrolledStudents(currentPage, searchNameInput)
                if (response.isSuccessful) {
                    val students = response.body()
                    if (students != null) {
                        withContext(Dispatchers.Main) {
                            studentsAdapter.updateData(students)

                            binding.btnNext.isEnabled = students.isNotEmpty()

                            if (students.isEmpty()) {
                                showError("No hay más estudiantes para mostrar")
                                currentPage--
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showError("No se encontraron estudiantes no inscritos")
                            binding.btnNext.isEnabled = false
                            currentPage--
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.btnNext.isEnabled = false
                        showError("No se encontraron más estudiantes")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Error de conexión: ${e.message}")
                    binding.btnNext.isEnabled = false
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.lottieLoading.visibility = View.VISIBLE
            binding.lottieLoading.playAnimation()
        } else {
            binding.lottieLoading.visibility = View.GONE
            binding.lottieLoading.cancelAnimation()
        }
        // Deshabilitar botones mientras se carga
        binding.btnPrevious.isEnabled = !show
        binding.btnNext.isEnabled = !show
    }

    private fun showDocumentsDialog(student: StudentDocDocument) {
        // Obtener los documentos del estudiante (debes adaptar esto según tu API)
        val studentDocuments = student.Documents?.filter { !it.type.contains("_tutor") } ?: emptyList()
        val tutorDocuments = student.Documents?.filter { it.type.contains("_tutor") } ?: emptyList()

        val dialog = DocumentsDialogFragment.newInstance(
            studentName = student.name ?: "Estudiante",
            isEnrolled = student.enrollmentStatus ?: false,
            studentDocuments = studentDocuments,
            tutorDocuments = tutorDocuments,
            studentId = student.aspiranteId ?: ""
        )

        dialog.show(parentFragmentManager, "DocumentsDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}