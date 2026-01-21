package com.dev.nereya.shushme.utils

import android.content.Context
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class SoundMeter {

    private var mRecorder: MediaRecorder? = null
    fun start(context: Context) {
        if (mRecorder == null) {
            val tempFile = File(context.cacheDir, "temp_audio.3gp")

            mRecorder = MediaRecorder(context)
            mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            mRecorder?.setOutputFile(tempFile.absolutePath)

            try {
                mRecorder?.prepare()
                mRecorder?.start()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun stop() {
        if (mRecorder != null) {
            try {
                mRecorder?.stop()
                mRecorder?.release()
                mRecorder = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val amplitude: Int
        get() = if (mRecorder != null) mRecorder!!.maxAmplitude else 0
}