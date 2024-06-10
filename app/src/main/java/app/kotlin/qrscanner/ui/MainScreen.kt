package app.kotlin.qrscanner.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.kotlin.qrscanner.ui.components.NavigationBar
import app.kotlin.qrscanner.ui.views.CreateQRScreen
import app.kotlin.qrscanner.ui.views.ReadImageScreen
import app.kotlin.qrscanner.ui.views.ScanQRScreen

@Composable
fun MainScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(insets = WindowInsets.statusBars)
            .padding(
                top = 16.dp,
                bottom = 28.dp
            )
    ) {
        var selectedIndex: Int by remember {
            mutableIntStateOf(value = 1)
        }

        var previousIndex: Int by remember {
            mutableIntStateOf(value = 1)
        }

        val onPressNavigationBarItem: (Int) -> Unit = { index ->
            previousIndex = selectedIndex
            selectedIndex = index
        }

        @Composable
        fun ContextWrapper() {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = selectedIndex,
                    label = "navigation section",
                    transitionSpec = {
                        /***
                         * if (previous selection < current selection) then push right else push left
                         */

                        if (previousIndex < selectedIndex)
                            slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing
                                ),
                                initialOffsetX = { it }
                            ).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing
                                    ),
                                    targetOffsetX = { -it }
                                )
                            )
                        else
                            slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = LinearEasing
                                ),
                                initialOffsetX = { -it }
                            ).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = LinearEasing
                                    ),
                                    targetOffsetX = { it }
                                )
                            )
                    }
                ) { targetState ->
                    when(targetState){
                        0-> CreateQRScreen()
                        1-> ScanQRScreen()
                        else -> ReadImageScreen()
                    }
                }
            }
        }
        ContextWrapper()

        NavigationBar(
            selectedIndex = selectedIndex,
            onPress = onPressNavigationBarItem
        )
    }
}