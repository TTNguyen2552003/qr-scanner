package app.kotlin.qrscanner

const val CHANNEL_NAME = "QR code notification"
const val CHANNEL_DESCRIPTION = "show notification when something happen in app"
const val CHANNEL_ID = "QR_CODE_NOTIFICATION"

const val KEY_TEXT_INPUT = "text_input"

const val MAX_INPUT_TEXT_LENGTH = 100

const val NOTIFICATION_TITLE_SAVING_QR_CODE = "Processing image"
const val NOTIFICATION_BODY_SAVING_QR_CODE = "This takes a few second"
const val NOTIFICATION_ID_SAVING_QR_CODE = 1

const val NOTIFICATION_TITLE_SAVE_SUCCESSFULLY = "QR code saved"
const val NOTIFICATION_BODY_SAVE_SUCCESSFULLY = "Tap to see"
const val NOTIFICATION_ID_SAVE_SUCCESSFULLY = 2

const val NOTIFICATION_TITLE_SAVE_FAILED = "Failed to save the qr code \u26A0"
const val NOTIFICATION_BODY_SAVE_FAILED = ""
const val NOTIFICATION_ID_SAVE_FAILED = 3

const val NOTIFICATION_TITLE_SCAN_FAILED = "Failed to scan the qr code \u26A0"
const val NOTIFICATION_BODY_SCAN_FAILED = ""
const val NOTIFICATION_ID_SCAN_FAILED = 4

const val NOTIFICATION_TITLE_READ_FAILED = "Failed to detect the qr code in image \u26A0"
const val NOTIFICATION_BODY_READ_FAILED = ""
const val NOTIFICATION_ID_READ_FAILED = 5