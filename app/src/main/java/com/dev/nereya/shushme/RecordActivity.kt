package com.dev.nereya.shushme

import SimpleSoundManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.nereya.shushme.databinding.ActivityRecordBinding
import androidx.core.graphics.toColorInt
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.model.SoundItem
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private lateinit var recorder: SimpleSoundManager
    private lateinit var dataManager: DataManager
    private lateinit var firebaseAuth: FirebaseAuth
    private var noiseLevel = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (!::binding.isInitialized) return
            val rawAmp = recorder.amplitude
            noiseLevel = (rawAmp / 300).coerceIn(0, 100)

            binding.noiseProgressBar.progress = noiseLevel
            handler.postDelayed(this, 200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        dataManager = DataManager
        recorder = SimpleSoundManager(this)
        initViews()
    }

    private fun initViews() {
        binding.recordBackBTN.setOnClickListener {
            recorder.startRecording()
            finish()
        }

        binding.recordSoundsContainer.setOnClickListener {
            if (recorder.isRecording == false) {
                recorder.startRecording()
                binding.recordUploadListening.visibility = View.VISIBLE
                binding.recordUploadTitle.text = getString(R.string.listening_subtitle)
                binding.noiseProgressBar.visibility = View.VISIBLE
                binding.recordUploadPic.backgroundTintList =
                    ColorStateList.valueOf("#FF1744".toColorInt())
                recorder.isRecording = true
                handler.post(runnable)
            } else {
                binding.recordUploadListening.visibility = View.GONE
                binding.recordUploadTitle.text = getString(R.string.tap_to_record)
                binding.noiseProgressBar.visibility = View.GONE
                binding.recordUploadPic.backgroundTintList =
                    ColorStateList.valueOf("#BBDEFB".toColorInt())
                binding.recordUploadTitle.text = getString(R.string.tap_to_record)
                handler.removeCallbacks(runnable)
                showRenameDialog()
                recorder.stopRecording()
            }
        }
    }

    private fun showRenameDialog() {
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("Save Recording")
            .setMessage("Enter a name for your sound:")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()

                if (newName.isNotEmpty()) {
                    val success = recorder.renameSound("temp", newName)

                    if (success) {
                        val finalFile = File(filesDir, "$newName.3gp")

                        val soundItem = SoundItem.Builder(
                            newName,
                            firebaseAuth.currentUser?.displayName ?: "User",
                            finalFile.absolutePath,
                            false
                        ).build()

                        dataManager.addSound(soundItem)

                        Toast.makeText(this, "Saved as $newName!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Name already exists or error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}