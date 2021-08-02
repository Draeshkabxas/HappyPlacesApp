package com.derar.libya.happyplaces.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.derar.libya.happyplaces.R
import com.derar.libya.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var binding: ActivityAddHappyPlaceBinding
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var getImageFromGallery:ActivityResultLauncher<String?>
    private lateinit var getPhotoFromCamera:ActivityResultLauncher<Void?>
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initializeGetImageFromGallery()
        initializeGetPhotoFromCamera()
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
        dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }


        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
    }

    private fun initializeGetPhotoFromCamera() {
        getPhotoFromCamera=registerForActivityResult(
            ActivityResultContracts.TakePicturePreview(),
            ActivityResultCallback {
                val imageUri=saveImageToInternalStorage(it)
                binding.ivPlaceImage.setImageURI(imageUri)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeGetImageFromGallery() {
         getImageFromGallery=registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                val imageUri=saveImageToInternalStorage(getCapturedImage(it))
                binding.ivPlaceImage.setImageURI(imageUri)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                contentResolver,
                selectedPhotoUri
            )
            else -> {
                val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf(
                    "Select photo from Gallery", "Capture photo from camera"
                )
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> {
                            choosePhotoFromGallery()
                        }
                        1 -> {
                            takePhotoFromCamera()
                        }
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if(report.areAllPermissionsGranted()){
                    lunchGetPhotoFromCamera()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermission()
            }

        }).onSameThread()
            .check()

    }

    private fun lunchGetPhotoFromCamera() {
        try {
            getPhotoFromCamera.launch(null)
        }catch (e:Exception){
            Toast.makeText(this,
                "Failed to take the image from camera.",
                Toast.LENGTH_SHORT).show()
        }

    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if(report.areAllPermissionsGranted()){
                    lunchGetImageFromGallery()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermission()
            }

        }).onSameThread()
            .check()

    }

    private fun lunchGetImageFromGallery() {
        try {
            getImageFromGallery.launch("image/*")
        }catch (e:Exception){
            Toast.makeText(this,
                "Failed to load the image from gallery.",
            Toast.LENGTH_SHORT).show()
        }
        }

    private fun showRationalDialogForPermission() {
       AlertDialog.Builder(this)
           .setMessage("It looks like you have turned off permission required for this feature." +
                   "\n It can be enabled under the Application  settings")
           .setPositiveButton("GO TO SETTINGS"){
               _,_->
              goToSettings()
           }
           .setNegativeButton("Cancel"){dialog,_->
               dialog.dismiss()
           }
           .show()

    }

    private fun goToSettings() {
        try{
            val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri= Uri.fromParts("package",packageName,null)
            intent.data=uri
            startActivity(intent)
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper=ContextWrapper(applicationContext)
        var file=wrapper.getDir(IMAGE_DIRECTORY, MODE_PRIVATE)
        file= File(file,"${UUID.randomUUID()}.jpg")
        try {
           val stream=FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        } catch (e: Exception) {
        }
        return Uri.parse(file.absolutePath)
    }

    companion object{
        private const val IMAGE_DIRECTORY="HappyPlacesImages"
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())
    }
}