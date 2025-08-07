package kz.protectorai.core

fun interface Eventful<T : Eventful.Event> {

    fun onEvent(event: T)

    operator fun invoke(event: T) = onEvent(event)

    interface Event
}