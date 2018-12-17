// WITH_JDK
fun test1() = Runnable { }

fun test2(a: () -> Unit) = Runnable(a)