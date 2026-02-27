import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class SoundManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    var currentFile: File? = null
    var isRecording : Boolean = false

    fun startRecording(fileName: String = "temp") {
        stopRecording()

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

            if (fileName == "temp") {
                setOutputFile("/dev/null")
            } else {
                currentFile = File(context.filesDir, "$fileName.3gp")
                setOutputFile(currentFile?.absolutePath)
            }

            try {
                prepare()
                start()
                isRecording = true
            } catch (e: Exception) {
                e.printStackTrace()
                stopRecording()
            }
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            recorder?.reset()
            recorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder = null
            isRecording = false
        }
    }


    fun renameSound(currentName: String, newName: String): Boolean {
        val sourceFile = File(context.filesDir, "$currentName.3gp")
        val destFile = File(context.filesDir, "$newName.3gp")

        if (!sourceFile.exists()) return false
        if (destFile.exists()) return false

        return sourceFile.renameTo(destFile)
    }

    val amplitude: Int
        get() = if (recorder != null) recorder!!.maxAmplitude else 0
}