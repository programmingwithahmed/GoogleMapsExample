package com.programmingwithahmed.googlemapsexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.programmingwithahmed.googlemapsexample.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {

            btnMapWithMarker.setOnClickListener {

                val intent = Intent(this@StartActivity, MapsActivity::class.java)
                startActivity(intent)

            }

            btnMapWithRoute.setOnClickListener {

                val intent = Intent(this@StartActivity, MapsActivity2::class.java)
                startActivity(intent)

            }

        }
    }
}