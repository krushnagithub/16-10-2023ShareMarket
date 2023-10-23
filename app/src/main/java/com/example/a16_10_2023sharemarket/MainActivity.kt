package com.example.a16_10_2023sharemarket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.a16_10_2023sharemarket.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseApp.getApps(this).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("863937518910")
                .setApiKey("AIzaSyAw6ig8C4IszbFOwwRu3kBqDF3uQ_9bgUI")
                .setProjectId("sharemarket-cf13b")
                .build()
            FirebaseApp.initializeApp(this, options)
        }


    }
}