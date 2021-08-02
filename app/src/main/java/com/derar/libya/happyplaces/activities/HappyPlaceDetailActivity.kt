package com.derar.libya.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.derar.libya.happyplaces.R
import com.derar.libya.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.derar.libya.happyplaces.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}