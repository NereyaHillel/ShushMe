package com.dev.nereya.shushme

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
import com.google.firebase.auth.FirebaseAuth

class MyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var dataManager: DataManager
    private lateinit var soundAdapter: SoundAdapter


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
        dataManager = DataManager
        initViews()
    }

    private fun initViews() {
        binding.myListBTNSharedSounds.setOnClickListener {
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
        binding.myListSoundsRV.adapter = soundAdapter
        binding.myListSoundsRV.layoutManager = LinearLayoutManager(this)
        binding.myListBTNDelete.setOnClickListener {
            val soundToDelete = DataManager.currentSound

            if (soundToDelete?.title == "Default Shush") {
                Toast.makeText(this, "Cannot delete default sound", Toast.LENGTH_SHORT).show()
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

