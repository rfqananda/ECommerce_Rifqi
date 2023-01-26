package com.example.ecommerce_rifqi.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.databinding.ActivityRegisterBinding
import com.example.ecommerce_rifqi.ui.view.RegisterViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.example.ecommerce_rifqi.utils.rotateBitmap
import com.example.ecommerce_rifqi.utils.uriToFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.properties.Delegates

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var loading: LoadingDialog

    private var isBackCamera by Delegates.notNull<Boolean>()

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showMessage("Tidak mendapatkan permission.")
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        loading = LoadingDialog(this)

        binding.btnLogin.setOnClickListener {
            moveActivity(this)
        }

        binding.apply {

            etEmail.doOnTextChanged { text, start, before, count ->
                if (Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                    inputEmail.error = null
                    inputEmail.isErrorEnabled = false
                } else inputEmail.error = "The email you are using is not correct!"
            }

            etPassConfirm.doOnTextChanged { text, start, before, count ->
                if (etPassConfirm.text.toString() != etPass.text.toString()) {
                    inputPassConfirm.error = "Passwords not match!"

                } else {
                    inputPassConfirm.error = null
                    inputPassConfirm.isErrorEnabled = false
                }
            }


            var gender: Int = 0

            rbMale.setOnClickListener {
                gender = 0
                rbMale.isChecked = true
            }

            rbFemale.setOnClickListener {
                gender = 1
                rbFemale.isChecked = true
            }


            btnSignup.setOnClickListener {
                loading.startLoading()

                if (getFile != null){

                    //coba



                    val file = reduceFileImage(getFile as File)

                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "image",
                        file.name,
                        requestImageFile
                    )

                    val name = etName.text.toString().toRequestBody("text/plain".toMediaType())
                    val email = etEmail.text.toString().toRequestBody("text/plain".toMediaType())
                    val pass = etPass.text.toString().toRequestBody("text/plain".toMediaType())
                    val phone = etPhone.text.toString().toRequestBody("text/plain".toMediaType())
                    val genderBody = gender.toString().toRequestBody("text/plain".toMediaType())

                    register(
                        name,
                        email,
                        pass,
                        phone,
                        genderBody,
                        imageMultipart
                    )


                } else {
                    loading.isDismiss()
                    val insertImage = resources.getString(R.string.txt_insert_image)
                    showMessage(insertImage)
                }
            }
            btnUploadPp.setOnClickListener {
                showSimpleDialog()
            }
        }
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {

            val myFile = it.data?.getSerializableExtra("picture") as File
            isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            binding.ivPhoto.setImageBitmap(result)

            getFile = reduceFileImage(myFile)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@RegisterActivity)
            getFile = myFile
            binding.ivPhoto.setImageURI(selectedImg)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun dialog(message: String) {
        loading.isDismiss()
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Message")
            setMessage(message)
            setPositiveButton("Ok") { dialogInterface, i ->
                dialogInterface.dismiss()
                moveActivity(this@RegisterActivity)
            }
        }.show()
    }

    private fun moveActivity(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSimpleDialog(){
        val getPhoto = arrayOf("Camera", "Galeri")

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Image")
            .setItems(getPhoto) {dialog, which ->
                when(which){
                    0 -> startCameraX()
                    1 -> startGallery()
                }
            }.show()
    }



    private fun startCameraX() {
        val intent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun reduceFileImage(file: File): File {
        var bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun register(
        name: RequestBody,
        email: RequestBody,
        password: RequestBody,
        phone: RequestBody,
        gender: RequestBody,
        image: MultipartBody.Part
    ) {

        viewModel = ViewModelProvider(this, ViewModelFactory(applicationContext))[RegisterViewModel::class.java]

        viewModel.setRegister(name, email, password, phone, gender, image)

        viewModel.toast.observe(this){
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
                loading.isDismiss()
            }
        }
        viewModel.registerSuccess.observe(this){
            it.getContentIfNotHandled()?.let { response ->
                dialog(response.success.message)
            }
        }
//
//        viewModel.registerError.observe(this){
//            it.getContentIfNotHandled()?.let { response ->
//                loading.isDismiss()
//                showMessage(response.error.message)
//            }
//        }

    }


}