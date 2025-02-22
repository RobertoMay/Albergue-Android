package com.example.albergue_android.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.albergue_android.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider)
        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.i1, scaleType = ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.i2, scaleType = ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.i3, scaleType = ScaleTypes.CENTER_CROP))

        imageSlider.setImageList(imageList)

        val btnAbout = view.findViewById<Button>(R.id.btn_about)
        val btnActivities = view.findViewById<Button>(R.id.btn_activities)
        val btnRegistration = view.findViewById<Button>(R.id.btn_registration)

        // Asignar eventos de clic (por ahora no hacen nada)
        btnAbout.setOnClickListener { /* Aquí irá la navegación */ }
        btnActivities.setOnClickListener { /* Aquí irá la navegación */ }
        btnRegistration.setOnClickListener { /* Aquí irá la navegación */ }
    }

}