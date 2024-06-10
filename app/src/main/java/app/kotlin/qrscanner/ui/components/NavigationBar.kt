package app.kotlin.qrscanner.ui.components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.theme.notScale
import app.kotlin.qrscanner.ui.theme.onSurface
import app.kotlin.qrscanner.ui.theme.primary
import app.kotlin.qrscanner.ui.theme.white

data class NavigationBarItemData(
    val index: Int,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
)

@Composable
fun NavigationBarItem(
    selected: Boolean,
    @DrawableRes icon: Int,
    @StringRes label: Int,
    onPress: () -> Unit
) {
    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onPress()
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 4.dp)
        ) {
            val animatedColor: Color by animateColorAsState(
                targetValue = if (selected)
                    white
                else
                    onSurface,
                label = "icon tint and text color",
                animationSpec = tween(
                    durationMillis = 50,
                    delayMillis = 200,
                    easing = EaseOut
                )
            )
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier.size(24.dp),
                tint = animatedColor
            )
            Text(
                text = stringResource(id = label),
                style = MaterialTheme.typography.labelMedium.notScale(),
                color = animatedColor
            )
        }
    }
}

@Composable
fun NavigationBar(
    selectedIndex: Int,
    onPress: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
            )
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(size = 16.dp)
            )
            .height(height = 80.dp)
            .clip(shape = RoundedCornerShape(size = 16.dp))
            .drawBehind {
                drawRoundRect(
                    color = white,
                    cornerRadius = CornerRadius(x = 16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        @Composable
        fun SegmentsContainer() {
            val navigationBarItemsData: List<NavigationBarItemData> = listOf(
                NavigationBarItemData(
                    index = 0,
                    icon = R.drawable.qr_code_create,
                    label = R.string.section_label_create
                ),
                NavigationBarItemData(
                    index = 1,
                    icon = R.drawable.qr_code_scan,
                    label = R.string.section_label_scan
                ),
                NavigationBarItemData(
                    index = 2,
                    icon = R.drawable.qr_code_storage,
                    label = R.string.section_label_storage
                )
            )
            val currentWidth: Int = LocalConfiguration.current.screenWidthDp
            val xPosSelectionScroller: Dp by animateDpAsState(
                /**
                 * 60 is the size of the scroller
                 * 48 is the padding left and to the edge of the screen
                 * */
                targetValue = when (selectedIndex) {
                    0 -> 0.dp
                    1 -> ((currentWidth - (48 + 48)) / 2 - 60 / 2).dp
                    else -> (currentWidth - (60 + 48 + 48)).dp
                },
                label = "x position of the selection scroller",
                animationSpec = tween(
                    durationMillis = 250,
                    delayMillis = 0,
                    easing = EaseOut
                )
            )
            Row(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp
                    )
                    .drawBehind {
                        drawRoundRect(
                            color = primary,
                            cornerRadius = CornerRadius(x = 8.dp.toPx()),
                            size = Size(
                                width = 60.dp.toPx(),
                                height = 60.dp.toPx()
                            ),
                            topLeft = Offset(
                                x = xPosSelectionScroller.toPx(),
                                y = 0f
                            )
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                navigationBarItemsData.forEach {
                    NavigationBarItem(
                        selected = selectedIndex == it.index,
                        icon = it.icon,
                        label = it.label,
                        onPress = { onPress(it.index) }
                    )
                }
            }
        }
        SegmentsContainer()
    }
}