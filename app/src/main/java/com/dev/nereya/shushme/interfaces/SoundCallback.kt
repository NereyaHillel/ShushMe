package com.dev.nereya.shushme.interfaces

import com.dev.nereya.shushme.model.SoundItem

interface SoundCallback {
    fun onSoundSelected(sound: SoundItem, position: Int)
}