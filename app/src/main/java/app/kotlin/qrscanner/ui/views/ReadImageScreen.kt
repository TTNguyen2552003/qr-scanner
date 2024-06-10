package app.kotlin.qrscanner.ui.views

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.components.Button
import app.kotlin.qrscanner.ui.components.OptionsContext
import app.kotlin.qrscanner.ui.components.ReadOnlyTextField
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.viewmodels.ReadImageUiState
import app.kotlin.qrscanner.ui.viewmodels.ReadImageViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadImageScreen(readImageViewModel: ReadImageViewModel = viewModel()) {
    val readImageUiState: State<ReadImageUiState> = readImageViewModel.uiState.collectAsState()
    val context: Context = LocalContext.current

    val accessImagesPermissionState: PermissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 32.dp)
    ) {
        LaunchedEffect(Unit) {
            readImageViewModel.resetResult()
        }

        LaunchedEffect(Unit) {
            if (!accessImagesPermissionState.status.isGranted)
                accessImagesPermissionState.launchPermissionRequest()
        }

        LaunchedEffect(key1 = readImageUiState.value.keyPickingImageEvent) {
            launch {
                if (readImageUiState.value.autoCopy && readImageUiState.value.barcodeResult != "") {
                    val clipBoardManager: ClipboardManager = context
                        .getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

                    val clip: ClipData = ClipData.newPlainText(
                        "qr code with auto copy",
                        readImageUiState.value.barcodeResult
                    )

                    clipBoardManager.setPrimaryClip(clip)

                    Toast.makeText(context, "QR code copied to clip board", Toast.LENGTH_LONG)
                        .show()
                }
            }

            launch {
                if (readImageUiState.value.autoOpenWeblink) {
                    if (URLUtil.isHttpsUrl(readImageUiState.value.barcodeResult)) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(readImageUiState.value.barcodeResult)
                        )
                        context.startActivity(intent)
                    }
                }
            }
        }


        @Composable
        fun CTAContainer() {
            Column(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                Box(
                    modifier = Modifier.size(size = 200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = if (!accessImagesPermissionState.status.isGranted)
                            painterResource(id = R.drawable.access_photo_permission_request_place_holder)
                        else {
                            if (readImageUiState.value.pickedImage == null)
                                painterResource(id = R.drawable.load_photo_place_holder)
                            else
                                rememberAsyncImagePainter(model = readImageUiState.value.pickedImage)
                        },
                        contentDescription = "",
                        modifier = Modifier.size(size = 200.dp),
                        contentScale = ContentScale.FillWidth
                    )

                    if (!accessImagesPermissionState.status.isGranted)
                        Text(
                            text = stringResource(id = R.string.explanation_for_access_photo_permission),
                            style = MaterialTheme
                                .typography
                                .bodySmall
                                .notScale(),
                            color = onSurface,
                            textAlign = TextAlign.Center
                        )
                }

                val pickImageLauncher: ManagedActivityResultLauncher<String, Uri?> =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        readImageViewModel.apply {
                            updatePickedImage(newUri = uri)
                            analyzeImage(context = context)
                        }
                    }

                Button(
                    label = if (
                        ContextCompat.checkSelfPermission(
                            context,
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                Manifest.permission.READ_MEDIA_IMAGES
                            else
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                        R.string.button_label_load
                    else
                        R.string.button_label_allow,
                    onPress = {
                        if (accessImagesPermissionState.status.isGranted) {
                            pickImageLauncher.launch(input = "image/*")
                        } else {
                            val goToAppSetting: () -> Unit = {
                                val intent: Intent =
                                    Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                                intent.setData(uri)
                                context.startActivity(intent)
                            }

                            goToAppSetting()
                        }
                    }
                )
            }
        }
        CTAContainer()

        ReadOnlyTextField(value = readImageUiState.value.barcodeResult)

        OptionsContext(
            firstOptionState = readImageUiState.value.autoCopy,
            onFirstStateChange = { readImageViewModel.updateAutoCopyState() },
            secondOptionState = readImageUiState.value.autoOpenWeblink,
            onSecondStateChange = { readImageViewModel.updateAutoOpenWebState() }
        )
    }
}