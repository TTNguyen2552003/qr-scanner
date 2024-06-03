package app.kotlin.qrscanner

const val CHANNEL_NAME = "QR code notification"
const val CHANNEL_DESCRIPTION = "show notification when qr code saved"
const val CHANNEL_ID = "QR_CODE_NOTIFICATION"

const val KEY_TEXT_INPUT = "text_input"

const val NOTIFICATION_TITLE_PROCESS = "Processing image"
const val NOTIFICATION_BODY_PROCESS = "This takes a few second"
const val NOTIFICATION_ID_PROCESS = 1

const val NOTIFICATION_TITLE_SUCCESS = "QR code saved"
const val NOTIFICATION_BODY_SUCCESS = "Tap to see"
const val NOTIFICATION_ID_SUCCESS = 2

const val NOTIFICATION_TITLE_FAILED = "Failed to save the qr code \u26A0"
const val NOTIFICATION_BODY_FAILED = ""
const val NOTIFICATION_ID_FAILED = 3

const val MAX_INPUT_TEXT_LENGTH = 100

const val NOTIFICATION_TITLE_FAILED_SCANNING = "Failed to scan the qr code \u26A0"
const val NOTIFICATION_BODY_FAILED_SCANNING = ""
const val NOTIFICATION_ID_FAILED_SCANNING = 4