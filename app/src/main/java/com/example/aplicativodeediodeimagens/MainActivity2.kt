package com.example.aplicativodeediodeimagens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativodeediodeimagens.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}