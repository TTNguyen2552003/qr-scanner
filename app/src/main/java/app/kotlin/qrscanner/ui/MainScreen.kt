package app.kotlin.qrscanner.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.kotlin.qrscanner.R
import app.kotlin.qrscanner.ui.views.CreateQRScreen
import app.kotlin.qrscanner.ui.views.ReadImageScreen
import app.kotlin.qrscanner.ui.views.ScanQRScreen


data class BottomAppBarItem(
    val index: Int = 0,
    val label: String = "Label",
    @DrawableRes val icon: Int,
    val destination: String = "Unknown"
)

@Composable
fun MainScreen() {
    val bottomAppBarItems: List<BottomAppBarItem> = listOf(
        BottomAppBarItem(
            index = 0,
            label = "Create",
            icon = R.drawable.qr_code_create,
            destination = "Create QR Screen"
        ),
        BottomAppBarItem(
            index = 1,
            label = "Scan",
            icon = R.drawable.qr_code_scan,
            destination = "Scan QR screen"
        ),
        BottomAppBarItem(
            index = 2,
            label = "Storage",
            icon = R.drawable.storage
        )
    )


    val navController: NavHostController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(insets = WindowInsets.statusBars),
        bottomBar = {

            var selected: Int by remember {
                mutableIntStateOf(value = 1)
            }

            BottomAppBar {
                bottomAppBarItems.forEach { it ->
                    NavigationBarItem(
                        selected = selected == it.index,
                        onClick = {
                            selected = it.index
                            navController.navigate(route = it.destination) {
                                popUpTo(id = 0)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = it.icon),
                                contentDescription = ""
                            )
                        },
                        label = { Text(text = it.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = bottomAppBarItems[1].destination
        ) {
            composable(route = bottomAppBarItems[0].destination) {
                CreateQRScreen()
            }
            composable(route = bottomAppBarItems[1].destination) {
                ScanQRScreen()
            }
            composable(route = bottomAppBarItems[2].destination) {
                ReadImageScreen()
            }
            innerPadding
        }
    }
}