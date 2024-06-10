package app.kotlin.qrscanner.ui.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.components.Button
import app.kotlin.qrscanner.ui.components.TextField
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.theme.onSurfaceVariant
import app.kotlin.qrscanner.ui.theme.white
import app.kotlin.qrscanner.ui.viewmodels.CreateQRUiState
import app.kotlin.qrscanner.ui.viewmodels.CreateQRViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateQRScreen(
    createQRViewModel: CreateQRViewModel = viewModel(factory = CreateQRViewModel.factory)
) {
    val createQRUiState: State<CreateQRUiState> = createQRViewModel
        .uiState
        .collectAsState()
    val context: Context = LocalContext.current

    var showNotificationRequirementRationaleDialog: Boolean by remember {
        mutableStateOf(value = false)
    }

    val notificationPermissionState: PermissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.POST_NOTIFICATIONS
        else
            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
        onPermissionResult = { isGranted ->
            showNotificationRequirementRationaleDialog = !isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!notificationPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest()
        }
    }

    val goToAppSetting: () -> Unit = {
        val intent: Intent = Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.setData(uri)
        context.startActivity(intent)
    }

    if (showNotificationRequirementRationaleDialog)
        NotificationRequirementRationaleDialog(
            onDismissRequest = { showNotificationRequirementRationaleDialog = false },
            onConfirm = if (!notificationPermissionState.status.isGranted) {
                {
                    goToAppSetting()
                    showNotificationRequirementRationaleDialog = false
                }
            } else {
                { showNotificationRequirementRationaleDialog = false }
            }
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        val onValueChange: (String) -> Unit = { newValue ->
            createQRViewModel.updateTextInput(newValue = newValue)
        }

        Box(modifier = Modifier.align(alignment = Alignment.TopCenter)) {
            TextField(
                value = createQRUiState.value.textInput,
                onValueChange = onValueChange,
                clearTextField = { onValueChange("") }
            )
        }

        if (createQRUiState.value.textInput.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(size = 256.dp)
                    .align(alignment = Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr_code_place_holder_outline),
                    contentDescription = "QR code place holder outline",
                    modifier = Modifier.size(size = 252.dp)
                )
                Text(
                    text = stringResource(id = R.string.qr_code_place_holder_text),
                    style = MaterialTheme
                        .typography
                        .bodyLarge
                        .notScale(),
                    color = onSurface
                )
            }
        } else {
            @Composable
            fun QRResultAndSaveButton() {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(alignment = Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    createQRUiState.value.qrCodeResult?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier.size(size = 256.dp)
                        )
                    }

                    Button(
                        label = R.string.button_label_save,
                        onPress = { createQRViewModel.saveQRCode() }
                    )
                }
            }
            QRResultAndSaveButton()
        }
    }
}

@Composable
fun NotificationRequirementRationaleDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Text(
                text = stringResource(id = R.string.button_label_ok),
                style = MaterialTheme
                    .typography
                    .bodyLarge
                    .notScale(),
                color = onSurface,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onConfirm()
                        }
                    )
                }
            )
        },
        dismissButton = {
            Text(
                text = stringResource(id = R.string.button_label_cancel),
                style = MaterialTheme
                    .typography
                    .bodyLarge
                    .notScale(),
                color = onSurfaceVariant,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onDismissRequest()
                        }
                    )
                }
            )
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.notifications_permission_request_icon),
                contentDescription = "notification permission request icon"
            )
        },
        containerColor = white,
        shape = RoundedCornerShape(size = 16.dp),
        text = {
            Text(
                text = stringResource(id = R.string.explanation_for_notification_permission),
                style = MaterialTheme
                    .typography
                    .bodySmall
                    .notScale(),
                color = onSurface
            )
        }
    )
}