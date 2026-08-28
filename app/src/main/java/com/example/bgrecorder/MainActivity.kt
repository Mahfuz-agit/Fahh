package com.example.bgrecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bgrecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecordingsAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            startRecordingService(RecordingService.ACTION_START)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecordingsAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnStart.setOnClickListener { onStartClicked() }
        binding.btnPause.setOnClickListener { startRecordingService(RecordingService.ACTION_PAUSE) }
        binding.btnResume.setOnClickListener { startRecordingService(RecordingService.ACTION_RESUME) }
        binding.btnStop.setOnClickListener {
            startRecordingService(RecordingService.ACTION_STOP)
            refreshList()
        }

        refreshList()
        updateButtonStates()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
        updateButtonStates()
    }

    private fun onStartClicked() {
        val perms = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val needed = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isEmpty()) {
            startRecordingService(RecordingService.ACTION_START)
        } else {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    private fun startRecordingService(action: String) {
        val intent = Intent(this, RecordingService::class.java).apply { this.action = action }
        ContextCompat.startForegroundService(this, intent)
        updateButtonStates()
    }

    private fun updateButtonStates() {
        binding.btnStart.isEnabled = !RecordingService.isRecording
        binding.btnPause.isEnabled = RecordingService.isRecording && !RecordingService.isPaused
        binding.btnResume.isEnabled = RecordingService.isRecording && RecordingService.isPaused
        binding.btnStop.isEnabled = RecordingService.isRecording
    }

    private fun refreshList() {
        adapter.update(RecordingUtils.listRecordings(this))
    }
}
