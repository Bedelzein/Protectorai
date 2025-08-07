package kz.protectorai.core

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext

const val EMPTY_STRING = ""
val EMPTY_UNIT_LAMBDA: () -> Unit = {}

fun LifecycleOwner.coroutineScope(
    context: CoroutineContext = Dispatchers.Main.getImmediateOrFallback(),
): CoroutineScope = CoroutineScope(context = context).withLifecycle(lifecycle)

/**
 * Automatically cancels this [CoroutineScope] when the specified [lifecycle] is destroyed.
 *
 * @return the same (this) [CoroutineScope].
 */
internal fun CoroutineScope.withLifecycle(lifecycle: Lifecycle): CoroutineScope {
    lifecycle.doOnDestroy(::cancel)

    return this
}

@Volatile
private var isImmediateSupported: Boolean = true

private fun MainCoroutineDispatcher.getImmediateOrFallback(): MainCoroutineDispatcher {
    if (isImmediateSupported) {
        try {
            return immediate
        } catch (_: UnsupportedOperationException) {} catch (_: NotImplementedError) {}

        isImmediateSupported = false
    }

    return this
}