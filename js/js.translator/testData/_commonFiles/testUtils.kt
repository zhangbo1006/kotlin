package testUtils

fun isLegacyBackend(): Boolean =
    js("(typeof Kotlin != \"undefined\" && Kotlin.kotlin != \"undefined\")").unsafeCast<Boolean>()

