package com.fonrouge.android.aLib.composable

import android.content.Context
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.fonrouge.android.aLib.viewModel.BaseViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun GmsScanScreen(
    baseViewModel: BaseViewModel<*>,
    context: Context = LocalContext.current,
    onFailure: (Exception) -> Unit = {},
    onCanceled: () -> Unit = {},
    onSuccess: (Barcode) -> Unit,
) {
    val scanner = GmsBarcodeScanning.getClient(context, baseViewModel.gmsBarcodeScannerOptions)
    scanner.startScan()
        .addOnSuccessListener { barcode: Barcode ->
            ToneGenerator(0, ToneGenerator.MAX_VOLUME).startTone(ToneGenerator.TONE_PROP_PROMPT)
            onSuccess(barcode)
        }
        .addOnCanceledListener {
            onCanceled()
        }
        .addOnFailureListener { exception: Exception ->
            onFailure(exception)
        }
        .addOnCompleteListener {
            baseViewModel.startScan.value = false
        }
}