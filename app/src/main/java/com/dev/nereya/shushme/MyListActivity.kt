package com.dev.nereya.shushme

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.nereya.shushme.adapters.SoundAdapter
import com.dev.nereya.shushme.databinding.ActivityMyListBinding
import com.dev.nereya.shushme.interfaces.SoundCallback
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.model.SoundItem

class MyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyListBinding

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
        initViews()

        dataManager = DataManager
        soundAdapter = SoundAdapter(dataManager.sounds,true)
        soundAdapter.soundCallback = object : SoundCallback {
            override fun onSoundSelected(sound: SoundItem, position: Int) {
                DataManager.currentSound = sound
                soundAdapter.notifyItemChanged(position)
            }
        }
        binding.myListSoundsRV.adapter = soundAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.myListSoundsRV.layoutManager = linearLayoutManager
    }

    private fun initViews() {
        binding.myListBTNSharedSounds.setOnClickListener {
            val intent = android.content.Intent(this, SharedSoundsActivity::class.java)
            startActivity(intent)
        }
        binding.myListBackBTN.setOnClickListener {
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        soundAdapter.notifyDataSetChanged()
    }

}

