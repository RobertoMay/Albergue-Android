package com.example.albergue_android.ui.alumn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.albergue_android.R

import android.widget.TextView




class AlumnoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alumn, container, false)
        view.findViewById<TextView>(R.id.textViewAlumno).text = "Alumno"
        return view
    }
}
