package com.dev.nereya.shushme.interfaces

import com.dev.nereya.shushme.model.SoundItem

interface FirebaseStorageCallback {
    fun uploadSound()
    fun downloadSound(sound: SoundItem)
}