package com.dev.nereya.shushme

import SoundManager
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.nereya.shushme.databinding.ActivityMainBinding
import com.dev.nereya.shushme.model.DataManager
import com.dev.nereya.shushme.utils.SingleSoundPlayer
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.dev.nereya.shushme.interfaces.SoundPlayerCallback
import com.dev.nereya.shushme.utils.SignalManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var dataManager = DataManager
    private var noiseLevel = 0
    private var threshold = 50
    private lateinit var ssp: SingleSoundPlayer
    private lateinit var sm: SoundManager
    private val PERMISSION_REQUEST_CODE = 200
    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val rawAmp = sm.amplitude
            noiseLevel = (rawAmp / 300).coerceIn(0, 100)

            binding.noiseProgressBar.progress = noiseLevel
            binding.mainNoiseLevel.text = noiseLevel.toString()

            if (noiseLevel > threshold) {
                handler.removeCallbacks(this)
                sm.stopRecording()
                binding.noiseProgressBar.progress = 0
                binding.mainNoiseLevel.text = "SHHH!"
//                SignalManager.getInstance().vibrate()

                ssp.play(object : SoundPlayerCallback {
                    override fun onPlaybackFinished() {
                        dataManager.currentSound?.let {
                            ssp.prepareCurrentSound(it.path)
                        }
                        sm.startRecording()
                        handler.post(runnable)
                    }
                })
            } else {
                handler.postDelayed(this, 200)
            }
        }
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sm = SoundManager(this)
        ssp = SingleSoundPlayer(this)
        auth = Firebase.auth

        initViews()

        if (!checkPermission()) {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun startListening() {
        sm.startRecording()
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    private fun stopListening() {
        sm.stopRecording()
        handler.removeCallbacks(runnable)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun initViews() {
        binding.mainSoundName.text = dataManager.currentSound?.title
        binding.thresholdSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                threshold = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnCancel.setOnClickListener {
            binding.loginRequiredContainer.visibility = View.GONE
        }
        updateUI()
    }

    private fun updateUI() {
        val user = auth.currentUser

        if (user != null) {
            binding.logInOutBtn.setImageResource(R.drawable.logout_icon)
            val name = if (user.displayName?.isEmpty() == true) "User" else user.displayName
            binding.usernameTitle.text = "Hello, $name"

            binding.mainRecord.setOnClickListener {
                startActivity(Intent(this, RecordActivity::class.java))
            }
            binding.mainMyList.setOnClickListener {
                startActivity(Intent(this, MyListActivity::class.java))
            }

            binding.logInOutBtn.setOnClickListener {
                auth.signOut()
                binding.loginRequiredContainer.visibility = View.GONE
                binding.usernameTitle.text = "Hello, Guest"
                updateUI()
            }

        } else {
            binding.logInOutBtn.setImageResource(R.drawable.login_icon)
            binding.logInOutBtn.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            binding.mainRecord.setOnClickListener {
                binding.loginRequiredContainer.visibility = View.VISIBLE
            }
            binding.mainMyList.setOnClickListener {
                binding.loginRequiredContainer.visibility = View.VISIBLE
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        binding.mainSoundName.text = dataManager.currentSound?.title
        dataManager.currentSound?.let {
            ssp.prepareCurrentSound(it.path)
        }
        if (checkPermission()) {
            startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        stopListening()
        ssp.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        stopListening()
        ssp.release()
    }
}