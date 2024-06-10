package app.kotlin.qrscanner.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.theme.onSurfaceVariant
import app.kotlin.qrscanner.ui.theme.white

@Composable
fun ReadOnlyTextField(value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .clip(shape = RoundedCornerShape(size = 8.dp))
            .drawBehind {
                drawRoundRect(
                    color = white,
                    cornerRadius = CornerRadius(x = 8.dp.toPx())
                )
            }
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
    ) {
        BasicTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            textStyle = MaterialTheme
                .typography
                .bodyLarge
                .copy(color = onSurface)
                .notScale(),
            singleLine = true,
            readOnly = true
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.read_only_text_field_place_holder),
                    style = MaterialTheme
                        .typography
                        .bodyLarge
                        .notScale(),
                    color = onSurfaceVariant
                )
            }
            innerTextField()
        }
    }
}