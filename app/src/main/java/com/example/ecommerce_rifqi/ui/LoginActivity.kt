package com.example.ecommerce_rifqi.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce_rifqi.databinding.ActivityLoginBinding
import com.example.ecommerce_rifqi.ui.view.LoginViewModel
import com.example.ecommerce_rifqi.helper.Constant
import com.example.ecommerce_rifqi.helper.PreferencesHelper
import com.example.ecommerce_rifqi.utils.ViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var sharedPref: PreferencesHelper

    private lateinit var viewModel: LoginViewModel

    private lateinit var loading: LoadingDialog

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var email: String = ""
    private var pass: String = ""

    override fun onStart() {
        super.onStart()
        getTokenFirebase()
        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loading = LoadingDialog(this)
        sharedPref = PreferencesHelper(this)
        binding.apply {
            etEmail.doOnTextChanged { text, start, before, count ->
                if (Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                    inputEmail.error = null
                    inputEmail.isErrorEnabled = false
                } else inputEmail.error = "The email you are using is not correct!"
            }

            btnLogin.setOnClickListener {
                val token_fcm = sharedPref.getString(Constant.PREF_FB)

                if (etEmail.text!!.isEmpty()){
                    inputEmail.error = "Email cannot be empty!"
                } else if(etPass.text!!.isEmpty()){
                    inputPass.error = "Password cannot be empty!"
                } else {
                    loading.startLoading()
                    if (token_fcm != null) {
                        loginUser(etEmail.text.toString(), etPass.text.toString(), token_fcm)
                    } else {
                        loading.isDismiss()
                        showMessage("Token is not found")
                    }
                }

                //Firebase OnClick Button Login
                val btn_login = Bundle()
                btn_login.putString("screen_name", "Login")
                btn_login.putString("email", binding.etEmail.text.toString())
                btn_login.putString("button_name", "Login")
                firebaseAnalytics.logEvent(Constant.button_click, btn_login)
            }

            btnSignup.setOnClickListener {
                val i = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(i)
                //Firebase OnClick Button Signup
                val btn_signup = Bundle()
                btn_signup.putString("screen_name", "Login")
                btn_signup.putString("button_name", "Sign Up")
                firebaseAnalytics.logEvent(Constant.button_click, btn_signup)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Firebase Onload
        firebaseAnalytics = Firebase.analytics
        val onload = Bundle()
        onload.putString("screen_name", "Login")
        onload.putString("screen_class", this.javaClass.simpleName)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, onload)
    }
    private fun loginUser(email: String, password: String, token: String) {
        viewModel = ViewModelProvider(this@LoginActivity, ViewModelFactory(applicationContext))[LoginViewModel::class.java]
        viewModel.setLogin(email, password, token)

        viewModel.loginSuccess.observe(this){
            it.getContentIfNotHandled()?.let { response ->
                binding.inputPass.error = null
                binding.inputPass.isErrorEnabled = false
                loading.isDismiss()

                val access = response.success.access_token
                val refresh = response.success.refresh_token
                val name = response.success.data_user.name
                val emailUser = response.success.data_user.email
                val id = response.success.data_user.id
                val path = response.success.data_user.path

                Log.d("Access Token", access)

                saveSession(access, refresh, name, emailUser, id, path)
                val i = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(i)
                finish()
                showMessage(response.success.message)

            }
        }

        viewModel.loginError.observe(this){
            it.getContentIfNotHandled()?.let { response ->
                loading.isDismiss()
                showMessage(response.error.message)
            }
        }


    }

    private fun saveSession(
        access: String,
        refresh: String,
        name: String,
        email: String,
        id: Int,
        path: String) {
        sharedPref.put(Constant.PREF_IS_LOGIN, true)
        sharedPref.put(Constant.PREF_ACCESS, access)
        sharedPref.put(Constant.PREF_REFRESH, refresh)
        sharedPref.put(Constant.PREF_NAME, name)
        sharedPref.put(Constant.PREF_EMAIL, email)
        sharedPref.put(Constant.PREF_ID, id.toString())
        sharedPref.put(Constant.PREF_PATH, path)

    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getTokenFirebase(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->

            if (isNetworkConnected(this)) {
                val token = task.result
                sharedPref.put(Constant.PREF_FB, token)
                Log.d("tokenfirebase", token)
            } else {
                showMessage("No internet connection!")
            }

        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

}