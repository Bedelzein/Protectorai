package kz.protectorai

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import kz.protectorai.navigation.RootComponent
import kz.protectorai.navigation.RootContent

@OptIn(ExperimentalDecomposeApi::class)
@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    val lifecycle = ApplicationLifecycle()
    val backDispatcher = BackDispatcher()
    val root = RootComponent.Default(
        componentContext = DefaultComponentContext(lifecycle, backHandler = backDispatcher)
    )
    RootContent(modifier = Modifier.fillMaxSize(), root = root)
    /*PredictiveBackGestureOverlay(
        backDispatcher = backDispatcher,
        backIcon = { progress, _ ->
            PredictiveBackGestureIcon(
                imageVector = ProtectoraiIcons.Logout(),
                progress = progress
            )
        }
    ) {

    }*/
}