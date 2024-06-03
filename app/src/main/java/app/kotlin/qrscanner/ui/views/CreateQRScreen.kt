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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.ui.theme.notScale
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun CreateQRScreen(

) {


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
                var textInput: String by remember {
                    mutableStateOf(value = "")
                }

                var bitmap: Bitmap? by remember {
                    mutableStateOf(value = null)
                }
                TextField(
                    value = textInput,
                    onValueChange = {
                        textInput = it
                        bitmap = generateQRCode(text = textInput)
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
                        if (textInput.isNotEmpty())
                            IconButton(
                                onClick = {
                                    textInput = ""
                                }
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
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Generated QR Code",
                                modifier = Modifier
                                    .width(width = 256.dp)
                                    .height(height = 256.dp)
                            )

                            Button(onClick = { }) {
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

fun generateQRCode(text: String): Bitmap? {
    if (text == "")
        return null

    val width = 256
    val height = 256

    val bitMatrix: BitMatrix =
        MultiFormatWriter()
            .encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height
            )

    val bitmap: Bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.RGB_565
    )

    for (x: Int in 0 until width) {
        for (y: Int in 0 until height) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }
    return bitmap
}