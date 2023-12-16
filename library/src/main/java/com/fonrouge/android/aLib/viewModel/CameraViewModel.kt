package com.fonrouge.android.aLib.viewModel

import android.media.ToneGenerator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

class CameraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()
    var lastTime: Long = 0L

    val gmsBarcodeScannerOptions by lazy {
        GmsBarcodeScannerOptions.Builder()
            .allowManualInput()
//            .enableAutoZoom()
            .build()
    }

    data class State(
        val scannerOpen: Boolean = false,
        val codeScanned: String? = null,
    )

    fun onEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            UIEvent.Open -> _uiState.value = _uiState.value.copy(scannerOpen = true)
            UIEvent.Close -> _uiState.value = _uiState.value.copy(scannerOpen = false)
            is UIEvent.CodeRead -> {
                _uiState.value = _uiState.value.copy(codeScanned = uiEvent.codeScanned)
                lastTime = System.currentTimeMillis()
                ToneGenerator(0, ToneGenerator.MAX_VOLUME).startTone(ToneGenerator.TONE_PROP_PROMPT)
            }
        }
    }

    sealed class UIEvent {
        data object Open : UIEvent()
        data object Close : UIEvent()
        data class CodeRead(val codeScanned: String?) : UIEvent()
    }

    @Serializable
    enum class CameraType {
        GooglePlay,
        CameraX,
    }
}
