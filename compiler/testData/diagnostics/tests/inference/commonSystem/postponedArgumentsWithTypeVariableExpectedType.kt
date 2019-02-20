// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_PARAMETER -UNUSED_VARIABLE

interface A
class B : A
class C : A

fun <K> select1(x: K, y: K): K = TODO()

fun <T, K : T> select2(x: T, y: K): K = TODO()
fun <T, K : T> select3(x: T, y: K): T = TODO()

fun <F, K : () -> F> select4(x: K, y: K): K = TODO()

fun test() {
    val l0 = select1({ B() }, { B() })
    val c0 = select1(::C, ::C)

    val l1 = select1({ B() }, { C() })
    val c1 = select1(::B, ::C)

    val lc = select1(::B, { C() })

    val e0: Int.() -> A = select1({ B() }, { B() })
    val e1: (Int) -> A = select1({ B() }, { C() })

    val l2 = select2({ B() }, { C() })
    val l3 = select3({ B() }, { C() })

    val c2 = select2(::B, ::C)
    val c3 = select3(::B, ::C)

    val l4 = select4({ B() }, { C() })
    val c4 = select4(::B, ::C)
}