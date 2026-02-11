package com.dev.nereya.shushme.utils

import android.content.Context
import android.media.MediaPlayer
import com.dev.nereya.shushme.R
import com.dev.nereya.shushme.interfaces.SoundPlayerCallback

class SingleSoundPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun prepareCurrentSound(path: String) {
        try {
            release()
            mediaPlayer = MediaPlayer().apply {
                if (path == "system_resource") {
                    val afd = context.resources.openRawResourceFd(R.raw.shh)
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                } else {
                    setDataSource(path)
                }
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            release()
        }
    }

    fun play(callback: SoundPlayerCallback) {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.setOnCompletionListener {
                    callback.onPlaybackFinished()
                }
                it.setOnErrorListener { mp, what, extra ->
                    release()
                    callback.onPlaybackFinished()
                    true
                }
                it.start()
            }
        }
    }

    fun release() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }
}