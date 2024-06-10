package app.kotlin.qrscanner.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.disableSwitchContainerColor
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.theme.primary
import app.kotlin.qrscanner.ui.theme.white

@Composable
fun Switch(
    state: Boolean,
    onStateChange: () -> Unit
) {
    val xPosition: Dp by animateDpAsState(
        targetValue = if (state)
            28.dp
        else
            4.dp,
        label = "X Axis position of thumb",
        animationSpec = tween(
            durationMillis = 250,
            easing = EaseOut
        )
    )

    val containerColor: Color by animateColorAsState(
        targetValue = if (state)
            primary
        else
            disableSwitchContainerColor,
        tween(
            durationMillis = 250,
            easing = EaseOut
        ),
        label = "switch container color"
    )
    Box(
        modifier = Modifier
            .width(width = 56.dp)
            .height(height = 32.dp)
            .clip(shape = RoundedCornerShape(size = 16.dp))
            .drawBehind {
                drawRoundRect(
                    color = containerColor,
                    cornerRadius = CornerRadius(x = 16.dp.toPx())
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onStateChange()
                    }
                )
            }
            .padding(
                start = xPosition,
                top = 4.dp
            )
    ) {
        Box(
            modifier = Modifier
                .size(size = 24.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = CircleShape
                )
                .clip(shape = CircleShape)
                .drawBehind {
                    drawCircle(
                        color = white,
                        radius = 12.dp.toPx()
                    )
                }
        )
    }
}

@Composable
fun Option(
    @StringRes optionDescription: Int,
    state: Boolean,
    onStateChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(size = 8.dp),
            )
            .drawBehind {
                drawRoundRect(
                    color = white,
                    cornerRadius = CornerRadius(x = 8.dp.toPx())
                )
            }
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = optionDescription),
            style = MaterialTheme
                .typography
                .bodySmall
                .notScale(),
            color = onSurface
        )

        Switch(
            state = state,
            onStateChange = onStateChange
        )
    }
}

@Composable
fun OptionsContext(
    firstOptionState: Boolean,
    onFirstStateChange: () -> Unit,
    secondOptionState: Boolean,
    onSecondStateChange: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .wrapContentHeight()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .drawBehind {
                drawRoundRect(
                    color = white,
                    cornerRadius = CornerRadius(x = 16.dp.toPx())
                )
            }
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 20.dp
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(space = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.options_context_title),
            style = MaterialTheme
                .typography
                .titleLarge
                .notScale(),
            color = onSurface
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {
            Option(
                optionDescription = R.string.option_description_auto_copy,
                state = firstOptionState,
                onStateChange = onFirstStateChange
            )

            Option(
                optionDescription = R.string.option_description_auto_open_web_link,
                state = secondOptionState,
                onStateChange = onSecondStateChange
            )
        }
    }
}