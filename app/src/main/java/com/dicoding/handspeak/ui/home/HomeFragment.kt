package com.dicoding.handspeak.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dicoding.handspeak.databinding.FragmentHomeBinding
import com.dicoding.handspeak.ui.education.AlphabetActivity
import com.dicoding.handspeak.ui.education.DictionaryActivity
import com.dicoding.handspeak.ui.education.KatasifatActivity
import com.dicoding.handspeak.ui.education.NumerikActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.alphabet.setOnClickListener {
            val intent = Intent(activity, AlphabetActivity::class.java)
            startActivity(intent)
        }

        binding.katasifat.setOnClickListener {
            val intent = Intent(activity, KatasifatActivity::class.java)
            startActivity(intent)
        }

        binding.numerik.setOnClickListener {
            val intent = Intent(activity, NumerikActivity::class.java)
            startActivity(intent)
        }

        binding.dictionary.setOnClickListener {
            val intent = Intent(activity, DictionaryActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
