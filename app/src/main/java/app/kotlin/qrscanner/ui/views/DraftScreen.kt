package app.kotlin.qrscanner.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BarCodeScanner(context: Context) {
    private val options = GmsBarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .enableAutoZoom()
        .build()

    private val scanner: GmsBarcodeScanner = GmsBarcodeScanning.getClient(context, options)
    var barcodeResult: MutableStateFlow<String?> = MutableStateFlow(value = null)

    fun scan() {
        try {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    barcodeResult.value = barcode.rawValue
                }
                .addOnFailureListener {
                    throw Throwable()
                }
        } catch (_: Throwable) {
        }
    }
}

@Composable
fun DraftScreen(
    onClickAction: suspend () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val coroutineScope = rememberCoroutineScope()

        Button(onClick = {
            coroutineScope.launch {
                onClickAction()
            }
        }) {
            Text(text = "Click me")
        }
    }
}