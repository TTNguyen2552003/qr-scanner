package app.kotlin.qrscanner.ui.views

import android.content.ClipData
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.viewmodels.ReadImageUiState
import app.kotlin.qrscanner.ui.viewmodels.ReadImageViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun ReadImageScreen(
    readImageViewModel: ReadImageViewModel = viewModel()
) {
    val readImageUiState: State<ReadImageUiState> = readImageViewModel
        .uiState
        .collectAsState()

    val context: Context = LocalContext.current

    LaunchedEffect(key1 = readImageUiState.value.barcodeResult) {
        if (readImageUiState.value.autoCopy) {
            val clipBoardManager: android.content.ClipboardManager = context
                .getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager

            val clip: ClipData = ClipData.newPlainText(
                "qr code with auto copy",
                readImageUiState.value.barcodeResult
            )

            clipBoardManager.setPrimaryClip(clip)

            Toast.makeText(context, "QR code copied to clip board", Toast.LENGTH_LONG).show()
        }

        if (readImageUiState.value.autoOpenWeblink) {
            if (URLUtil.isHttpsUrl(readImageUiState.value.barcodeResult)) {
                val intent: Intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(readImageUiState.value.barcodeResult))
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

                        rememberAsyncImagePainter(model = readImageUiState.value.pickedImage)
                        Image(
                            painter = if (readImageUiState.value.pickedImage == null)
                                painterResource(id = R.drawable.add_photo_place_holder)
                            else
                                rememberAsyncImagePainter(model = readImageUiState.value.pickedImage),
                            contentDescription = "",
                            modifier = Modifier.size(size = 200.dp),
                            contentScale = ContentScale.Fit
                        )

                        val pickImageLauncher: ManagedActivityResultLauncher<String, Uri?> =
                            rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.GetContent()
                            ) { uri: Uri? ->
                                readImageViewModel.apply {
                                    updatePickedImage(newUri = uri)
                                    analyzeImage(context = context)
                                }
                            }
                        Button(onClick = { pickImageLauncher.launch(input = "image/*") }) {
                            Text(
                                text = "Load",
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
                    value = readImageUiState.value.barcodeResult,
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
                            enable = readImageUiState.value.autoCopy,
                            onChangeSwitch = { readImageViewModel.updateAutoCopyState() }
                        )

                        Option(
                            title = "Automatically open the web link",
                            enable = readImageUiState.value.autoOpenWeblink,
                            onChangeSwitch = { readImageViewModel.updateAutoOpenWebState() }
                        )
                    }
                }
                Options()
            }
        }
        ContextWrapper()
    }
}