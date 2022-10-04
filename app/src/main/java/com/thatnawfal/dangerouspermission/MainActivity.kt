package com.thatnawfal.dangerouspermission

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.thatnawfal.dangerouspermission.databinding.ActivityMainBinding
import java.io.IOException
import java.util.*


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity() {
    private lateinit var fileName : String

    private lateinit var binding : ActivityMainBinding
    private var audioRecordingPermissionGranted = false

    private val recorder = MediaRecorder()
    private val player = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionCheck = checkSelfPermission(Manifest.permission.RECORD_AUDIO)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission DIIZINKAN", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission DITOLAK", Toast.LENGTH_LONG).show()
            requestRecordPermission()
        }

        buttonFunction()
    }

    private fun buttonFunction() {
        binding.activityMainRecord.setOnClickListener {
            Toast.makeText(this, "start record", Toast.LENGTH_LONG)
                .show()
            startRecording()
        }

        binding.activityMainStop.setOnClickListener {
            Toast.makeText(this, "stopped", Toast.LENGTH_LONG)
                .show()
            stopRecording()
        }

        binding.activityMainPlay.setOnClickListener {
            Toast.makeText(this, "Permissions for Location Permitted", Toast.LENGTH_LONG)
                .show()
            playRecording()
        }

        binding.activityMainStopPlaying.setOnClickListener {
            stopPlaying()
        }

    }

    private fun playRecording() {
        try {
            player.setDataSource(fileName)
            player.setOnCompletionListener { stopPlaying() }
            player.prepare()
            player.start()
        } catch (e: IOException) {
            Log.e(MainActivity::class.java.simpleName + ":playRecording()", "prepare() failed")
        }
    }

    private fun stopPlaying() {
        player.stop()
        player.release();

    }

    private fun startRecording() {
        val uuid: String = UUID.randomUUID().toString()

        fileName = "${externalCacheDir?.absolutePath}/${uuid}.3gp"
        print(fileName)

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare()
        } catch (e: IOException) {
            Log.e(MainActivity::class.java.simpleName + ":startRecording()", "prepare() failed")
        }

        recorder.start();

    }

    private fun stopRecording() {
        recorder.stop();
        recorder.release();
    }

    private fun requestRecordPermission() {
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 201)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            201 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    permissions[0] == Manifest.permission.RECORD_AUDIO
                ) {
                    Toast.makeText(this, "Permissions for Location Permitted", Toast.LENGTH_LONG)
                        .show()
                    audioRecordingPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                } else {
                    Toast.makeText(this, "Permissions for Location Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        if (!audioRecordingPermissionGranted) {
            finish();
        }
    }
}