package com.dev.nereya.shushme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.nereya.shushme.databinding.ActivityMainBinding
import com.dev.nereya.shushme.utils.SingleSoundPlayer
import com.dev.nereya.shushme.utils.SoundMeter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var noiseLevel = 0
    private var threshold = 50
    private lateinit var ssp: SingleSoundPlayer
    private lateinit var sm: SoundMeter
    private val PERMISSION_REQUEST_CODE = 200
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            if (!::binding.isInitialized) return
            val rawAmp = sm.amplitude
            noiseLevel = (rawAmp / 300).coerceIn(0, 100)

            binding.noiseProgressBar.progress = noiseLevel
            binding.mainNoiseLevel.text = noiseLevel.toString()

            if (noiseLevel > threshold) {
                ssp.playSound(R.raw.shh)
            }
            handler.postDelayed(this, 200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        sm = SoundMeter()
        ssp = SingleSoundPlayer(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()

        if (checkPermission()) {
            startListening()
        } else {
            requestPermission()
        }
    }
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun startListening() {
        sm.start(this)
        handler.post(runnable)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun initViews() {
        binding.thresholdSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                threshold = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // pass
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // pass
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            sm.start(this)
        }
    }

    override fun onPause() {
        super.onPause()
        sm.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        sm.stop()
    }
}