package app.kotlin.qrscanner

import android.app.Application
import app.kotlin.qrscanner.data.AppContainer
import app.kotlin.qrscanner.data.DefaultAppContainer

class QRScannerApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(context = applicationContext)
    }
}