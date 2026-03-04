package com.dev.nereya.shushme.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.math.sqrt

class AudioManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    var currentFile: File? = null
    private var listener: AudioRecord? = null
    var isRecording: Boolean = false

    // We will store the live RMS value here for MainActivity to read
    var currentAmplitude: Int = 0
        private set

    fun startListening() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) return

        // 1. Define standard audio settings
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT

        // 2. Ask Android for the safe buffer size
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return
        }

        // 3. Initialize the AudioRecord safely
        listener = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (listener?.state != AudioRecord.STATE_INITIALIZED) {
            return
        }

        listener?.startRecording()
        isRecording = true

        // 4. Start a background thread to continuously read the live sound
        Thread {
            val buffer = ShortArray(bufferSize)
            while (isRecording) {
                // Read a chunk of audio from the microphone
                val readResult = listener?.read(buffer, 0, bufferSize) ?: 0

                if (readResult > 0) {
                    // Calculate RMS (Average Loudness)
                    var sum = 0.0
                    for (i in 0 until readResult) {
                        sum += buffer[i] * buffer[i]
                    }
                    val rms = sqrt(sum / readResult)


                    currentAmplitude = rms.toInt()
                }
            }
        }.start()
    }

    fun stopListening() {
        isRecording = false
        try {
            listener?.stop()
            listener?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            listener = null
            currentAmplitude = 0
        }
    }



    fun startRecording(fileName: String = "temp") {
        stopRecording()

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            currentFile = File(context.filesDir, "$fileName.m4a")
            setOutputFile(currentFile?.absolutePath)

            try {
                prepare()
                start()
                isRecording = true
            } catch (e: Exception) {
                e.printStackTrace()
                stopRecording()
            }
        }
    }
    fun stopRecording() {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            recorder?.reset()
            recorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
            isRecording = false
        }
    }
    fun renameSound(currentName: String, newName: String): Boolean {
        val sourceFile = File(context.filesDir, "$currentName.m4a")
        val destFile = File(context.filesDir, "$newName.m4a")

        if (!sourceFile.exists()) return false
        if (destFile.exists()) return false

        return sourceFile.renameTo(destFile)
    }

}