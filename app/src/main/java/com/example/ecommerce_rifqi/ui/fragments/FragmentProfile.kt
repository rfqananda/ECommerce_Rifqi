package com.example.ecommerce_rifqi.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecommerce_rifqi.R
import com.example.ecommerce_rifqi.adapter.CustomSpinnerAdapter
import com.example.ecommerce_rifqi.databinding.FragmentProfileBinding
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.ui.*
import com.example.ecommerce_rifqi.ui.view.ChangePhotoViewModel
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
import java.util.*
import kotlin.properties.Delegates

@Suppress("DEPRECATION")
class FragmentProfile : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var sharedPref: PreferencesHelper

    private lateinit var viewModel: ChangePhotoViewModel

    private lateinit var loading: LoadingDialog

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var isBackCamera by Delegates.notNull<Boolean>()

    private var isUserAction = false

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    var languages = arrayOf(" ", "EN", "ID")
    private var images = intArrayOf(R.drawable.bg_card4, R.drawable.ic_eng, R.drawable.ic_id)

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        sharedPref = PreferencesHelper(requireContext())

        loading = LoadingDialog(requireActivity())

        binding.btnLogout.setOnClickListener {
            sharedPref.clear()
            startActivity(Intent(requireContext(), LoginActivity::class.java))

            //On Click Logout
            firebaseAnalytics = Firebase.analytics
            val selectItem = Bundle()
            selectItem.putString("screen_name", "Profile")
            selectItem.putString("item_name", "Logout")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, selectItem)
            activity?.finish()
        }

        binding.apply {

            var currentPos = sharedPref.getString(Constant.PREF_CURRENT_POSITION)

            spinner.setOnTouchListener{ v, event ->
                isUserAction = true
                false
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (isUserAction) {
                        when (position) {
                            2 -> {
                                setLocate("in")
                                sharedPref.put(Constant.PREF_CURRENT_POSITION, position.toString())
                                sharedPref.put(Constant.PREF_LANG, "in")
                                requireActivity().recreate()
                            }
                            1 -> {
                                setLocate("en")
                                sharedPref.put(Constant.PREF_CURRENT_POSITION, position.toString())
                                sharedPref.put(Constant.PREF_LANG, "en")
                                requireActivity().recreate()
                            }
                            else -> requireActivity().recreate()
                        }

                        //On Change Language
                        firebaseAnalytics = Firebase.analytics
                        val selectItem = Bundle()
                        selectItem.putString("screen_name", "Profile")
                        selectItem.putString("item_name", "Change Language")
                        selectItem.putString("language", sharedPref.getString(Constant.PREF_LANG))
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, selectItem)
                    } else {
                        isUserAction = false
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            val customSpinnerAdapter = CustomSpinnerAdapter(requireActivity(), images, languages)
            spinner.adapter = customSpinnerAdapter

            if (currentPos != null) {
                spinner.setSelection(currentPos.toInt())
            } else spinner.setSelection(0)


            val name = sharedPref.getString(Constant.PREF_NAME)
            val emailUser = sharedPref.getString(Constant.PREF_EMAIL)

            tvName.text = name
            tvEmail.text = emailUser

            btnChangePassword.setOnClickListener {
                //On Click Change Password
                firebaseAnalytics = Firebase.analytics
                val selectItem = Bundle()
                selectItem.putString("screen_name", "Profile")
                selectItem.putString("item_name", "Change Password")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, selectItem)

                findNavController().navigate(R.id.action_fragmentProfile_to_fragmentChangePassword)
            }

            btnChangePp.setOnClickListener {
                showSimpleDialog()
                //Firebase OnClick Camera Icon
                val btn_camera = Bundle()
                btn_camera.putString("screen_name", "Profile")
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
        onload.putString("screen_name", "Profile")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
    }

    private fun whenGetPhoto() {
        if (getFile != null) {
            loading.startLoading()
            val file = getFile as File

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image",
                file.name,
                requestImageFile
            )
            val id = sharedPref.getString(Constant.PREF_ID)

            val userID = id!!.toRequestBody("text/plain".toMediaType())

            Log.d("User ID", userID.toString())


            changeImage(userID, imageMultipart)
        } else showMessage("LAlala")
    }

    override fun onStart() {
        super.onStart()

        val imageUser = sharedPref.getString(Constant.PREF_PATH)

        Glide.with(requireContext())
            .load(imageUser)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(CircleCrop())
            .into(binding.ivPhotoUser)
    }


    private fun setLocate(Lang: String) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireActivity().resources.updateConfiguration(
            config,
            requireActivity().resources.displayMetrics
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun changeImage(id: RequestBody, image: MultipartBody.Part) {
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(requireContext())
        )[ChangePhotoViewModel::class.java]

        viewModel.setChangeImage(id, image)
        viewModel.changeImageSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response.success.message)
                loading.isDismiss()
                if (response.success.status == 200) {
                    kotlin.run {
                        sharedPref.changeImage(response.success.path)
                    }
                }
            }
        }
        viewModel.toast.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { response ->
                showMessage(response)
                loading.isDismiss()
            }
        }


    }

    private fun showSimpleDialog() {
        val getPhoto = arrayOf("Camera", "Galeri")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Image")
            .setItems(getPhoto) { dialog, which ->
                val selectedOption = getPhoto[which]
                when (which) {
                    0 -> startCameraX()
                    1 -> startGallery()
                }

                //On Change Image
                firebaseAnalytics = Firebase.analytics
                val selectItem = Bundle()
                selectItem.putString("screen_name", "Profile")
                selectItem.putString("image", selectedOption)
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, selectItem)
            }
            .show()
    }

    private fun startCameraX() {
        val intent = Intent(requireActivity(), CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RegisterActivity.CAMERA_X_RESULT) {

            val myFile = it.data?.getSerializableExtra("picture") as File
            isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            Glide.with(requireContext())
                .load(result)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CircleCrop())
                .into(binding.ivPhotoUser)

            getFile = reduceFileImage(myFile, isBackCamera)

            whenGetPhoto()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = reduceFileImage(myFile, null)

            Glide.with(requireContext())
                .load(myFile)
                .transition(DrawableTransitionOptions.withCrossFade())
                .transform(CircleCrop())
                .into(binding.ivPhotoUser)

            whenGetPhoto()
        }
    }

    private fun reduceFileImage(file: File, isBackCamera:Boolean?): File {
        var bitmap = BitmapFactory.decodeFile(file.path)
        if (isBackCamera == true){
            bitmap = rotateBitmap(bitmap, isBackCamera)
        }
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


}