package com.dev.nereya.shushme.interfaces

import com.dev.nereya.shushme.model.SoundItem

interface FirebaseCallback {
    fun uploadSound()
    fun downloadSound(sound: SoundItem)
}