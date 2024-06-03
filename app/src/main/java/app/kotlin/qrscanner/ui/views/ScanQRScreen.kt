package app.kotlin.qrscanner.ui.views

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    scanQRViewModel: ScanQRViewModel = viewModel(factory = ScanQRViewModel.factory)
) {
    val scanQRUiState: State<ScanQRUiState> = scanQRViewModel.uiState.collectAsState()

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

                        Button(onClick = { scanQRViewModel.scanQRCode() }) {
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
                    onValueChange = { },
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