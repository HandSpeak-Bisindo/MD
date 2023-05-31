package com.dicoding.handspeak

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.dicoding.handspeak.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.LoginBtn.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.editPasswordlogin.text.toString()

            //Validasi Email
            if (email.isEmpty()) {
                binding.edtEmailLogin.error = "Isi Email Terlebih Dahulu"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailLogin.error = "Email Tidak Valid"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }

            //Validasi Password
            if (password.isEmpty()) {
                binding.editPasswordlogin.error= "Isi Password Terlebih Dahulu"
                binding.editPasswordlogin.requestFocus()
                return@setOnClickListener
            }

            LoginFirebase(email, password)

        }
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeFragment::class.java)
                    startActivity(intent)
                } else{
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}