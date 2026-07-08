package com.voiceshift.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.voiceshift.R
import com.voiceshift.audio.AudioForegroundService
import com.voiceshift.audio.RouteMode
import kotlinx.coroutines.launch
import com.voiceshift.App

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as App
        HomeViewModel.Factory(app.repository, app.audioEngine, app.audioRouter)
    }
    
    private lateinit var waveformAnim: LottieAnimationView
    private lateinit var btnStartStop: MaterialButton
    private lateinit var btnRouteToggle: ImageButton
    private lateinit var rvPresets: RecyclerView
    private lateinit var presetAdapter: PresetAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                toggleAudioState()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        waveformAnim = view.findViewById(R.id.waveformAnim)
        btnStartStop = view.findViewById(R.id.btnStartStop)
        btnRouteToggle = view.findViewById(R.id.btnRouteToggle)
        rvPresets = view.findViewById(R.id.rvPresets)

        presetAdapter = PresetAdapter { preset ->
            viewModel.selectPreset(preset)
        }
        rvPresets.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = presetAdapter
        }

        btnStartStop.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                toggleAudioState()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        btnRouteToggle.setOnClickListener {
            viewModel.toggleRoute()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.presets.collect { presets ->
                        presetAdapter.submitList(presets)
                    }
                }
                launch {
                    viewModel.isRunning.collect { isRunning ->
                        if (isRunning) {
                            btnStartStop.text = getString(R.string.stop)
                            waveformAnim.playAnimation()
                            startForegroundService()
                        } else {
                            btnStartStop.text = getString(R.string.start)
                            waveformAnim.pauseAnimation()
                            stopForegroundService()
                        }
                    }
                }
                launch {
                    viewModel.routeMode.collect { mode ->
                        if (mode == RouteMode.SPEAKER) {
                            btnRouteToggle.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
                        } else {
                            btnRouteToggle.setImageResource(android.R.drawable.ic_menu_call)
                        }
                    }
                }
            }
        }
    }

    private fun toggleAudioState() {
        viewModel.toggleAudio()
    }

    private fun startForegroundService() {
        val intent = Intent(requireContext(), AudioForegroundService::class.java)
        requireContext().startForegroundService(intent)
    }

    private fun stopForegroundService() {
        val intent = Intent(requireContext(), AudioForegroundService::class.java)
        requireContext().stopService(intent)
    }
}
