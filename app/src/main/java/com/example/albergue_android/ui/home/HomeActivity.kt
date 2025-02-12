package com.example.albergue_android.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.albergue_android.R
import androidx.activity.ComponentActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.denzcoskun.imageslider.constants.ScaleTypes

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider);
        val imageList = ArrayList<SlideModel>();

        imageList.add(SlideModel(R.drawable.i1, scaleType = ScaleTypes.CENTER_CROP));
        imageList.add(SlideModel(R.drawable.i2, scaleType = ScaleTypes.CENTER_CROP));
        imageList.add(SlideModel(R.drawable.i3, scaleType = ScaleTypes.CENTER_CROP));

//        imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."))
//        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."))
//        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."))
        imageSlider.setImageList(imageList)
    }
}