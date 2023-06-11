package com.dicoding.handspeak.ui

import android.net.Uri

interface ProfileUpdateListener {
    fun onProfileUpdated(name: String?, photoUrl: Uri?)
}
