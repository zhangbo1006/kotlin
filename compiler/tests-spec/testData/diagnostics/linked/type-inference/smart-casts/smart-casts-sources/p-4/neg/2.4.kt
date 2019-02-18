// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 4
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
class Case1<T : Number> {
    inline fun <reified T : CharSequence>case_1(x: Any?) {
        if (x is T) {
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any & kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>toByte<!>()
        }
    }
}
