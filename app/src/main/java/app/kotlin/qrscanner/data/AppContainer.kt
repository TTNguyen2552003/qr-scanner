package app.kotlin.qrscanner.data

import android.content.Context

interface AppContainer {
    val qrScannerRepository: QRScannerRepository
}

class DefaultAppContainer(
    context: Context
) : AppContainer {
    override val qrScannerRepository = WorkManagerQRScannerRepository(context = context)
}