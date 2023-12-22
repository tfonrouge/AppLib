package com.fonrouge.android.aLib.composable

import androidx.compose.runtime.Composable
import com.fonrouge.android.aLib.viewModel.ViewModelCamera
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun ScanBarcodeScreen(
    viewModelCamera: ViewModelCamera,
    onReadBarcode: (Barcode) -> Unit = {},
    onFilter: ((Barcode) -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    when (viewModelCamera.selectedCameraType.value) {
        ViewModelCamera.CameraType.GooglePlay -> {
            GmsScanScreen(
                viewModelCamera = viewModelCamera,
                onReadBarcode = onReadBarcode,
                content = content
            )
        }

        ViewModelCamera.CameraType.CameraX -> {
            CameraXCoreReaderScreen1(
                viewModelCamera = viewModelCamera,
                onReadBarcode = onReadBarcode,
                onFilter = onFilter,
                content = content
            )
        }
    }
}