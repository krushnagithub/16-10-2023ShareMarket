package com.example.a16_10_2023sharemarket.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.a16_10_2023sharemarket.R
import com.example.a16_10_2023sharemarket.databinding.FragmentSignInBinding
import com.example.a16_10_2023sharemarket.viewmodel.AuthViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONException

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private val RC_GOOGLE_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FacebookSdk.sdkInitialize(requireContext())
        binding = FragmentSignInBinding.bind(
            inflater.inflate(
                R.layout.fragment_sign_in,
                container,
                false
            )
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        initViews()
        setupOtpFieldListeners()
        setupClickListeners()
        observeLoginResult()
        if (authViewModel.isUserAuthenticated()) {
            // The user is already authenticated, navigate to the dashboard
            findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
        }
    }




    private fun initViews() {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private fun setupOtpFieldListeners() {
        val otpDigit1 = binding.otpDigit1
        val otpDigit2 = binding.otpDigit2
        val otpDigit3 = binding.otpDigit3
        val otpDigit4 = binding.otpDigit4
        val otpDigit5 = binding.otpDigit5
        val otpDigit6 = binding.otpDigit6

        val editTexts = listOf(otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6)

        for (i in 0 until editTexts.size) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.isNotEmpty() == true) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.isEmpty() == true) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }
            })
        }
    }


    private fun setupClickListeners() {
        binding.facebookLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val accessToken = loginResult.accessToken
                    fetchFacebookUserInfo(accessToken)
                }

                override fun onCancel() {
                    // Handle canceled login
                }

                override fun onError(error: FacebookException) {
                    Log.e("Facebook Login", "Login failed: ${error.message}")
                }
            })




        binding.sendOtpButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            authViewModel.sendOtp(phoneNumber, requireActivity())
        }

        binding.verifyOtpButton.setOnClickListener {
            val otp = buildString {
                append(binding.otpDigit1.text)
                append(binding.otpDigit2.text)
                append(binding.otpDigit3.text)
                append(binding.otpDigit4.text)
                append(binding.otpDigit5.text)
                append(binding.otpDigit6.text)
            }
            authViewModel.verifyOtp(otp)
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }

    private fun fetchFacebookUserInfo(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
            if (response != null && response.error == null) {
                val name = `object`?.getString("name")
                Log.d("Facebook Login", "User's name: $name")
                if (name != null) {
                    authViewModel.signInWithFacebook(accessToken, name)
                    authViewModel.setUserAuthenticated(true)
                    findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
                }
            } else {
                Log.e("Facebook Login", "Graph API error: ${response?.error?.errorMessage}")
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "name")
        request.parameters = parameters
        request.executeAsync()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                authViewModel.signInWithGoogle(account)

                // Set the user as authenticated
                authViewModel.setUserAuthenticated(true)

                // Redirect to the dashboard upon a successful Google login
                findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
            } catch (e: ApiException) {
                Toast.makeText(
                    requireContext(),
                    "Google Sign-In failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun observeLoginResult() {
        authViewModel.loginResultLiveData.observe(viewLifecycleOwner) { loginSuccessful ->
            if (loginSuccessful) {
                authViewModel.setOtpSentFlag(true)
                authViewModel.setLoggedIn(true)

                // If the user is authenticated, navigate to the dashboard
                if (authViewModel.isUserAuthenticated()) {
                    findNavController().navigate(R.id.action_signInFragment_to_dashboardFragment)
                }
            }
        }

        authViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
