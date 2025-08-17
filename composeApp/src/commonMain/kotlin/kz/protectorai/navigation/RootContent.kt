package kz.protectorai.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import kz.protectorai.navigation.auth.AuthContent

@Composable
fun RootContent(
    root: RootComponent,
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Surface(modifier = modifier.fillMaxSize()) {
            Children(
                modifier = Modifier.fillMaxSize().systemBarsPadding(),
                stack = root.stack,
                animation = stackAnimation(fade())
            ) {
                (_, child) -> when (child) {
                    is RootComponent.Child.Auth -> {
                        val component = child.component
                        val state by component.stateFlow.collectAsState()
                        AuthContent(state, component)
                    }
                    is RootComponent.Child.Feed -> child.component.Content()
                }
            }
        }
    }
}