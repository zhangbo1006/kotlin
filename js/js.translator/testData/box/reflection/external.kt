// EXPECTED_REACHABLE_NODES: 1221
external class A1

external object O1

fun box(): String {
    assertEquals(null, A1::class.simpleName, "simpleName of external class must be null")
    assertEquals(js("A1"), A1::class.js, "Can't get reference to external class")

    assertEquals(null, O1::class.simpleName, "simpleName of external object must be null")
    assertEquals(js("O1"), O1::class.js, "Can't get reference to external object via instance")

    return "OK"
}