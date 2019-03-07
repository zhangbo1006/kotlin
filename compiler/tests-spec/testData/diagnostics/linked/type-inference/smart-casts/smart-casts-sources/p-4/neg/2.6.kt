// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 6
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    if (x!! is Int) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inv<!>()
    }
}

// TESTCASE NUMBER: 2
fun case_2(x: Any?) {
    (x as Nothing?)!!
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Nothing?"), UNREACHABLE_CODE!>x<!>
    <!UNREACHABLE_CODE!><!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!><!MISSING_DEPENDENCY_CLASS, MISSING_DEPENDENCY_CLASS!>inv<!>()<!>
}

// TESTCASE NUMBER: 3
fun case_3(x: Any?) {
    if (x as Number? is Int) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Number?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Number?")!>x<!>.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inv<!>()
    }
}

// TESTCASE NUMBER: 4
fun case_4(x: Any?) {
    if (x as Class? is Class) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class? & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class? & kotlin.Any?")!>x<!><!UNSAFE_CALL!>.<!>prop_1
    }
}

// TESTCASE NUMBER: 5
fun case_5(x: Any?) {
    if (x as Nothing? is Nothing) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Nothing?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Nothing?")!>x<!><!UNSAFE_CALL!>.<!><!MISSING_DEPENDENCY_CLASS, MISSING_DEPENDENCY_CLASS!>inv<!>()
    }
}

// TESTCASE NUMBER: 6
fun case_6(x: Any?) {
    (x as String?)!!
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.String?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.String?")!>x<!><!UNSAFE_CALL!>.<!>length
}

// TESTCASE NUMBER: 7
fun case_7(x: Any?) {
    if (x as String? != null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.String?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.String?")!>x<!><!UNSAFE_CALL!>.<!>length
    }
}

// TESTCASE NUMBER: 8
fun case_8(x: Any?) {
    if (x as String? == null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.String?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.String?")!>x<!><!UNSAFE_CALL!>.<!>length
    }
}