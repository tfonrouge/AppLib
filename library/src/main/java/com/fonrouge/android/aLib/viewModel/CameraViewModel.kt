package com.fonrouge.android.aLib.viewModel

import android.media.ToneGenerator
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.fonrouge.android.aLib.barcode.BarcodeCamera
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

@ExperimentalGetImage
class CameraViewModel : ViewModel() {

    companion object {
        var onSelectCameraType: () -> CameraType = {
            CameraType.GooglePlay
        }
    }

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()
    val selectedCameraType: MutableState<CameraType> = mutableStateOf(onSelectCameraType())
    var lastTime: Long = 0L

    val gmsBarcodeScannerOptions by lazy {
        GmsBarcodeScannerOptions.Builder()
            .allowManualInput()
//            .enableAutoZoom()
            .build()
    }

    val barcodeCamera = mutableStateOf(BarcodeCamera())

    val torchState = mutableStateOf(false)

    data class State(
        val scannerOpen: Boolean = false,
        val codeScanned: String? = null,
    )

    fun onEvent(uiEvent: UIEvent) {
        when (uiEvent) {
            UIEvent.Open -> _uiState.value = _uiState.value.copy(scannerOpen = true)
            UIEvent.Close -> {
                torchState.value = false
                _uiState.value = _uiState.value.copy(scannerOpen = false)
            }
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
