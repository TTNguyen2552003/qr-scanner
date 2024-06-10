package app.kotlin.qrscanner.ui.views

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.URLUtil
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrscanner.NOTIFICATION_BODY_SCAN_FAILED
import app.kotlin.qrscanner.NOTIFICATION_ID_SCAN_FAILED
import app.kotlin.qrscanner.NOTIFICATION_TITLE_SCAN_FAILED
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.components.Button
import app.kotlin.qrscanner.ui.components.OptionsContext
import app.kotlin.qrscanner.ui.components.ReadOnlyTextField
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.viewmodels.QRCodeAnalyzer
import app.kotlin.qrscanner.ui.viewmodels.ScanQRUiState
import app.kotlin.qrscanner.ui.viewmodels.ScanQRViewModel
import app.kotlin.qrscanner.workers.makeNotification
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanQRScreen(scanQRViewModel: ScanQRViewModel = viewModel()) {
    val scanQRUiState: State<ScanQRUiState> = scanQRViewModel.uiState.collectAsState()
    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState: PermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 32.dp)
    ) {
        LaunchedEffect(Unit) {
            scanQRViewModel.resetResult()
        }

        LaunchedEffect(Unit) {
            if (!cameraPermissionState.status.isGranted)
                cameraPermissionState.launchPermissionRequest()
        }

        LaunchedEffect(key1 = scanQRUiState.value.keyScanningEvent) {
            launch {
                if (scanQRUiState.value.autoCopy && scanQRUiState.value.barcodeResult != "") {
                    val clipBoardManager: ClipboardManager = context
                        .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

                    val clip: ClipData = ClipData.newPlainText(
                        "qr code with auto copy",
                        scanQRUiState.value.barcodeResult
                    )

                    clipBoardManager.setPrimaryClip(clip)

                    Toast.makeText(context, "QR code copied to clip board", Toast.LENGTH_LONG)
                        .show()
                }
            }

            launch {
                if (scanQRUiState.value.autoOpenWeblink) {
                    if (URLUtil.isHttpsUrl(scanQRUiState.value.barcodeResult)) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(scanQRUiState.value.barcodeResult)
                        )
                        context.startActivity(intent)
                    }
                }
            }
        }

        @Composable
        fun CTAContainer() {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(size = 200.dp)
                        .clip(shape = RectangleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!cameraPermissionState.status.isGranted) {
                        Image(
                            painter = painterResource(id = R.drawable.qr_code_place_holder_outline),
                            contentDescription = ""
                        )
                        Text(
                            text = stringResource(id = R.string.explanation_for_access_camera_permission),
                            style = MaterialTheme
                                .typography
                                .bodySmall
                                .notScale(),
                            color = onSurface,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        if (!scanQRUiState.value.isScanning) {
                            Image(
                                painter = painterResource(id = R.drawable.qr_code_place_holder),
                                contentDescription = "",
                                modifier = Modifier.size(size = 200.dp),
                                contentScale = ContentScale.FillWidth
                            )

                            var isReversedScan: Boolean by remember {
                                mutableStateOf(value = false)
                            }

                            val yPositionScan: Dp by animateDpAsState(
                                targetValue = if (isReversedScan)
                                    (-40).dp
                                else
                                    150.dp,
                                label = "y Axis position of scan bar",
                                animationSpec = tween(
                                    durationMillis = 1500,
                                    easing = LinearEasing
                                )
                            )

                            LaunchedEffect(Unit) {
                                while (true) {
                                    delay(timeMillis = 1500)
                                    isReversedScan = !isReversedScan
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(shape = RectangleShape)
                                    .drawBehind {
                                        drawRect(
                                            brush = if (isReversedScan)
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Red,
                                                        Color.Transparent
                                                    ),
                                                    startY = yPositionScan.toPx(),
                                                    endY = (yPositionScan + 40.dp).toPx()
                                                )
                                            else
                                                Brush.verticalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        Color.Red
                                                    ),
                                                    startY = yPositionScan.toPx(),
                                                    endY = (yPositionScan + 40.dp).toPx()
                                                ),
                                            topLeft = Offset(x = 0f, y = yPositionScan.toPx()),
                                            size = Size(
                                                width = 150.dp.toPx(),
                                                height = 40.dp.toPx()
                                            )
                                        )
                                    }
                            )
                        } else {
                            val cameraController: LifecycleCameraController = remember {
                                LifecycleCameraController(context).apply {
                                    bindToLifecycle(lifecycleOwner)
                                    setImageAnalysisAnalyzer(
                                        ContextCompat.getMainExecutor(context),
                                        QRCodeAnalyzer(
                                            onQRCodeDetected = {
                                                scanQRViewModel.updateQRCodeResult(newValue = it)
                                                scanQRViewModel.recordScanningEvent()
                                                scanQRViewModel.stopScanning()
                                            },
                                            onScanFailed = {
                                                scanQRViewModel.updateQRCodeResult(newValue = "")
                                                scanQRViewModel.recordScanningEvent()
                                                scanQRViewModel.stopScanning()
                                                makeNotification(
                                                    title = NOTIFICATION_TITLE_SCAN_FAILED,
                                                    body = NOTIFICATION_BODY_SCAN_FAILED,
                                                    notificationId = NOTIFICATION_ID_SCAN_FAILED,
                                                    context = context
                                                )
                                            }
                                        )
                                    )
                                }
                            }
                            CameraPreviewView(
                                cameraController = cameraController,
                                context = context
                            )
                        }
                    }
                }
                Button(
                    label = if (cameraPermissionState.status.isGranted) {
                        if (scanQRUiState.value.isScanning)
                            R.string.button_label_stop
                        else
                            R.string.button_label_scan
                    } else
                        R.string.button_label_allow,
                    onPress = {
                        if (cameraPermissionState.status.isGranted) {
                            if (!scanQRUiState.value.isScanning)
                                scanQRViewModel.startScanning()
                            else
                                scanQRViewModel.stopScanning()
                        } else {
                            val goToAppSetting: () -> Unit = {
                                val intent: Intent =
                                    Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                                intent.setData(uri)
                                context.startActivity(intent)
                            }

                            goToAppSetting()
                        }
                    }
                )
            }
        }
        CTAContainer()

        ReadOnlyTextField(value = scanQRUiState.value.barcodeResult)

        OptionsContext(
            firstOptionState = scanQRUiState.value.autoCopy,
            onFirstStateChange = { scanQRViewModel.updateAutoCopyState() },
            secondOptionState = scanQRUiState.value.autoOpenWeblink,
            onSecondStateChange = { scanQRViewModel.updateAutoOpenWebState() }
        )
    }
}

@Composable
fun CameraPreviewView(cameraController: LifecycleCameraController, context: Context) {
    val previewView: PreviewView = remember { PreviewView(context) }
    AndroidView(
        { previewView },
        modifier = Modifier.size(size = 200.dp)
    ) {
        previewView.controller = cameraController
    }
}
