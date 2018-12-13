/*
 * KOTLIN CODEGEN BOX NOT LINKED SPEC TEST (NEGATIVE)
 *
 * SECTIONS: objects, inheritance
 * NUMBER: 9
 * DESCRIPTION: Access to class members in the super constructor call of an object.
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-25289
 */

open class CheckNested(a: Any) {
     companion object : CheckNested(this)
}

fun box(): String? {
    if (CheckNested.Companion == null) return null

    return "OK"
}