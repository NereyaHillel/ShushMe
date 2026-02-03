package com.dev.nereya.shushme.model

object DataManager {
    var currentSound: SoundItem? = null
    val sounds = mutableListOf<SoundItem>()
    val sharedSounds = mutableListOf<SoundItem>()


    fun addSound(sound: SoundItem) {
        sounds.add(sound)
    }
    fun addSharedSound(sound: SoundItem) {
        sharedSounds.add(sound)
    }

    fun removeSound(sound: SoundItem) {
        sounds.remove(sound)
    }
    fun removeSharedSound(sound: SoundItem) {
        sharedSounds.remove(sound)
    }
}