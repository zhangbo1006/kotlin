// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 13
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, functions, enumClasses, interfaces, sealedClasses
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-30507
 */

// TESTCASE NUMBER: 1
fun case_1(x: Class?) {
    x!!
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>[if (true) {<!VAL_REASSIGNMENT!>x<!>=null;0} else 0] += <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>[0]
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>[0].inv()
}

// TESTCASE NUMBER: 2
fun case_2() {
    var x: Class? = <!CONSTANT_EXPECTED_TYPE_MISMATCH!>10<!>
    x!!
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>(if (true) {x=null;0} else 0, <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), TYPE_MISMATCH!>x<!>)
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_1()
}

// TESTCASE NUMBER: 3
fun case_3() {
    var x: Class? = Class()
    x!!
    val <!UNUSED_VARIABLE!>y<!> = <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>[if (true) {x=null;0} else 0, <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>[0]]
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!>x<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_1()
}