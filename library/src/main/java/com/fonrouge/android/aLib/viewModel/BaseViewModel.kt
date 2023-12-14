package com.fonrouge.android.aLib.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.fonrouge.fsLib.model.state.SimpleState
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T : Any> : ViewModel() {

    val startScan = mutableStateOf(false)
    val gmsBarcodeScannerOptions by lazy {
        GmsBarcodeScannerOptions.Builder()
            .allowManualInput()
//            .enableAutoZoom()
            .build()
    }

    private val _snackBarStatus = MutableStateFlow<SimpleState?>(null)
    val snackBarStatus = _snackBarStatus.asStateFlow()

    fun pushSimpleState(simpleState: SimpleState?) {
        _snackBarStatus.value = simpleState
    }
}
