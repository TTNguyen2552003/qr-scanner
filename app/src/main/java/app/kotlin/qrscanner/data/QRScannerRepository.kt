package app.kotlin.qrscanner.data

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import app.kotlin.qrscanner.workers.SaveQRWorkers

interface QRScannerRepository {
    fun saveQRCode()
}

class WorkManagerQRScannerRepository(
    context: Context
) : QRScannerRepository {
    private val workManager: WorkManager = WorkManager.getInstance(context)

    override fun saveQRCode() {
        val saveImageBuilder: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SaveQRWorkers>().build()
        workManager.enqueue(saveImageBuilder)
    }
}