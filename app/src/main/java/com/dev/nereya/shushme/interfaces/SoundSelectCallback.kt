package com.dev.nereya.shushme.interfaces

import com.dev.nereya.shushme.model.SoundItem

interface SoundSelectCallback {
    fun onSoundSelected(sound: SoundItem, position: Int)
}