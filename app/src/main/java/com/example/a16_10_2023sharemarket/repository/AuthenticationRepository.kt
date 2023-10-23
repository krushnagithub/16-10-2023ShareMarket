package com.example.a16_10_2023sharemarket.repository

import android.app.Application
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.example.a16_10_2023sharemarket.R
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthenticationRepository(private val application: Application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var googleSignInClient: GoogleSignInClient? = null

    val verificationIdLiveData: MutableLiveData<String> = MutableLiveData()
    val loginResultLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessageLiveData: MutableLiveData<String> = MutableLiveData()

    private var storedVerificationId: String? = null
    private var isTestingMode = false

    fun setTestingMode(testing: Boolean) {
        isTestingMode = testing
    }

    fun sendOtp(phoneNumber: String, activity: Activity) {
        if (isTestingMode) {
            storedVerificationId = "test_verification_id"
            verificationIdLiveData.postValue("test_verification_id")
        } else {
            val callbacks = getPhoneAuthCallbacks()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                activity,
                callbacks
            )
        }
    }

    fun verifyOtp(otp: String) {
        if (isTestingMode || storedVerificationId != null) {
            val verificationId = if (isTestingMode) "test_verification_id" else storedVerificationId
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            errorMessageLiveData.postValue("Verification ID is missing. Please request OTP again.")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginResultLiveData.postValue(true)
                } else {
                    val errorMessage = task.exception?.message ?: "Login failed"
                    errorMessageLiveData.postValue(errorMessage)
                }
            }
    }

    private fun getPhoneAuthCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                errorMessageLiveData.postValue("Verification failed: ${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                verificationIdLiveData.postValue(verificationId)
            }
        }
    }


    init {
        configureGoogleSignInClient()
    }

    private fun configureGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(application, gso)
    }

    fun getGoogleSignInClient(): GoogleSignInClient? {
        return googleSignInClient
    }
    fun signInWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginResultLiveData.postValue(true)
                } else {
                    val errorMessage = task.exception?.message ?: "Login failed"
                    errorMessageLiveData.postValue(errorMessage)
                }
            }
    }
        fun signInWithFacebook(token: AccessToken) {
            val credential = FacebookAuthProvider.getCredential(token.token)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginResultLiveData.postValue(true)
                    } else {
                        val errorMessage = task.exception?.message ?: "Login failed"
                        errorMessageLiveData.postValue(errorMessage)
                    }
                }
        }

    }


