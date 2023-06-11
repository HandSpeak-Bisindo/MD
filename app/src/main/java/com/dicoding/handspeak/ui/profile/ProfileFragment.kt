package com.dicoding.handspeak.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.dicoding.handspeak.databinding.FragmentProfileBinding
import com.dicoding.handspeak.ui.LoginActivity
import com.dicoding.handspeak.ui.SettingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var updateUsernameLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentUser: FirebaseUser

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Mendapatkan instance pengguna saat ini
        currentUser = auth.currentUser!!

        // Menampilkan email pada TextView
        binding.emailProfile.text = currentUser.email
        binding.nameProfile.text = currentUser.displayName

        // Menampilkan foto profil
        currentUser.photoUrl?.let { photoUrl ->
            Picasso.get().load(photoUrl).into(binding.photoProfile)
        }

        updateUsernameLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val username = data?.getStringExtra("username")
                binding.nameProfile.text = username
            }
        }

        binding.editProfile.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            updateUsernameLauncher.launch(intent)
        }

        binding.logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateProfilePhoto(photoUrl: String) {
        // Gunakan library Picasso untuk memuat foto profil dari URL
        Picasso.get().load(photoUrl).into(binding.photoProfile)
    }
}
