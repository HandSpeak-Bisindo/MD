package com.dicoding.handspeak.ui.education

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alphabet(
    val name: String,
    val photo: Int
) : Parcelable