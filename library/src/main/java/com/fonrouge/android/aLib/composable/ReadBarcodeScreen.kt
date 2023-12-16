package com.fonrouge.android.aLib.composable

import androidx.compose.runtime.Composable
import com.fonrouge.android.aLib.apiServices.AppApi
import com.fonrouge.android.aLib.viewModel.CameraViewModel
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun ReadBarcodeScreen(
    cameraViewModel: CameraViewModel,
    onReadBarcode: (Barcode) -> Unit = {},
    content: @Composable () -> Unit,
) {
    when (AppApi.cameraType) {
        CameraViewModel.CameraType.GooglePlay -> {
            GmsScanScreen(
                cameraViewModel = cameraViewModel,
                onReadBarcode = onReadBarcode,
                content = content
            )
        }

        CameraViewModel.CameraType.CameraX -> {
            CameraXCoreReaderScreen1(
                cameraViewModel = cameraViewModel,
                onReadBarcode = onReadBarcode,
                content = content
            )
        }
    }
}