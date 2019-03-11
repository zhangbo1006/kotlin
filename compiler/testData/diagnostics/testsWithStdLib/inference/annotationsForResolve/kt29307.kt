// !DIAGNOSTICS: -UNUSED_VARIABLE
// !WITH_NEW_INFERENCE
// ISSUE: KT-29307

fun test_1(map: Map<String, String>) {
    val x = <!NI;TYPE_INFERENCE_ONLY_INPUT_TYPES!>map[<!OI;CONSTANT_EXPECTED_TYPE_MISMATCH!>42<!>]<!> // OK
}

class A

fun test_2(map: Map<A, String>) {
    val x = <!NI;TYPE_INFERENCE_ONLY_INPUT_TYPES!>map[<!OI;CONSTANT_EXPECTED_TYPE_MISMATCH!>42<!>]<!>
}

fun test_3(m: Map<*, String>) {
    val x = m[42] // should be ok
}

fun test_4(m: Map<out Number, String>) {
    val x = m.get(42) // should be ok
}
