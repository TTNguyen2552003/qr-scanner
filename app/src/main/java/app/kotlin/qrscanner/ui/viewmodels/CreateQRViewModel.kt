package app.kotlin.qrscanner.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.kotlin.qrscanner.MAX_INPUT_TEXT_LENGTH
import app.kotlin.qrscanner.QRScannerApplication
import app.kotlin.qrscanner.data.BackgroundWorkQRScannerRepository
import app.kotlin.qrscanner.workers.generateQRCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateQRUiState(
    val textInput: String = "",
    val qrCodeResult: Bitmap? = null
)

class CreateQRViewModel(
    private val backgroundWorkQRScannerRepository: BackgroundWorkQRScannerRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<CreateQRUiState> =
        MutableStateFlow(value = CreateQRUiState())

    val uiState: StateFlow<CreateQRUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null
    private val debounceDelay: Long = 250

    init {
        resetState()
    }

    private fun updateQRCodeResult(textInput: String) {
        val newQRCode: Bitmap? = generateQRCode(text = textInput)
        _uiState.update { currentState ->
            currentState.copy(qrCodeResult = newQRCode)
        }
    }

    fun updateTextInput(newValue: String) {
        _uiState.update { currentState ->
            if (newValue.length <= MAX_INPUT_TEXT_LENGTH)
                currentState.copy(textInput = newValue)
            else
                currentState.copy(
                    textInput = newValue.substring(
                        startIndex = 0,
                        endIndex = MAX_INPUT_TEXT_LENGTH
                    )
                )
        }

        debounceJob?.cancel()

        debounceJob = viewModelScope.launch {
            delay(debounceDelay)
            updateQRCodeResult(textInput = _uiState.value.textInput)
        }
    }

    fun saveQRCode() {
        val currentTextInput: String = _uiState.value.textInput
        resetState()
        backgroundWorkQRScannerRepository.saveQRCode(textInput = currentTextInput)
    }

    private fun resetState() {
        _uiState.value = CreateQRUiState()
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application: QRScannerApplication =
                    (this[APPLICATION_KEY] as QRScannerApplication)
                val backgroundWorkQRScannerRepository: BackgroundWorkQRScannerRepository =
                    application.container.qrScannerRepository
                CreateQRViewModel(
                    backgroundWorkQRScannerRepository = backgroundWorkQRScannerRepository
                )
            }
        }
    }
}