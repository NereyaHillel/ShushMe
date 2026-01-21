package com.dev.nereya.shushme.utils

import android.content.Context
import android.media.MediaPlayer


class SingleSoundPlayer(context: Context) {
    private val context: Context = context.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    fun playSound(resourceId: Int) {
        if (mediaPlayer?.isPlaying == true) {
            return
        }
        release()

        mediaPlayer = MediaPlayer.create(context, resourceId)

        mediaPlayer?.setOnCompletionListener {
            release()
        }
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}