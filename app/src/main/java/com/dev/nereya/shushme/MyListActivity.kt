package com.dev.nereya.shushme

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.nereya.shushme.adapters.SoundAdapter
import com.dev.nereya.shushme.databinding.ActivityMyListBinding
import com.dev.nereya.shushme.interfaces.SoundSelectCallback
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.model.SoundItem
import com.dev.nereya.shushme.utils.SignalManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.File

class MyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var dataManager: DataManager
    private lateinit var soundAdapter: SoundAdapter

    private val storage = Firebase.storage
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        storageRef = storage.reference.child("sounds")
        dataManager = DataManager
        initViews()
    }

    private fun initViews() {
        binding.myListBTNBrowse.setOnClickListener {
            val intent = android.content.Intent(this, SharedSoundsActivity::class.java)
            startActivity(intent)
        }
        binding.myListBackBTN.setOnClickListener {
            finish()
        }
        soundAdapter = SoundAdapter(dataManager.sounds, true)

        soundAdapter.soundCallback = object : SoundSelectCallback {
            override fun onSoundSelected(sound: SoundItem, position: Int) {
                DataManager.currentSound = sound
                soundAdapter.notifyDataSetChanged()
            }
        }
        binding.myListBTNShare.setOnClickListener {
            binding.mainMyListShareProgress.visibility = android.view.View.VISIBLE
            val soundToShare = DataManager.currentSound

            if (soundToShare == null || soundToShare.path == "system_resource") {
                SignalManager.getInstance().toast("Cannot share default sound", SignalManager.ToastLength.SHORT)
                binding.mainMyListShareProgress.visibility = android.view.View.INVISIBLE
                return@setOnClickListener
            }

            val fileUri = Uri.fromFile(File(soundToShare.path))
            val fileName = "${soundToShare.title}_${soundToShare.author}.3gp"
            val soundRef = storageRef.child("$fileName")

            val uploadTask = soundRef.putFile(fileUri)

            uploadTask.addOnFailureListener {
                SignalManager.getInstance().toast("Upload failed", SignalManager.ToastLength.SHORT)
            }.addOnSuccessListener {
                SignalManager.getInstance().toast("Upload successful", SignalManager.ToastLength.SHORT)
            }.addOnCompleteListener {
                binding.mainMyListShareProgress.visibility = android.view.View.INVISIBLE
            }
        }
        binding.myListSoundsRV.adapter = soundAdapter
        binding.myListSoundsRV.layoutManager = LinearLayoutManager(this)
        binding.myListBTNDelete.setOnClickListener {
            val soundToDelete = DataManager.currentSound

            if (soundToDelete?.title == "Default Shush") {
                SignalManager.getInstance().toast("Cannot delete default sound", SignalManager.ToastLength.SHORT)
                return@setOnClickListener
            }

            val index = soundAdapter.soundList.indexOf(soundToDelete)

            if (index != -1) {
                dataManager.currentSound = dataManager.sounds[0]
                dataManager.currentSound?.isChosen = true
                soundAdapter.notifyItemChanged(0)
                dataManager.removeSound(soundToDelete!!)
                soundAdapter.notifyItemRemoved(index)
                soundAdapter.notifyItemRangeChanged(index, soundAdapter.soundList.size)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DataManager.loadFromFiles(this)
        soundAdapter.notifyDataSetChanged()
    }
}

