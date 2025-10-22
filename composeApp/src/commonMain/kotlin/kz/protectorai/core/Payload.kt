package kz.protectorai.core

import kotlin.jvm.JvmInline

sealed interface Payload<out T> {
    @JvmInline
    value class Success<T>(val data: T) : Payload<T>

    @JvmInline
    value class Failure(val message: String) : Payload<Nothing>
}

inline fun <T> Payload<T>.valueOr(alternative: (Payload.Failure) -> T): T = when (this) {
    is Payload.Success -> this.data
    is Payload.Failure -> alternative(this)
}

fun <T> Payload<T>.valueOrNull(): T? = when (this) {
    is Payload.Success -> data
    is Payload.Failure -> null
}