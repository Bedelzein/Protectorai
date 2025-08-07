package kz.protectorai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kz.protectorai.core.Stateful

interface Composite<S : Composite.State> : Stateful<S> {

    @Composable
    fun Content(modifier: Modifier = Modifier) {
        val state by stateFlow.collectAsState()
        Content(state)
    }

    @Composable
    fun Content(state: S)

    interface State
}