package com.dicoding.handspeak.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.handspeak.R
import com.dicoding.handspeak.databinding.ActivitySettingBinding
import com.dicoding.handspeak.ui.profile.ProfileFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var imageUri: Uri
    private lateinit var firebaseAuth: FirebaseAuth
    private var profileUpdateListener: ProfileUpdateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)

        val user = firebaseAuth.currentUser

        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.imagePhoto)
            }
        }

        binding.etName.setText(user?.displayName)

        binding.btnProfile.setOnClickListener {
            showImagePickerDialog()
        }

        profileUpdateListener = object : ProfileUpdateListener {
            override fun onProfileUpdated(name: String?, photoUrl: Uri?) {
                // Implement your logic here when profile is updated
            }
        }

        binding.btnUpdate.setOnClickListener {
            val image = when {
                ::imageUri.isInitialized -> imageUri
                else -> user?.photoUrl
            }

            val name = binding.etName.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = "Name is required"
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                            profileUpdateListener?.onProfileUpdated(name, image)
                            updateUsernameInProfile(name) // Tambahkan ini
                        } else {
                            Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            packageManager?.let {
                intent.resolveActivity(it)?.also {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            selectedImage?.let {
                val imgBitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                uploadImage(imgBitmap)
            }
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref =
            FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { url ->
                        url.result?.let { photoUrl ->
                            Picasso.get().load(photoUrl).into(binding.imagePhoto)
                            imageUri = photoUrl
                            // Mengirim URL foto profil ke ProfileFragment
                            val profileFragment =
                                supportFragmentManager.findFragmentById(R.id.photo_profile) as? ProfileFragment
                            profileFragment?.updateProfilePhoto(photoUrl.toString())
                        }
                    }
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(this)
            .setTitle("Choose image from")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        intentCamera()
                    }
                    1 -> {
                        Intent(Intent.ACTION_PICK).also { intent ->
                            intent.type = "image/*"
                            startActivityForResult(intent, REQUEST_GALLERY)
                        }
                    }
                }
            }
            .show()
    }

    private fun updateUsernameInProfile(username: String) {
        val intent = Intent()
        intent.putExtra("username", username)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_GALLERY = 200
    }
}


