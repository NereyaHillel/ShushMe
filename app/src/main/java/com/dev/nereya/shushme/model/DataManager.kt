package com.dev.nereya.shushme.model

import android.content.Context
import java.io.File

object DataManager {
    var currentSound: SoundItem? =
        SoundItem.Builder("Default Shush", "System", "system_resource", false).build()

    val sounds = mutableListOf<SoundItem>(currentSound!!)
    val sharedSounds = mutableListOf<SoundItem>()

    fun addSound(sound: SoundItem) {
        if (sounds.none { it.path == sound.path }) {
            sounds.add(sound)
        }
    }


    fun removeSound(sound: SoundItem) {
        if (sound.path == "system_resource" || sound.title == "Default Shush") {
            return
        }
        if (sounds.contains(sound)) {
            val isDeletingCurrent = (currentSound == sound)

            sounds.remove(sound)

            val file = File(sound.path)
            if (file.exists()) file.delete()

            if (isDeletingCurrent && sounds.isNotEmpty()) {
                currentSound = sounds.first()
            }
        }
    }

    fun loadFromFiles(context: Context) {
        val filesDir = context.filesDir
        val files = filesDir.listFiles()
        val currentPath = currentSound?.path

        sounds.clear()

        val systemSound = SoundItem.Builder(
            "Default Shush",
            "System",
            "system_resource",
            currentPath == "system_resource"
        ).build()
        sounds.add(systemSound)

        files?.forEach { file ->
            if (file.name != "temp.m4a" && file.name.endsWith(".m4a")) {
                val fullFileName = file.name.removeSuffix(".m4a")

                val parts = fullFileName.split("_")
                val displayName = parts.getOrNull(0) ?: "Unknown"
                val authorName = parts.getOrNull(1) ?: "User"

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
