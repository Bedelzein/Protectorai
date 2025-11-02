package kz.protectorai

@Deprecated("Temporary solution to fix later")
object CommonHardcode {
    inline fun <T> wildcard(block: () -> T) = block()

    inline operator fun <T> invoke(block: () -> T) = wildcard(block)
}