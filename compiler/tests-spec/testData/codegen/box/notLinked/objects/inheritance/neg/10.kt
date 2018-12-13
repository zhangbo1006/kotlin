/*
 * KOTLIN CODEGEN BOX NOT LINKED SPEC TEST (NEGATIVE)
 *
 * SECTIONS: objects, inheritance
 * NUMBER: 10
 * DESCRIPTION: Access to class members in the super constructor call of an object.
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25289
 */

open class CheckNested(a: Any) {
    object A : CheckNested(B)
    object B : CheckNested(A)
}

fun box(): String? {
    if (CheckNested.A == null) return null

    return "OK"
}
