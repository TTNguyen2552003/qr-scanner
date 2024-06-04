package app.kotlin.qrscanner.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import app.kotlin.qrscanner.NOTIFICATION_BODY_SCAN_FAILED
import app.kotlin.qrscanner.NOTIFICATION_ID_SCAN_FAILED
import app.kotlin.qrscanner.NOTIFICATION_TITLE_SCAN_FAILED
import app.kotlin.qrscanner.workers.makeNotification
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ScanQRUiState(
    val barcodeResult: String = "",
    val autoCopy: Boolean = false,
    val autoOpenWeblink: Boolean = false
)

class ScanQRViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ScanQRUiState> =
        MutableStateFlow(value = ScanQRUiState())

    val uiState: StateFlow<ScanQRUiState> = _uiState.asStateFlow()

    private fun updateQRCodeResult(newValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                barcodeResult = newValue
            )
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


    fun scanQRCode(context: Context) {
        val options:GmsBarcodeScannerOptions = GmsBarcodeScannerOptions
            .Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        val scanner: GmsBarcodeScanner = GmsBarcodeScanning.getClient(context, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                updateQRCodeResult(newValue = "")
                updateQRCodeResult(newValue = barcode.rawValue ?: "")
            }
            .addOnFailureListener {
                updateQRCodeResult(newValue = "")
                makeNotification(
                    title = NOTIFICATION_TITLE_SCAN_FAILED,
                    body = NOTIFICATION_BODY_SCAN_FAILED,
                    notificationId = NOTIFICATION_ID_SCAN_FAILED,
                    context = context
                )
            }
    }
}