package com.dicoding.handspeak.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.dicoding.handspeak.R
import com.dicoding.handspeak.ScanActivity
import com.dicoding.handspeak.databinding.ActivityResultBinding
import java.io.File


class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.retake.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
            finish()
        }

        val result = intent.getStringExtra("response")
        val imagePath = intent.getStringExtra("imagePath")

        if (result != null) {
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = result
        }

        if (imagePath != null){
            val imageResult = findViewById<ImageView>(R.id.image_result)
            loadImageFromPath(imagePath, imageResult)
        }




    }

    private fun loadImageFromPath(imagePath: String, imageResult: ImageView?) {
        val file = File(imagePath)
        val uri = FileProvider.getUriForFile(this, "com.dicoding.handspeak", file)
        imageResult?.setImageURI(uri)
    }

}
