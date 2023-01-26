package com.example.ecommerce_rifqi.helper

import android.content.Context
import android.content.SharedPreferences


class PreferencesHelper (context: Context){

    private val PREFS_NAME = "sharedpreprefkotlin123"
    private var sharedpref: SharedPreferences
    val editor: SharedPreferences.Editor

    init {
        sharedpref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedpref.edit()
    }

    fun put(key: String, value: String){
        editor.putString(key, value)
            .apply()
    }

    fun getString(key: String) : String? {
        return sharedpref.getString(key, null)
    }

    fun put(key: String, value: Boolean){
        editor.putBoolean(key, value)
            .apply()
    }

    fun getBoolean(key: String) : Boolean{
        return sharedpref.getBoolean(key, false)
    }

    fun clear(){
        editor.clear()
            .apply()
    }

    fun changeImage(value: String){
        editor.putString(Constant.PREF_PATH, value)
        editor.apply()
    }

    fun putNewToken(access_token : String, refresh_token : String ){
        editor.apply{
            putString(Constant.PREF_ACCESS, access_token)
            putString(Constant.PREF_REFRESH, refresh_token)
        }
    }


    fun changeLanguage(position: Int){
        editor.putString(Constant.PREF_CURRENT_POSITION, position.toString())
        editor.apply()
    }

}