package app.kotlin.qrscanner.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.primary
import app.kotlin.qrscanner.ui.theme.white

@Composable
fun Button(
    @StringRes label: Int,
    onPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .drawBehind {
                drawRoundRect(
                    color = primary,
                    cornerRadius = CornerRadius(x = 8.dp.toPx())
                )
            }
            .padding(
                horizontal = 24.dp,
                vertical = 12.dp
            )
    ) {
        Text(
            text = stringResource(id = label),
            style = MaterialTheme
                .typography
                .labelLarge
                .notScale(),
            color = white,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onPress = { onPress() }
                )
            }
        )
    }
}