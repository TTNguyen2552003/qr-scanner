package app.kotlin.qrscanner.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import app.kotlin.qrscanner.NOTIFICATION_BODY_READ_FAILED
import app.kotlin.qrscanner.NOTIFICATION_ID_READ_FAILED
import app.kotlin.qrscanner.NOTIFICATION_TITLE_READ_FAILED
import app.kotlin.qrscanner.workers.makeNotification
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ReadImageUiState(
    val pickedImage: Uri? = null,
    val barcodeResult: String = "",
    val autoCopy: Boolean = false,
    val autoOpenWeblink: Boolean = false
)

class ReadImageViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<ReadImageUiState> =
        MutableStateFlow(value = ReadImageUiState())

    val uiState: StateFlow<ReadImageUiState> = _uiState.asStateFlow()

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

    fun updatePickedImage(newUri: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(
                pickedImage = newUri
            )
        }
    }

    fun analyzeImage(context: Context) {
        val currentUri: Uri? = _uiState.value.pickedImage
        val inputImage: InputImage? = currentUri?.let { InputImage.fromFilePath(context, it) }
        val scanner: BarcodeScanner = BarcodeScanning.getClient()

        if (inputImage != null) {
            try {
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            updateQRCodeResult(newValue = "")
                            updateQRCodeResult(newValue = barcodes[0].rawValue ?: "")
                        } else {
                            updateQRCodeResult(newValue = "")
                            makeNotification(
                                title = NOTIFICATION_TITLE_READ_FAILED,
                                body = NOTIFICATION_BODY_READ_FAILED,
                                notificationId = NOTIFICATION_ID_READ_FAILED,
                                context = context
                            )
                        }
                    }
                    .addOnFailureListener {
                        throw Throwable()
                    }
            } catch (throwable: Throwable) {
                updateQRCodeResult(newValue = "")
                makeNotification(
                    title = NOTIFICATION_TITLE_READ_FAILED,
                    body = NOTIFICATION_BODY_READ_FAILED,
                    notificationId = NOTIFICATION_ID_READ_FAILED,
                    context = context
                )
            }
        }
    }
}