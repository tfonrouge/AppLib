package com.fonrouge.applib.ui

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fonrouge.android.aLib.ui.ScanBarcodeScreen
import com.fonrouge.android.aLib.viewModel.ViewModelCamera

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeReaderScreen1(
    viewModelCamera: ViewModelCamera = viewModel()
) {
    ScanBarcodeScreen(viewModelCamera = viewModelCamera) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val state by viewModelCamera.uiState.collectAsState(null)
            var expandedCameraSelector by remember { mutableStateOf(false) }
            var selectedCameraType by remember {
                viewModelCamera.selectedCameraType
            }
            ExposedDropdownMenuBox(
                expanded = expandedCameraSelector,
                onExpandedChange = { expandedCameraSelector = !expandedCameraSelector }
            ) {
                TextField(
                    value = "$selectedCameraType",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCameraSelector,
                    onDismissRequest = { expandedCameraSelector = false }) {
                    ViewModelCamera.CameraType.entries.forEach { cameraType ->
                        DropdownMenuItem(
                            text = { Text(text = "$cameraType") },
                            onClick = {
                                selectedCameraType = cameraType
                                expandedCameraSelector = false
                            },
                            leadingIcon = {
                                if (selectedCameraType == cameraType) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "selectedIcon"
                                    )
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Button(onClick = { viewModelCamera.onEvent(ViewModelCamera.UIEvent.Open) }) {
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
}
