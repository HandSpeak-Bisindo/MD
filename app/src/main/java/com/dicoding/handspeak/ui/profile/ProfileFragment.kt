package com.dicoding.handspeak.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.content.Intent
import com.dicoding.handspeak.databinding.FragmentProfileBinding
import com.dicoding.handspeak.ui.LoginActivity
import com.dicoding.handspeak.ui.SettingActivity
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Menampilkan email pada TextView
        val currentUser = auth.currentUser
        binding.emailProfile.text = currentUser?.email

        binding.editProfile.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }
        binding.logout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            })
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
