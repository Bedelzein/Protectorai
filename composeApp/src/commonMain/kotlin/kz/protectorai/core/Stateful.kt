package kz.protectorai.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Stateful<STATE : Any> {

    val stateFlow: StateFlow<STATE>
    val state: STATE

    fun Stateful<STATE>.updateState(transform: STATE.() -> STATE)

    class Default<STATE : Any>(initialState: STATE): Stateful<STATE> {

        override val stateFlow: MutableStateFlow<STATE> = MutableStateFlow(initialState)
        override val state: STATE get() = stateFlow.value

        override fun Stateful<STATE>.updateState(transform: STATE.() -> STATE) {
            this@Default.stateFlow.value = state.transform()
        }
    }
}