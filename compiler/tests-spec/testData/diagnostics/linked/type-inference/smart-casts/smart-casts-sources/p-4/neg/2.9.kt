// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 9
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun <T, K> case_1(x: T?, y: K?) {
    x <!UNCHECKED_CAST!>as T<!>
    y <!UNCHECKED_CAST!>as K<!>
    val z = <!DEBUG_INFO_EXPRESSION_TYPE("T & T?")!>x<!> ?: <!DEBUG_INFO_EXPRESSION_TYPE("K & K?")!>y<!>

    <!DEBUG_INFO_EXPRESSION_TYPE("T & T?")!>x<!><!UNSAFE_CALL!>.<!>equals(10)
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>z<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>z<!><!UNSAFE_CALL!>.<!>equals(10)
}

// TESTCASE NUMBER: 1
inline fun <reified T: Any, reified K: T?> case_2(y: K?) {
    y as K

    <!DEBUG_INFO_EXPRESSION_TYPE("K & K?")!>y<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("K & K?")!>y<!><!UNSAFE_CALL!>.<!>equals(10)
}