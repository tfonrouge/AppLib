package com.fonrouge.android.aLib.composable

import android.Manifest
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fonrouge.android.aLib.barcode.BarcodeCamera
import com.fonrouge.android.aLib.viewModel.CameraViewModel
import com.fonrouge.library.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.common.Barcode

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraXCoreReaderScreen1(
    cameraViewModel: CameraViewModel,
    onReadBarcode: (Barcode) -> Unit = {},
    onFilter: ((Barcode) -> Boolean)? = null,
    content: @Composable () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        if (cameraViewModel.uiState.collectAsState().value.scannerOpen) {
            MainContent(
                viewModel = cameraViewModel,
                onReadBarcode = onReadBarcode,
                onFilter = onFilter,
            )
        } else {
            content()
        }
    } else {
        NoPermissionScreen(cameraPermissionState::launchPermissionRequest)
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun MainContent(
    viewModel: CameraViewModel = CameraViewModel(),
    onReadBarcode: (Barcode) -> Unit = {},
    onFilter: ((Barcode) -> Boolean)? = null,
) {
    val barcodeCamera by remember { mutableStateOf(BarcodeCamera()) }
    var torch by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .drawWithContent {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val width = canvasWidth * .9f
                            val height = width * 3 / 4f

                            drawContent()

                            drawRect(Color(0x99000000))

                            // Draws the rectangle in the middle
                            drawRoundRect(
                                topLeft = Offset(
                                    (canvasWidth - width) / 2,
                                    canvasHeight * .3f
                                ),
                                size = Size(width, height),
                                color = Color.Transparent,
                                cornerRadius = CornerRadius(24.dp.toPx()),
                                blendMode = BlendMode.SrcIn
                            )

                            // Draws the rectangle outline
                            drawRoundRect(
                                topLeft = Offset(
                                    (canvasWidth - width) / 2,
                                    canvasHeight * .3f
                                ),
                                color = Color.White,
                                size = Size(width, height),
                                cornerRadius = CornerRadius(24.dp.toPx()),
                                style = Stroke(
                                    width = 2.dp.toPx()
                                ),
                                blendMode = BlendMode.Src
                            )
                        }
                ) {
                    barcodeCamera.CameraPreview(viewModel, onReadBarcode, onFilter)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Lector de Código de Barras",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Image(
                                painter = if (torch) painterResource(id = R.drawable.ic_torch_on) else painterResource(
                                    id = R.drawable.ic_torch
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        torch = !torch
                                        barcodeCamera.toggleFlash(torch)
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NoPermissionScreen(
    onRequestPermission: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Please grant the permission to use the camera",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.size(40.dp))
        Button(onClick = onRequestPermission) {
            Icon(imageVector = Icons.Default.Camera, contentDescription = "Camera")
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Grant permission")
        }
    }
}
