package app.kotlin.qrscanner.ui.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.viewmodels.ScanQRUiState
import app.kotlin.qrscanner.ui.viewmodels.ScanQRViewModel

@Preview
@Composable
fun ScanQRScreen(
    scanQRViewModel: ScanQRViewModel = viewModel()
) {
    val scanQRUiState: State<ScanQRUiState> = scanQRViewModel.uiState.collectAsState()

    val context: Context = LocalContext.current

    LaunchedEffect(key1 = scanQRUiState.value.barcodeResult) {
        if (scanQRUiState.value.autoCopy) {
            val clipBoardManager: ClipboardManager = context
                .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            val clip: ClipData = ClipData.newPlainText(
                "qr code with auto copy",
                scanQRUiState.value.barcodeResult
            )

            clipBoardManager.setPrimaryClip(clip)

            Toast.makeText(context, "QR code copied to clip board", Toast.LENGTH_LONG).show()
        }

        if (scanQRUiState.value.autoOpenWeblink) {
            if (URLUtil.isHttpsUrl(scanQRUiState.value.barcodeResult)){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scanQRUiState.value.barcodeResult))
                context.startActivity(intent)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        @Composable
        fun ContextWrapper() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 28.dp,
                        end = 28.dp,
                        top = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 40.dp)
            ) {
                @Composable
                fun CTAContainer() {
                    Column(
                        modifier = Modifier.wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.qr_code),
                            contentDescription = ""
                        )

                        Button(onClick = { scanQRViewModel.scanQRCode(context = context) }) {
                            Text(
                                text = "Scan",
                                style = MaterialTheme
                                    .typography
                                    .labelSmall
                                    .notScale()
                            )
                        }
                    }
                }
                CTAContainer()

                TextField(
                    value = scanQRUiState.value.barcodeResult,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    label = {
                        Text(
                            text = "Output",
                            style = MaterialTheme
                                .typography
                                .bodySmall
                                .notScale()
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme
                        .typography
                        .bodyLarge
                        .notScale()
                )

                @Composable
                fun Options() {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(space = 24.dp)
                    ) {
                        @Composable
                        fun Option(
                            title: String,
                            enable: Boolean,
                            onChangeSwitch: () -> Unit
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme
                                        .typography
                                        .titleMedium
                                        .notScale()
                                )

                                Switch(
                                    checked = enable,
                                    onCheckedChange = { onChangeSwitch() }
                                )
                            }
                        }

                        Option(
                            title = "Automatically copy to the clipboard",
                            enable = scanQRUiState.value.autoCopy,
                            onChangeSwitch = { scanQRViewModel.updateAutoCopyState() }
                        )

                        Option(
                            title = "Automatically open the web link",
                            enable = scanQRUiState.value.autoOpenWeblink,
                            onChangeSwitch = { scanQRViewModel.updateAutoOpenWebState() }
                        )
                    }
                }
                Options()
            }
        }
        ContextWrapper()
    }
}