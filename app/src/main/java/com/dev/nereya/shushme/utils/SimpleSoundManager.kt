import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class SimpleSoundManager(private val context: Context) {

    private var recorder: MediaRecorder? = null
    var currentFile: File? = null
    var isRecording : Boolean = false

    fun startRecording(fileName: String = "temp") {
        stopRecording()
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
        try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
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

    fun deleteCurrentFile() {
        currentFile?.delete()
    }

    fun isNameAvailable(name: String): Boolean {
        val file = File(context.filesDir, "$name.3gp")
        return !file.exists()
    }
    val amplitude: Int
        get() = if (recorder != null) recorder!!.maxAmplitude else 0
}