package com.dev.nereya.shushme

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.nereya.shushme.adapters.SoundAdapter
import com.dev.nereya.shushme.databinding.ActivitySharedSoundsBinding
import com.dev.nereya.shushme.interfaces.SoundCallback
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.model.SoundItem

class SharedSoundsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySharedSoundsBinding
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
        initViews()


        val dataManager = DataManager
        val soundAdapter = SoundAdapter(dataManager.sharedSounds)
        soundAdapter.soundCallback = object : SoundCallback {
            override fun onSoundSelected(sound: SoundItem, position: Int) {
                soundAdapter.notifyItemChanged(position)
            }
        }
        binding.sharedSoundsRV.adapter = soundAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.sharedSoundsRV.layoutManager = linearLayoutManager
    }

    fun initViews(){
        binding.sharedBackBTN.setOnClickListener {
            finish()
        }
    }
}