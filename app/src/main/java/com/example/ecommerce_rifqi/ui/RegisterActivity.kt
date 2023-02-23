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
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.ui.view.RegisterViewModel
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.example.ecommerce_rifqi.utils.rotateBitmap
import com.example.ecommerce_rifqi.utils.uriToFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
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

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var userChoice = ""

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
            //Firebase OnClick Button Login
            val btn_login = Bundle()
            btn_login.putString("screen_name", "Sign Up")
            btn_login.putString("button_name", "Login")
            firebaseAnalytics.logEvent(Constant.button_click, btn_login)
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

                if (getFile != null) {
                    val file = getFile as File

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



                    if (
                        etName.text?.isNotEmpty() == true &&
                        etEmail.text?.isNotEmpty() == true &&
                        etPass.text?.isNotEmpty() == true &&
                        etPhone.text?.isNotEmpty() == true &&
                        genderBody.toString().isNotEmpty()
                    ) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                            inputEmail.error = "The email you are using is not correct!"
                            loading.isDismiss()
                        } else{
                            register(
                                name,
                                email,
                                pass,
                                phone,
                                genderBody,
                                imageMultipart
                            )
                        }

                    } else {
                        loading.isDismiss()
                        showMessage(resources.getString(R.string.txt_register))
                    }
                } else {
                    loading.isDismiss()
                    val insertImage = resources.getString(R.string.txt_insert_image)
                    showMessage(insertImage)
                }

                //Firebase OnClick Button SignUp
                val genderIdentify = if (gender > 0){
                    "Perempuan"
                } else "Laki-laki"

                val btn_signup = Bundle()
                btn_signup.putString("screen_name", "Sign Up")
                btn_signup.putString("image", userChoice)
                btn_signup.putString("email", etEmail.text.toString())
                btn_signup.putString("name", etName.text.toString())
                btn_signup.putString("phone", etPhone.text.toString())
                btn_signup.putString("gender", genderIdentify)
                btn_signup.putString("button_name", "Sign Up")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, btn_signup)
            }
            btnUploadPp.setOnClickListener {
                showSimpleDialog()
                //Firebase OnClick Camera Icon
                val btn_camera = Bundle()
                btn_camera.putString("screen_name", "Sign Up")
                btn_camera.putString("button_name", "Icon Photo")
                firebaseAnalytics.logEvent(Constant.button_click, btn_camera)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Firebase Onload
        firebaseAnalytics = Firebase.analytics
        val onload = Bundle()
        onload.putString("screen_name", "Sign Up")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
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

            getFile = reduceFileImage(myFile, isBackCamera)
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

    private fun showSimpleDialog() {
        val getPhoto = arrayOf("Camera", "Galeri")

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Image")
            .setItems(getPhoto) { dialog, which ->
                val selectedOption = getPhoto[which]
                when (which) {
                    0 -> startCameraX()
                    1 -> startGallery()
                }

                userChoice = selectedOption
                //Firebase On Change Image
                val camera = Bundle()
                camera.putString("screen_name", "Sign Up")
                camera.putString("image", selectedOption)
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, camera)
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

    private fun reduceFileImage(file: File, isBackCamera: Boolean): File {
        var bitmap = BitmapFactory.decodeFile(file.path)
        bitmap = rotateBitmap(bitmap, isBackCamera)
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

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(applicationContext)
        )[RegisterViewModel::class.java]

        viewModel.setRegister(name, email, password, phone, gender, image)

        viewModel.toast.observe(this) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
                loading.isDismiss()
            }
        }
        viewModel.registerSuccess.observe(this) {
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