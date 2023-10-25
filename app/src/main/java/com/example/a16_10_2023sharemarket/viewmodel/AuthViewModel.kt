package com.example.a16_10_2023sharemarket.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.a16_10_2023sharemarket.repository.AuthenticationRepository
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthenticationRepository(application)

    private var isOtpSent = false
    private var isLoggedIn = false

    private var googleSignInClient: GoogleSignInClient? = repository.getGoogleSignInClient()
    val errorMessageLiveData: MutableLiveData<String> = repository.errorMessageLiveData
    val loginResultLiveData: MutableLiveData<Boolean> = repository.loginResultLiveData
    private val sharedPreferences = application.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
    private val PREF_USER_AUTHENTICATED = "user_authenticated"

    fun setUserAuthenticated(authenticated: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_USER_AUTHENTICATED, authenticated).apply()
    }

    fun isUserAuthenticated(): Boolean {
        return sharedPreferences.getBoolean(PREF_USER_AUTHENTICATED, false)
    }




    fun signInWithGoogle() {
        if (googleSignInClient != null) {
            val signInIntent = googleSignInClient!!.signInIntent
            // For example:
            // activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        } else {
            errorMessageLiveData.postValue("Google Sign-In is not properly configured.")
        }
    }



    fun setOtpSentFlag(isOtpSent: Boolean) {
        this.isOtpSent = isOtpSent
    }

    fun sendOtp(phoneNumber: String, activity: Activity) {
        if (!isOtpSent && !isLoggedIn && isValidPhoneNumber(phoneNumber)) {
            repository.sendOtp(phoneNumber, activity)
        } else if (isLoggedIn) {
            errorMessageLiveData.postValue("User is already logged in.")
        } else {
            errorMessageLiveData.postValue("Invalid phone number or OTP already sent.")
        }
    }

    fun verifyOtp(otp: String) {
        if (!isLoggedIn) {
            repository.verifyOtp(otp)
        }
    }


    fun setLoggedIn(isLogged: Boolean) {
        isLoggedIn = isLogged
    }


    fun signInWithGoogle(account: GoogleSignInAccount) {
        repository.signInWithGoogle(account)
    }
    fun signInWithFacebook(token: AccessToken, name: String) {
        repository.signInWithFacebook(token)
    }
}
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        try {
            val parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, Locale.getDefault().country)
            return phoneNumberUtil.isValidNumber(parsedPhoneNumber)
        } catch (e: Exception) {
            e.printStackTrace()
        }
            return false
        }


