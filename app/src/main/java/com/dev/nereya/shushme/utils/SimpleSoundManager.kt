import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class SimpleSoundManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var currentFile: File? = null

    fun startRecording(fileName: String = "temp") {
        currentFile = File(context.filesDir, "$fileName.3gp")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(currentFile?.absolutePath)

            prepare()
            start()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    fun playSound(fileName: String) {
        val fileToPlay = File(context.filesDir, "$fileName.3gp")

        if (fileToPlay.exists()) {
            stopPlaying()

            player = MediaPlayer().apply {
                setDataSource(fileToPlay.absolutePath)
                prepare()
                start()
            }
        }
    }

    fun stopPlaying() {
        player?.release()
        player = null
    }

    val amplitude: Int
        get() = if (recorder != null) recorder!!.maxAmplitude else 0
}