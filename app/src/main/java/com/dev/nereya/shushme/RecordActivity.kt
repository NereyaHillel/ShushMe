package com.dev.nereya.shushme

import com.dev.nereya.shushme.utils.AudioManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.nereya.shushme.databinding.ActivityRecordBinding
import androidx.core.graphics.toColorInt
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.model.SoundItem
import com.dev.nereya.shushme.utils.SignalManager
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class RecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecordBinding
    private lateinit var recorder: AudioManager
    private lateinit var dataManager: DataManager
    private lateinit var firebaseAuth: FirebaseAuth

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
        recorder = AudioManager(this)
        initViews()
    }

    private fun initViews() {
        binding.recordBackBTN.setOnClickListener {
            recorder.stopRecording()
            finish()
        }

        binding.recordSoundsContainer.setOnClickListener {
            if (!recorder.isRecording) {
                recorder.startRecording()
                binding.recordUploadListening.visibility = View.VISIBLE
                binding.recordUploadTitle.text = getString(R.string.listening_subtitle)
                binding.noiseProgressBar.visibility = View.VISIBLE
                binding.recordUploadPic.backgroundTintList =
                    ColorStateList.valueOf("#FF1744".toColorInt())
                recorder.isRecording = true
            } else {
                binding.recordUploadListening.visibility = View.GONE
                binding.recordUploadTitle.text = getString(R.string.tap_to_record)
                binding.noiseProgressBar.visibility = View.GONE
                binding.recordUploadPic.backgroundTintList =
                    ColorStateList.valueOf("#BBDEFB".toColorInt())
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
                    firebaseAuth.currentUser?.reload()?.addOnCompleteListener { _ ->
                        val userName = firebaseAuth.currentUser?.displayName ?: "User"
                        val persistentName = "${newName}_${userName}"
                        val success = recorder.renameSound("temp", persistentName)

                        if (success) {
                            val finalFile = File(filesDir, "$persistentName.m4a")

                            val soundItem = SoundItem.Builder(
                                newName,
                                userName,
                                finalFile.absolutePath,
                                false
                            ).build()

                            dataManager.addSound(soundItem)
                            SignalManager.getInstance()
                                .toast("Saved as $newName", SignalManager.ToastLength.SHORT)
                            finish()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}