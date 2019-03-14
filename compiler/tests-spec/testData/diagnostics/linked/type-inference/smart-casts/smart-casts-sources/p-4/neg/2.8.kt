// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 8
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Class?) {
    if (x?.fun_4()?.fun_4()?.fun_4()?.fun_4() != null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_4()<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_4()<!>.fun_4()<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_4()<!>.fun_4()<!>.fun_4()<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_4()<!>.fun_4()<!>.fun_4()<!>.fun_4()<!>
    }
}

// TESTCASE NUMBER: 2
fun case_2(x: Class?) {
    if (x?.fun_4()?.prop_8 != null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_4()<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.fun_4()<!>.prop_8<!>
    }
}

// TESTCASE NUMBER: 3
fun case_3(x: Class?) {
    if (x?.prop_8?.fun_4() != null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.prop_8<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?"), SMARTCAST_IMPOSSIBLE!><!DEBUG_INFO_EXPRESSION_TYPE("Class"), DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_EXPRESSION_TYPE("Class & Class?"), DEBUG_INFO_SMARTCAST!>x<!>.prop_8<!>.fun_4()<!>.prop_8
    }
}

// TESTCASE NUMBER: 4
fun case_4(x: Class?) {
    if (x?.prop_8.also { } != null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?")!>x<!><!UNSAFE_CALL!>.<!>prop_8<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?")!><!DEBUG_INFO_EXPRESSION_TYPE("Class?")!>x<!><!UNSAFE_CALL!>.<!>prop_8<!><!UNSAFE_CALL!>.<!>fun_4()<!>
    }
}
