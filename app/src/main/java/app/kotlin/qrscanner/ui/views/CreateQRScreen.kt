package app.kotlin.qrscanner.ui.views

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.viewmodels.CreateQRUiState
import app.kotlin.qrscanner.ui.viewmodels.CreateQRViewModel

@Composable
fun CreateQRScreen(
    createQRViewModel: CreateQRViewModel = viewModel(factory = CreateQRViewModel.factory)
) {
    val createQRUiState: State<CreateQRUiState> = createQRViewModel
        .uiState
        .collectAsState()

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

                var bitmap: Bitmap? by remember {
                    mutableStateOf(value = null)
                }
                TextField(
                    value = createQRUiState.value.textInput,
                    onValueChange = {
                        createQRViewModel.updateTextInput(newValue = it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme
                        .typography
                        .bodyLarge
                        .notScale(),
                    label = {
                        Text(
                            text = "Input text",
                            style = MaterialTheme
                                .typography
                                .bodySmall
                                .notScale()
                        )
                    },
                    trailingIcon = {
                        if (createQRUiState.value.textInput.isNotEmpty())
                            IconButton(
                                onClick = { createQRViewModel.updateTextInput(newValue = "") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = ""
                                )
                            }
                    },
                    singleLine = true,
                )

                @Composable
                fun CTAContainer() {
                    Column(
                        modifier = Modifier.wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        createQRUiState.value.qrCodeResult?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Generated QR Code",
                                modifier = Modifier
                                    .width(width = 256.dp)
                                    .height(height = 256.dp)
                            )

                            Button(onClick = { createQRViewModel.saveQRCode() }) {
                                Text(
                                    text = "Save"
                                )
                            }
                        }
                    }
                }
                CTAContainer()
            }
        }
        ContextWrapper()
    }
}