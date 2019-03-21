// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 16
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    while (true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!> ?: return
    }

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?"), UNREACHABLE_CODE!>x<!>
    <!UNREACHABLE_CODE!><!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!><!UNSAFE_CALL!>.<!>equals(10)<!>
}

// TESTCASE NUMBER: 2
fun case_2(x: Any?) {
    while (true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!> ?: return
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>
    }

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), UNREACHABLE_CODE!>x<!>
    <!UNREACHABLE_CODE!><!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.equals(10)<!>
}

// TESTCASE NUMBER: 3
fun case_3(x: Any?) {
    while (true) {
        x ?: return <!USELESS_ELVIS!>?: <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!><!>
    }

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?"), UNREACHABLE_CODE!>x<!>
    <!UNREACHABLE_CODE!><!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!><!UNSAFE_CALL!>.<!>equals(10)<!>
}

// TESTCASE NUMBER: 4
fun case_4(x: Any?) {
    while (true) {
        x ?: break
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.equals(10)
    }

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
}

// TESTCASE NUMBER: 5
fun case_5(x: Any?) {
    while (true) {
        x ?: continue
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.equals(10)
    }

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?"), UNREACHABLE_CODE!>x<!>
}

// TESTCASE NUMBER: 6
fun case_6(x: Any?) {
    do {
        x ?: continue
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.equals(10)
    } while (false)

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
}

// TESTCASE NUMBER: 7
fun case_7(x: Any?) {
    do {
        x ?: break
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.equals(10)
    } while (false)

    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
}
