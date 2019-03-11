// !DIAGNOSTICS: -UNUSED_VARIABLE
// !WITH_NEW_INFERENCE
// ISSUE: KT-29307

fun foo(map: Map<String, String>) {
    val x = <!NI;ABSTRACT_SUPER_CALL!>map[<!OI;CONSTANT_EXPECTED_TYPE_MISMATCH!>42<!>]<!> // OK
}

class A

fun bar(map: Map<A, String>) {
    val x = <!NI;ABSTRACT_SUPER_CALL!>map[<!OI;CONSTANT_EXPECTED_TYPE_MISMATCH!>42<!>]<!>
}