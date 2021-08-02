package com.derar.libya.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.derar.libya.happyplaces.database.DatabaseHandler
import com.derar.libya.happyplaces.databinding.ActivityMainBinding
import com.derar.libya.happyplaces.models.HappyPlaceModel
import com.happyplaces.adapters.HappyPlacesAdapter

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
        getHappyPlacesFormLocalDB()
    }

    private fun setupHappyPlacesRecyclerView(
        happyPlaceList: ArrayList<HappyPlaceModel>
    ){
        binding.rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        binding.rvHappyPlacesList.setHasFixedSize(true)
        val happyPlacesAdapter = HappyPlacesAdapter(this,happyPlaceList)
        binding.rvHappyPlacesList.adapter=happyPlacesAdapter
    }
    private fun getHappyPlacesFormLocalDB(){
        val db=DatabaseHandler(this)
        val getHappyPlacesList:ArrayList<HappyPlaceModel> = ArrayList(db.getAllHappyPlaces())

        if(!getHappyPlacesList.isNullOrEmpty()){
            binding.rvHappyPlacesList.visibility= View.VISIBLE
            binding.tvNoRecordsAvailable.visibility=View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        }else{
            binding.rvHappyPlacesList.visibility= View.GONE
            binding.tvNoRecordsAvailable.visibility=View.VISIBLE
        }

    }


    override fun onResume(){
        getHappyPlacesFormLocalDB()
        super.onResume()
    }

}