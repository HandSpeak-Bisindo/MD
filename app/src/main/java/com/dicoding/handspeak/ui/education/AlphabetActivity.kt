package com.dicoding.handspeak.ui.education

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.handspeak.R
import com.dicoding.handspeak.databinding.ActivityAlphabetBinding

class AlphabetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlphabetBinding

    private lateinit var rvAlphabet: RecyclerView
    private val list = ArrayList<Alphabet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlphabetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvAlphabet = findViewById(R.id.rv_alphabet)
        rvAlphabet.setHasFixedSize(true)
        list.addAll(getListAlphabet())
        showRecyclerList()
    }

    private fun getListAlphabet(): ArrayList<Alphabet> {
        val dataName = resources.getStringArray(R.array.data_name)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        val listAlphabet = ArrayList<Alphabet>()
        for (i in dataName.indices) {
            val alphabet = Alphabet(dataName[i], dataPhoto.getResourceId(i, 0))
            listAlphabet.add(alphabet)
        }
        dataPhoto.recycle() // Menghapus sumber daya yang tidak lagi diperlukan
        return listAlphabet
    }

    private fun showRecyclerList() {
        rvAlphabet.layoutManager = LinearLayoutManager(this)
        val listAlphabetAdapter = ListAlphabetAdapter(list)
        rvAlphabet.adapter = listAlphabetAdapter
    }
}
