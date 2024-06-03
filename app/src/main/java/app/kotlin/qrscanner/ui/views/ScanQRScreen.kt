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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.notScale

@Preview
@Composable
fun ScanQRScreen() {
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

                        Button(onClick = { /*TODO*/ }) {
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

                var textFieldValue: String by remember {
                    mutableStateOf(value = "")
                }
                TextField(
                    value = textFieldValue,
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

                        var enableOption1: Boolean by remember {
                            mutableStateOf(value = false)
                        }
                        Option(
                            title = "Automatically copy to the clipboard",
                            enable = enableOption1,
                            onChangeSwitch = { enableOption1 = !enableOption1 }
                        )

                        var enableOption2: Boolean by remember {
                            mutableStateOf(value = false)
                        }
                        Option(
                            title = "Automatically open the web link",
                            enable = enableOption2,
                            onChangeSwitch = { enableOption2 = !enableOption2 }
                        )
                    }
                }
                Options()
            }
        }
        ContextWrapper()
    }
}