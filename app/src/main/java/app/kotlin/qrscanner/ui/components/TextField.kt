package app.kotlin.qrscanner.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.theme.onSurfaceVariant
import app.kotlin.qrscanner.ui.theme.white

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    clearTextField: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
                vertical = 16.dp,
                horizontal = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier
                .wrapContentHeight()
                .weight(weight = 1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            textStyle = MaterialTheme
                .typography
                .bodyLarge
                .copy(color = onSurface)
                .notScale(),
            singleLine = true,
            cursorBrush = SolidColor(onSurface)
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = "Enter your text",
                    style = MaterialTheme
                        .typography
                        .bodyLarge
                        .notScale(),
                    color = onSurfaceVariant
                )
            }
            innerTextField()
        }

        if (value.isNotEmpty()) {
            Icon(
                painter = painterResource(id = R.drawable.clear_text),
                contentDescription = "clear text input",
                modifier = Modifier
                    .size(size = 24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { clearTextField() }
                        )
                    }
            )
        }
    }
}