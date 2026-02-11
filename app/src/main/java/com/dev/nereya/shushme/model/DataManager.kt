package com.dev.nereya.shushme.model

import android.content.Context
import java.io.File

object DataManager {
    var currentSound: SoundItem? = SoundItem.Builder("Default Shush", "System", "system_resource", false).build()

    val sounds = mutableListOf<SoundItem>(currentSound!!)
    val sharedSounds = mutableListOf<SoundItem>()

    fun addSound(sound: SoundItem) {
        if (sounds.none { it.path == sound.path }) {
            sounds.add(sound)
        }
    }

    fun addSharedSound(sound: SoundItem) {
        sharedSounds.add(sound)
    }

    fun removeSound(sound: SoundItem) {
        sounds.remove(sound)
        if (sound.path != "system_resource") {
            val file = File(sound.path)
            if (file.exists()) file.delete()
        }
    }

    fun removeSharedSound(sound: SoundItem) {
        sharedSounds.remove(sound)
    }

    fun loadFromFiles(context: Context) {
        val filesDir = context.filesDir
        val files = filesDir.listFiles()
        val currentPath = currentSound?.path

        sounds.clear()

        val systemSound = SoundItem.Builder("Default Shush", "System", "system_resource", currentPath == "system_resource").build()
        sounds.add(systemSound)

        files?.forEach { file ->
            if (file.name != "temp.3gp" && file.name.endsWith(".3gp")) {
                val fullFileName = file.name.removeSuffix(".3gp")

                val parts = fullFileName.split("_")
                val displayName = parts.getOrNull(0) ?: "Unknown"
                val authorName = parts.getOrNull(1)?.replace("_", " ") ?: "User"

                val isItChosen = file.absolutePath == currentPath

                val localSound = SoundItem.Builder(
                    displayName,
                    authorName,
                    file.absolutePath,
                    isItChosen
                ).build()

                addSound(localSound)
            }
        }
    }
}
