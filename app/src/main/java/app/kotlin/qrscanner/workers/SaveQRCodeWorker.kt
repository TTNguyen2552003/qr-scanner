package app.kotlin.qrscanner.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.kotlin.qrscanner.CHANNEL_DESCRIPTION
import app.kotlin.qrscanner.CHANNEL_ID
import app.kotlin.qrscanner.CHANNEL_NAME
import app.kotlin.qrscanner.NOTIFICATION_BODY_FAILED
import app.kotlin.qrscanner.NOTIFICATION_BODY_PROCESS
import app.kotlin.qrscanner.NOTIFICATION_BODY_SUCCESS
import app.kotlin.qrscanner.NOTIFICATION_ID_FAILED
import app.kotlin.qrscanner.NOTIFICATION_ID_PROCESS
import app.kotlin.qrscanner.NOTIFICATION_ID_SUCCESS
import app.kotlin.qrscanner.NOTIFICATION_TITLE_FAILED
import app.kotlin.qrscanner.NOTIFICATION_TITLE_PROCESS
import app.kotlin.qrscanner.NOTIFICATION_TITLE_SUCCESS
import app.kotlin.qrscanner.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.time.LocalDateTime

class SaveQRCodeWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
    appContext = appContext, params = params
) {
    override suspend fun doWork(): Result {
        val notificationManager: NotificationManager = applicationContext
            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        return withContext(Dispatchers.IO) {
            try {
                makeNotification(
                    title = NOTIFICATION_TITLE_PROCESS,
                    body = NOTIFICATION_BODY_PROCESS,
                    notificationId = NOTIFICATION_ID_PROCESS,
                    context = applicationContext
                )

                val decodedString: String = inputData.getString("text_input") ?: ""
                val qrCode: Bitmap = generateQRCode(text = decodedString) ?: throw Throwable()
                val title = "qr_code_${LocalDateTime.now()}"
                if (saveBitmapToMediaStore(applicationContext, qrCode, title)) {
                    notificationManager.cancel(NOTIFICATION_ID_PROCESS)

                    makeNotification(
                        title = NOTIFICATION_TITLE_SUCCESS,
                        body = NOTIFICATION_BODY_SUCCESS,
                        notificationId = NOTIFICATION_ID_SUCCESS,
                        context = applicationContext
                    )
                    return@withContext Result.success()
                } else {
                    throw Throwable("Failed to save image")
                }

            } catch (e: Throwable) {
                makeNotification(
                    title = NOTIFICATION_TITLE_FAILED,
                    body = NOTIFICATION_BODY_FAILED,
                    notificationId = NOTIFICATION_ID_FAILED,
                    context = applicationContext
                )

                return@withContext Result.failure()
            }
        }
    }
}

fun makeNotification(title: String, body: String, notificationId: Int, context: Context) {
    val name: CharSequence = CHANNEL_NAME
    val description: String = CHANNEL_DESCRIPTION
    val importance: Int = NotificationManager.IMPORTANCE_HIGH
    val channelId: String = CHANNEL_ID
    val channel = NotificationChannel(
        channelId,
        name,
        importance
    )
    channel.description = description

    val notificationManager: NotificationManager = context
        .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)

    val largeIcon: Bitmap? = BitmapFactory
        .decodeResource(
            context.resources,
            R.drawable.notification_icon
        )

    val intent = Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    val pendingIntent: PendingIntent = PendingIntent
        .getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
        context,
        channelId
    )
        .setSmallIcon(R.drawable.notification_icon)
        .setLargeIcon(largeIcon)
        .setVibrate(LongArray(size = 0))
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setColor(Color.BLACK)

    if (notificationId == NOTIFICATION_ID_SUCCESS)
        notificationBuilder.setContentIntent(pendingIntent)

    NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
}

fun generateQRCode(text: String): Bitmap? {
    if (text == "")
        return null

    val width = 256
    val height = 256

    val bitMatrix: BitMatrix =
        MultiFormatWriter()
            .encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height
            )

    val bitmap: Bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.RGB_565
    )

    for (x: Int in 0 until width) {
        for (y: Int in 0 until height) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            )
        }
    }
    return bitmap
}

fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, title: String): Boolean {
    val contentValues: ContentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, title)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val contentResolver:ContentResolver = context.contentResolver
    val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        var outputStream: OutputStream? = null
        try {
            outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            outputStream?.close()

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
            return true
        } catch (e: Exception) {
            contentResolver.delete(uri, null, null)
            e.printStackTrace()
            return false
        } finally {
            outputStream?.close()
        }
    }
    return false
}

