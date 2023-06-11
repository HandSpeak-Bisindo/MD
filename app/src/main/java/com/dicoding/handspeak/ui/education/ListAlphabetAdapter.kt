package com.dicoding.handspeak.ui.education

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.handspeak.R

class ListAlphabetAdapter(private val listAlphabet: ArrayList<Alphabet>) : RecyclerView.Adapter<ListAlphabetAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_alphabet, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val alphabet = listAlphabet[position]
        holder.bind(alphabet)
    }

    override fun getItemCount(): Int = listAlphabet.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        private val tvName: TextView = itemView.findViewById(R.id.tv_item_name)

        fun bind(alphabet: Alphabet) {
            imgPhoto.setImageResource(alphabet.photo)
            tvName.text = alphabet.name
        }
    }
}
