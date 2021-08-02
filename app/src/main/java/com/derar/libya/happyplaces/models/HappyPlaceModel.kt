package com.derar.libya.happyplaces.models

data class HappyPlaceModel(
    val id:Int,
    val title:String,
    val description:String,
    val image:String,
    val date:String,
    val location:String,
    val longitude:Double,
    val latitude:Double
)