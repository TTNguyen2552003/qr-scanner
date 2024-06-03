package app.kotlin.qrscanner.data

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import app.kotlin.qrscanner.KEY_TEXT_INPUT
import app.kotlin.qrscanner.workers.SaveQRCodeWorker

interface BackgroundWorkQRScannerRepository {
    fun saveQRCode(textInput: String)
}

class WorkManagerQRScannerRepository(context: Context) : BackgroundWorkQRScannerRepository {

    private val workManager: WorkManager = WorkManager.getInstance(context)
    override fun saveQRCode(textInput: String) {
        val dataBuilder = Data.Builder()
        dataBuilder.putString(KEY_TEXT_INPUT, textInput)

        val saveQrCodeRequestBuilder: OneTimeWorkRequest.Builder =
            OneTimeWorkRequestBuilder<SaveQRCodeWorker>()
        saveQrCodeRequestBuilder.setInputData(dataBuilder.build())

        workManager.enqueue(saveQrCodeRequestBuilder.build())
    }
}