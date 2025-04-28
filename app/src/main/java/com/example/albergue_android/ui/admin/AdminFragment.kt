package com.example.albergue_android.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.albergue_android.R
import com.example.albergue_android.data.network.ApiClient
import com.example.albergue_android.data.network.DashboardDataResponse
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Color

class AdminFragment : Fragment() {

    private lateinit var textViewWelcome: TextView
    private lateinit var textViewStudents: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var pieChartInscritos: PieChart
    private lateinit var pieChartDocumentacion: PieChart
    private lateinit var pieChartAlbergue: PieChart
    private lateinit var barChartGenero: HorizontalBarChart
    private lateinit var contentLayout: ViewGroup // Agregado para manejar todo el contenido
    private lateinit var textViewNoStudents: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        textViewWelcome = view.findViewById(R.id.textViewWelcome)
        textViewStudents = view.findViewById(R.id.textViewStudents)
        progressBar = view.findViewById(R.id.progressBar)
        pieChartInscritos = view.findViewById(R.id.pieChartInscritos)
        pieChartDocumentacion = view.findViewById(R.id.pieChartDocumentacion)
        pieChartAlbergue = view.findViewById(R.id.pieChartAlbergue)
        barChartGenero = view.findViewById(R.id.barChartGenero)
        contentLayout = view.findViewById(R.id.contentLayout) // Agregado
        textViewNoStudents = view.findViewById(R.id.textViewNoStudents)


        // Inicialmente ocultamos todo el contenido, solo mostramos el ProgressBar
        contentLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        loadDashboardData()

        return view
    }

    private fun loadDashboardData() {



        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiClient.adminService.getDashboardData()

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE // Ocultar ProgressBar

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!

                    // Mostrar ahora el contenido
                    contentLayout.visibility = View.VISIBLE

                    if (data.alumnosInscritos == 0) {
                        // Mostrar alerta
                        showNoStudentsAlert()

                        // Mostrar mensaje en pantalla
                        textViewNoStudents.visibility = View.VISIBLE
                    }else{
                        textViewNoStudents.visibility = View.GONE
                    }


                    textViewWelcome.text = "Bienvenido, ${data.adminName}"
                    textViewStudents.text = "Total alumnos inscritos: ${data.alumnosInscritos}"

                    setupPieChartInscritos(data)
                    setupPieChartDocumentacion(data)
                    setupPieChartAlbergue(data)
                    setupBarChartGenero(data)

                } else {
                    Toast.makeText(requireContext(), "Error al cargar dashboard", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupPieChartInscritos(data: DashboardDataResponse) {
        val entries = listOf(
            PieEntry(data.alumnos.inscritos.toFloat(), "Inscritos"),
            PieEntry(data.alumnos.porInscribirse.toFloat(), "Por inscribirse")
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"),
            Color.parseColor("#FFC107")
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16f

        val pieData = PieData(dataSet)
        pieChartInscritos.data = pieData
        pieChartInscritos.description.isEnabled = false
        pieChartInscritos.centerText = "Inscripción"
        pieChartInscritos.setCenterTextSize(20f)
        pieChartInscritos.setEntryLabelColor(Color.BLACK)
        pieChartInscritos.invalidate()


        // Ajustar leyenda
        val legend = pieChartInscritos.legend
        legend.textSize = 14f
        legend.formSize = 14f
    }

    private fun setupPieChartDocumentacion(data: DashboardDataResponse) {
        val entries = listOf(
            PieEntry(data.documentacion.encuestaNoContestada.toFloat(), "Encuesta no contestada"),
            PieEntry(data.documentacion.encuestaContestada.toFloat(), "Encuesta contestada"),
            PieEntry(data.documentacion.documentosCompletos.toFloat(), "Documentos completos")
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#F44336"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#8BC34A")
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16f

        val pieData = PieData(dataSet)
        pieChartDocumentacion.data = pieData
        pieChartDocumentacion.description.isEnabled = false
        pieChartDocumentacion.centerText = "Documentos"
        pieChartDocumentacion.setCenterTextSize(20f)
        pieChartDocumentacion.setEntryLabelColor(Color.BLACK)
        pieChartDocumentacion.invalidate()

        // Ajustar leyenda
        val legend = pieChartDocumentacion.legend
        legend.textSize = 14f
        legend.formSize = 14f
    }

    private fun setupPieChartAlbergue(data: DashboardDataResponse) {
        val entries = listOf(
            PieEntry(data.albergue.plazasOcupadas.toFloat(), "Ocupadas"),
            PieEntry(data.albergue.plazasDisponibles.toFloat(), "Disponibles")
        )
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#9C27B0"),
            Color.parseColor("#00BCD4")
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16f

        val pieData = PieData(dataSet)
        pieChartAlbergue.data = pieData
        pieChartAlbergue.description.isEnabled = false
        pieChartAlbergue.centerText = "Plazas"
        pieChartAlbergue.setCenterTextSize(20f)
        pieChartAlbergue.setEntryLabelColor(Color.BLACK)
        pieChartAlbergue.invalidate()

        // Ajustar leyenda
        val legend = pieChartAlbergue.legend
        legend.textSize = 14f
        legend.formSize = 14f
    }

    /*private fun setupBarChartGenero(data: DashboardDataResponse) {
        val entries = listOf(
            BarEntry(0f, data.distribucionGenero.hombres.toFloat()),
            BarEntry(1f, data.distribucionGenero.mujeres.toFloat()),
            BarEntry(2f, data.distribucionGenero.otros.toFloat())
        )
        val dataSet = BarDataSet(entries, "Género")
        val barData = BarData(dataSet)
        barChartGenero.data = barData
        barChartGenero.invalidate()
    }*/

    private fun setupBarChartGenero(data: DashboardDataResponse) {
        val total = data.distribucionGenero.hombres + data.distribucionGenero.mujeres + data.distribucionGenero.otros

        val entries = listOf(
            BarEntry(0f, data.distribucionGenero.hombres.toFloat()),
            BarEntry(1f, data.distribucionGenero.mujeres.toFloat()),
            BarEntry(2f, data.distribucionGenero.otros.toFloat())
        )

        val dataSet = BarDataSet(entries, "Distribución por género")
        dataSet.colors = listOf(
            Color.parseColor("#42A5F5"), // Azul - Hombres
            Color.parseColor("#EC407A"), // Rosa - Mujeres
            Color.parseColor("#AB47BC")  // Morado - Otro
        )
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                barEntry ?: return ""
                val porcentaje = if (total != 0) (barEntry.y / total * 100) else 0f
                return "${barEntry.y.toInt()} (${String.format("%.1f", porcentaje)}%)"
            }
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        barChartGenero.data = barData

        // Estética general
        barChartGenero.description.isEnabled = false
        barChartGenero.setDrawGridBackground(false)
        barChartGenero.setDrawValueAboveBar(true)
        barChartGenero.setFitBars(true)

        // Ejes
        val xAxis = barChartGenero.xAxis
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> "Hombres"
                    1 -> "Mujeres"
                    2 -> "Otro"
                    else -> ""
                }
            }
        }
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.textSize = 14f

        val yAxisLeft = barChartGenero.axisLeft
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.axisMinimum = 0f // No negativos
        yAxisLeft.textSize = 14f

        val yAxisRight = barChartGenero.axisRight
        yAxisRight.isEnabled = false

        barChartGenero.legend.isEnabled = false // No necesitamos la leyenda, ya lo dice el texto
        barChartGenero.invalidate()
    }


    private fun showNoStudentsAlert() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sin alumnos inscritos")
            .setMessage("Actualmente no hay alumnos inscritos. Por favor, verifica más tarde.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

}
