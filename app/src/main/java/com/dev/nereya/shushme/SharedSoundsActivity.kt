package com.dev.nereya.shushme

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.nereya.shushme.adapters.SoundAdapter
import com.dev.nereya.shushme.databinding.ActivitySharedSoundsBinding
import com.dev.nereya.shushme.interfaces.FirebaseStorageCallback
import com.dev.nereya.shushme.interfaces.SoundPlayerCallback
import com.dev.nereya.shushme.interfaces.SoundSelectCallback
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.model.SoundItem
import com.dev.nereya.shushme.utils.SingleSoundPlayer
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.File

class SharedSoundsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedSoundsBinding
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private val dataManager = DataManager
    private lateinit var ssp: SingleSoundPlayer
    private lateinit var soundAdapter: SoundAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedSoundsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ssp = SingleSoundPlayer(this)
        soundAdapter = SoundAdapter(dataManager.sharedSounds, false)

        fetchSharedSounds()
        initViews()
        soundAdapter.soundCallback = object : SoundSelectCallback {
            override fun onSoundSelected(sound: SoundItem, position: Int) {
                storage.getReference(sound.path).downloadUrl.addOnSuccessListener { uri ->
                    ssp.playUrl(uri.toString(), object : SoundPlayerCallback {
                        override fun onPlaybackFinished() {
                        }
                    })

                }.addOnFailureListener {
                    Log.d("SharedSounds", "Could not fetch URL", it)
                }
            }
        }

        soundAdapter.firebaseCallback = object : FirebaseStorageCallback {
            override fun uploadSound() {
                //pass
            }

            override fun downloadSound(sound: SoundItem) {
                val soundRef = storageRef.child(sound.path)
                val fileName = "${sound.title}_${sound.author}.3gp"

                val localFile = File(filesDir, fileName)

                soundRef.getFile(localFile).addOnSuccessListener {
                    android.widget.Toast.makeText(
                        this@SharedSoundsActivity,
                        "Saved to My Sounds!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->
                    android.widget.Toast.makeText(
                        this@SharedSoundsActivity,
                        "Download failed",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    Log.e("SharedSounds", "Download Error", e)
                }
            }
        }
    }

    private fun fetchSharedSounds() {
        binding.sharedSoundsFirebaseProgress.visibility = android.view.View.VISIBLE
        storageRef.child("sounds").listAll()
            .addOnSuccessListener { listResult ->
                val newSoundList = ArrayList<SoundItem>()

                for (itemRef in listResult.items) {
                    val rawName = itemRef.name

                    val nameWithoutExt = rawName.substringBeforeLast(".")
                    val parts = nameWithoutExt.split("_")

                    val title = parts[0]
                    val author = if (parts.size > 1) parts[1] else "Unknown"

                    val path = itemRef.path

                    val sound = SoundItem.Builder(
                        title,
                        author,
                        path,
                        false
                    ).build()
                    newSoundList.add(sound)
                }

                dataManager.sharedSounds.clear()
                dataManager.sharedSounds.addAll(newSoundList)

                soundAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", "Error listing files", it)
            }
            .addOnCompleteListener {
                binding.sharedSoundsFirebaseProgress.visibility = android.view.View.INVISIBLE
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        ssp.release()
    }

    fun initViews() {

        binding.sharedSoundsRV.adapter = soundAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        binding.sharedSoundsRV.layoutManager = linearLayoutManager
        binding.sharedBackBTN.setOnClickListener {
            finish()
        }
    }
}