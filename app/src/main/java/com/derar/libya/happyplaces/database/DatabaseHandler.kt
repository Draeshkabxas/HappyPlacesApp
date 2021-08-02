package com.derar.libya.happyplaces.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.derar.libya.happyplaces.models.HappyPlaceModel
import java.lang.String

class DatabaseHandler : SQLiteOpenHelper {

    companion object {
        private const val DATABASE_NAME = "happyPlaces"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "happy_places"

        //All the columns names
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION ="description"
        private const val KEY_DATE = "date"
        private const val KEY_IMAGE= "image"
        private const val KEY_LOCATION = "location"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_LATITUDE = "latitude"
    }

    constructor(
        context: Context?,
    ) : super(context, DATABASE_NAME, null, DATABASE_VERSION)


    override fun onCreate(db: SQLiteDatabase?) {
      //Create table with fields
        val CREATE_HAPPY_PLACE_TABLE = "CREATE TABLE $TABLE_NAME ( " +
              "$KEY_ID INTEGER PRIMARY KEY," +
              "$KEY_TITLE  TEXT,"+
              "$KEY_DESCRIPTION TEXT,"+
              "$KEY_IMAGE TEXT,"+
              "$KEY_DATE TEXT,"+
              "$KEY_LOCATION TEXT,"+
              "$KEY_LONGITUDE TEXT,"+
              "$KEY_LATITUDE TEXT )"
        db!!.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        // Drop older table if existed
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME");
        // Create tables again
        onCreate(db);
    }
    /**
     * This function is for insert a passed happy place to
     * local database.
     * @param happyPlace the happy place that will inserting in the database
     * @return Long variable if it's above 0 then inserting work successfully
     * otherwise some problem happened when inserting
     */
    fun addHappyPlace(happyPlace:HappyPlaceModel): Long {
       val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_TITLE, happyPlace.title)
        values.put(KEY_DESCRIPTION, happyPlace.description )
        values.put(KEY_IMAGE, happyPlace.image)
        values.put(KEY_DATE, happyPlace.date)
        values.put(KEY_LOCATION, happyPlace.location)
        values.put(KEY_LONGITUDE, happyPlace.longitude.toString())
        values.put(KEY_LATITUDE, happyPlace.latitude.toString())

        // Inserting Row
        val result =db.insert(TABLE_NAME, null, values)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return result
    }


    /**
     * This function is for getting all happy places form
     * local database.
     * @return MutableList of HappyPlaceModel.kt objects
     */
    @SuppressLint("Recycle")
    fun getAllHappyPlaces():MutableList<HappyPlaceModel> {
       val happyPlacesList = ArrayList<HappyPlaceModel>()
        // Select All Query
        val selectQuery = "SELECT  * FROM $TABLE_NAME"

        val db = this.writableDatabase
        try {
            val cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val id=Integer.parseInt(cursor.getString(0))
                    val title=cursor.getString(1)
                    val description=cursor.getString(2)
                    val image=cursor.getString(3)
                    val date=cursor.getString(4)
                    val location=cursor.getString(5)
                    val longitude=cursor.getString(6)
                    val latitude =cursor.getString(7)
                    val happyPlace= HappyPlaceModel(
                        id,
                        title,
                        description,
                        image,
                        date,
                        location,
                        longitude.toDouble(),
                        latitude.toDouble()
                    )
                    // Adding contact to list
                    happyPlacesList.add(happyPlace);
                } while (cursor.moveToNext());
            }
        } catch (e: Exception) {
        }

        // return contact list
        return happyPlacesList;
    }

    /**
     * This function is for get happy place that have
     * the same passed id from local database.
     * @param id the happy place id that will getting from the database
     * @return HappyPlaceModel that have the same passed id
     * from local database.
     */
    fun getHappyPlace(id: Int): HappyPlaceModel? {
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_NAME, arrayOf(
                KEY_ID,
                KEY_TITLE,
                KEY_DESCRIPTION,
                KEY_IMAGE,
                KEY_DATE,
                KEY_LOCATION,
                KEY_LONGITUDE,
                KEY_LATITUDE
            ), "$KEY_ID=?", arrayOf(id.toString()), null, null, null, null
        )

        if (cursor!!.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            val title = cursor.getString(1)
            val description = cursor.getString(2)
            val image = cursor.getString(3)
            val date = cursor.getString(4)
            val location = cursor.getString(5)
            val longitude = cursor.getString(6)
            val latitude = cursor.getString(7)
            return HappyPlaceModel(
                id,
                title,
                description,
                image,
                date,
                location,
                longitude.toDouble(),
                latitude.toDouble()
            )
        }

        // return contact list
        return null
    }

    /**
     * This function is for update happy place that have
     * the same passed happyPlace id from local database.
     * @param happyPlace the happy place that will be updating from the database
     * @return Int variable if it's above 0 then updating work successfully
     * otherwise some problem happened when updating
     */
    fun updateHappyPlace(happyPlace:HappyPlaceModel):Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TITLE, happyPlace.title)
        values.put(KEY_DESCRIPTION, happyPlace.description )
        values.put(KEY_IMAGE, happyPlace.image)
        values.put(KEY_DATE, happyPlace.date)
        values.put(KEY_LOCATION, happyPlace.location)
        values.put(KEY_LONGITUDE, happyPlace.longitude.toString())
        values.put(KEY_LATITUDE, happyPlace.latitude.toString())
        // updating row
        return db.update(
            TABLE_NAME,
            values,
            "$KEY_ID = ?",
            arrayOf(String.valueOf(happyPlace.id))
        )
    }

    /**
     * This function is for delete happy place that have
     * the same passed happyPlace id from local database.
     * @param happyPlace the happy place that will deleting from the database
     */
    fun deleteHappyPlace(happyPlace:HappyPlaceModel) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$KEY_ID = ?", arrayOf(String.valueOf(happyPlace.id)))
        db.close()
    }

}