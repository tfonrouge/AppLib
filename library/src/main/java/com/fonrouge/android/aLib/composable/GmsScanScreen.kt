package com.fonrouge.android.aLib.composable

import android.content.Context
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.fonrouge.android.aLib.viewModel.CameraViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun GmsScanScreen(
    cameraViewModel: CameraViewModel,
    context: Context = LocalContext.current,
    onFailure: (Exception) -> Unit = {},
    onCanceled: () -> Unit = {},
    onReadBarcode: (Barcode) -> Unit,
    content: @Composable () -> Unit,
) {
    if (cameraViewModel.uiState.collectAsState().value.scannerOpen) {
        val scanner = GmsBarcodeScanning
            .getClient(context, cameraViewModel.gmsBarcodeScannerOptions)
        scanner.startScan()
            .addOnSuccessListener { barcode: Barcode ->
                ToneGenerator(0, ToneGenerator.MAX_VOLUME).startTone(ToneGenerator.TONE_PROP_PROMPT)
                onReadBarcode(barcode)
            }
            .addOnCanceledListener {
                onCanceled()
            }
            .addOnFailureListener { exception: Exception ->
                onFailure(exception)
            }
            .addOnCompleteListener {
                cameraViewModel.onEvent(CameraViewModel.UIEvent.Close)
            }
    } else {
        content()
    }
}