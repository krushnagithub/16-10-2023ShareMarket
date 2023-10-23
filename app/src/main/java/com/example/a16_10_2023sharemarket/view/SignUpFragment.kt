package com.example.a16_10_2023sharemarket.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.a16_10_2023sharemarket.R
import com.example.a16_10_2023sharemarket.databinding.FragmentSignUpBinding
import com.example.a16_10_2023sharemarket.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseUser

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.bind(inflater.inflate(R.layout.fragment_sign_up, null))
        return binding.root
    }
}
/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupListeners()
        observer()
    }

    private fun initView() {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
    }

    private fun setupListeners() {
        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_signinFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.editNumber.text.toString()
            val pass = binding.editPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.register(email, pass)
            }
        }
    }
    private fun observer(){
        authViewModel.userData.observe(viewLifecycleOwner, { firebaseUser: FirebaseUser? ->
            if (firebaseUser != null) {
                findNavController().navigate(R.id.action_signupFragment_to_dashboardFragment)
            }
        })
    }
}*/
