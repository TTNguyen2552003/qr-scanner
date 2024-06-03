package app.kotlin.qrscanner.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kotlin.qrscanner.NOTIFICATION_BODY_FAILED_SCANNING
import app.kotlin.qrscanner.NOTIFICATION_ID_FAILED_SCANNING
import app.kotlin.qrscanner.NOTIFICATION_TITLE_FAILED_SCANNING
import app.kotlin.qrscanner.QRScannerApplication
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

class ScanQRViewModel(private val context: Context) : ViewModel() {
    private val _uiState: MutableStateFlow<ScanQRUiState> =
        MutableStateFlow(value = ScanQRUiState())

    val uiState: StateFlow<ScanQRUiState> = _uiState.asStateFlow()

    private fun updateOutputText(newValue: String) {
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

    private val options = GmsBarcodeScannerOptions
        .Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()
    private val scanner: GmsBarcodeScanner = GmsBarcodeScanning.getClient(context, options)

    fun scanQRCode() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                updateOutputText(newValue = barcode.rawValue ?: "")
            }
            .addOnFailureListener {
                updateOutputText(newValue = "")
                makeNotification(
                    title = NOTIFICATION_TITLE_FAILED_SCANNING,
                    body = NOTIFICATION_BODY_FAILED_SCANNING,
                    notificationId = NOTIFICATION_ID_FAILED_SCANNING,
                    context = context
                )
            }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application: QRScannerApplication =
                    (this[APPLICATION_KEY] as QRScannerApplication)
                ScanQRViewModel(
                    context = application.applicationContext
                )
            }
        }
    }
}