package com.fonrouge.applib.ui

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fonrouge.android.aLib.composable.CameraXCoreReaderScreen1
import com.fonrouge.android.aLib.viewModel.CameraViewModel

@Composable
fun BarcodeReaderScreen1(
    cameraViewModel: CameraViewModel = viewModel()
) {
    CameraXCoreReaderScreen1(
        cameraViewModel = cameraViewModel,
        onReadBarcode = {
            Log.d(ContentValues.TAG, "SCANNER 1: ${it.displayValue}")
        }
    ) {
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val state by cameraViewModel.uiState.collectAsState(null)
        Button(onClick = { cameraViewModel.onEvent(CameraViewModel.UIEvent.Open) }) {
            Text(text = "Scan ...", style = MaterialTheme.typography.titleLarge)
        }
        if (!state?.codeScanned.isNullOrEmpty()) {
            Spacer(modifier = Modifier.size(20.dp))
            Text(
                text = "Code read: ${state?.codeScanned}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
