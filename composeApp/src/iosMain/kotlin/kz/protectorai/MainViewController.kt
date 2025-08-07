package kz.protectorai

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import kz.protectorai.navigation.RootComponent
import kz.protectorai.navigation.RootContent
import kz.protectorai.ui.icons.ProtectoraiIcons

@OptIn(ExperimentalDecomposeApi::class)
@Suppress("unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    val lifecycle = ApplicationLifecycle()
    val backDispatcher = BackDispatcher()
    val root = RootComponent.Default(
        componentContext = DefaultComponentContext(lifecycle, backHandler = backDispatcher)
    )
    PredictiveBackGestureOverlay(
        backDispatcher = backDispatcher,
        backIcon = { progress, _ ->
            PredictiveBackGestureIcon(
                imageVector = ProtectoraiIcons.ArrowBack(),
                progress = progress
            )
        }
    ) {
        RootContent(modifier = Modifier.fillMaxSize(), root = root)
    }
}