package app.kotlin.qrscanner.ui.viewmodels

import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ScanQRUiState(
    val barcodeResult: String = "",
    val keyScanningEvent: Boolean = false,
    val autoCopy: Boolean = false,
    val autoOpenWeblink: Boolean = false,
    val isScanning: Boolean = false
)

class ScanQRViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ScanQRUiState> =
        MutableStateFlow(value = ScanQRUiState())

    val uiState: StateFlow<ScanQRUiState> = _uiState.asStateFlow()

    fun updateQRCodeResult(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                barcodeResult = newValue
            )
        }
    }

    fun startScanning() {
        _uiState.update { currentState ->
            currentState.copy(isScanning = true)
        }
    }

    fun stopScanning() {
        _uiState.update { currentState ->
            currentState.copy(isScanning = false)
        }
    }

    fun updateAutoCopyState() {
        _uiState.update { currentState ->
            currentState.copy(autoCopy = !currentState.autoCopy)
        }
    }

    fun updateAutoOpenWebState() {
        _uiState.update { currentState ->
            currentState.copy(autoOpenWeblink = !currentState.autoOpenWeblink)
        }
    }

    fun recordScanningEvent() {
        _uiState.update { currentState ->
            currentState.copy(keyScanningEvent = !currentState.keyScanningEvent)
        }
    }

    fun resetResult() {
        updateQRCodeResult(newValue = "")
    }
}

class QRCodeAnalyzer(
    private val onQRCodeDetected: (String) -> Unit,
    private val onScanFailed: () -> Unit,
) : ImageAnalysis.Analyzer {

    private val scannerOptions = BarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()
    private val scanner = BarcodeScanning.getClient(scannerOptions)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            val image: InputImage = InputImage
                .fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty())
                        onQRCodeDetected(barcodes[0].rawValue ?: "")
                }
                .addOnFailureListener {
                    onScanFailed()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}