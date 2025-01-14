package com.example.aplicativodeediodeimagens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicativodeediodeimagens.databinding.ActivityFeatureBinding

class FeatureActivity : ComponentActivity() {
    private lateinit var binding: ActivityFeatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obter o nome do botão passado da MainActivity
        val buttonName = intent.getStringExtra("BUTTON_NAME")
        binding.textFeature.text = buttonName // Exibir o nome do botão no TextView
    }
}