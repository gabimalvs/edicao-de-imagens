package com.example.aplicativodeediodeimagens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.aplicativodeediodeimagens.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar o botão LOAD para abrir o PhotoPicker
        binding.buttonLoad.setOnClickListener {
            openPhotoPicker()
        }

        // Configurar os botões inferiores para iniciar a FeatureActivity
        binding.buttonCrop.setOnClickListener { startFeatureActivity("CROP") }
        binding.buttonLight.setOnClickListener { startFeatureActivity("LIGHT") }
        binding.buttonColors.setOnClickListener { startFeatureActivity("COLOR") }
        binding.buttonFilters.setOnClickListener { startFeatureActivity("FILTERS") }
    }

    private fun openPhotoPicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.imageView2.setImageURI(uri) // Exibir a imagem selecionada no ImageView
            }
        }
    }

    private fun startFeatureActivity(buttonName: String) {
        val intent = Intent(this, FeatureActivity::class.java)
        intent.putExtra("BUTTON_NAME", buttonName)
        startActivity(intent)
    }
}