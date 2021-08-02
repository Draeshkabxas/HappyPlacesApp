package com.derar.libya.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.derar.libya.happyplaces.database.DatabaseHandler
import com.derar.libya.happyplaces.databinding.ActivityMainBinding
import com.derar.libya.happyplaces.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.fabAddHappyPlace.setOnClickListener{
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }

        val testDb=DatabaseHandler(this)
        val id =testDb.getAllHappyPlaces()[0].id

        Log.i("testDB","${testDb.getHappyPlace(id)}")
    }
}