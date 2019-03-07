// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 5
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    if (x is Int || x !is Int) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inv<!>()
    }
}

// TESTCASE NUMBER: 2
fun case_2(x: Any) {
    if (x is Number || x !is Number || <!USELESS_IS_CHECK!>x is Number<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>.toByte()
    }
}

// TESTCASE NUMBER: 3
fun case_3(x: Any?) {
    if (x is Boolean || <!USELESS_IS_CHECK!>x !is Boolean is Boolean<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE!>prop_1<!>
    }
}

// TESTCASE NUMBER: 4
fun case_4(x: Any) {
    if (x !is EnumClass || <!USELESS_IS_CHECK!>x !is EnumClass<!> || <!USELESS_IS_CHECK!>x !is EnumClass<!> || <!USELESS_IS_CHECK!>x is EnumClass<!>) else {
        if (<!SYNTAX!><!>) {
            <!DEBUG_INFO_EXPRESSION_TYPE("EnumClass & kotlin.Any")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("EnumClass & kotlin.Any"), DEBUG_INFO_SMARTCAST!>x<!>.fun_1()
        }
    }
}

// TESTCASE NUMBER: 5
fun case_5(x: Any?) {
    if (!(x !is Class.NestedClass?) || x is Class.NestedClass? || x !is Class.NestedClass?) {
        if (!!(x !is Class.NestedClass?)) {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE!>prop_4<!>
        }
    }
}

// TESTCASE NUMBER: 6
fun case_6(x: Any?) {
    if (!(x is Object) || !!(<!USELESS_IS_CHECK!>x !is Object<!>)) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE!>prop_1<!>
    }
}

// TESTCASE NUMBER: 7
fun case_7(x: Any) {
    if (!(x is DeepObject.A.B.C.D.E.F.G.J) || !!!!!!(<!USELESS_IS_CHECK!>x is DeepObject.A.B.C.D.E.F.G.J<!>)) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>.<!UNRESOLVED_REFERENCE!>prop_1<!>
    }
}

// TESTCASE NUMBER: 8
fun case_8(x: Any?) {
    if (x is Int? == x is Int?) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>?.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inv<!>()
    }
}

// TESTCASE NUMBER: 9
fun case_9(x: Any?) {
    if (!!!(x !is TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>)) else {
        if (!(x !is TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>)) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>?.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>get<!>(0)
        }
    }
}

// TESTCASE NUMBER: 10
fun case_10(x: Any?) {
    if (!!(x is Interface3)) {
        if (!!(x !is Interface3)) {
            <!DEBUG_INFO_EXPRESSION_TYPE("Interface3 & kotlin.Any & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("Interface3 & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.itest()
            <!DEBUG_INFO_EXPRESSION_TYPE("Interface3 & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.itest3()
        }
    }
}

// TESTCASE NUMBER: 11
fun case_11(x: Any?) {
    if (x is SealedMixedChildObject1?) else {
        if (<!USELESS_IS_CHECK!>x is SealedMixedChildObject1?<!>) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("SealedMixedChildObject1? & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("SealedMixedChildObject1? & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_1
            <!DEBUG_INFO_EXPRESSION_TYPE("SealedMixedChildObject1? & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_2
        }
    }
}

// TESTCASE NUMBER: 12
inline fun <reified T, reified K>case_12(x: Any?) {
    if (x !is T) {
        if (<!USELESS_IS_CHECK!>x is T<!> is K) {
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?")!>x<!>
        }
    }
}

// TESTCASE NUMBER: 13
inline fun <reified T, reified K>case_13(x: Any?) {
    if (x !is T) {
        if (x !is K) {
            <!DEBUG_INFO_EXPRESSION_TYPE("K & T & kotlin.Any?")!>x<!>
        }
    }
}

// TESTCASE NUMBER: 14
inline fun <reified T, reified K>case_14(x: Any?) {
    if (x is K) else {
        if (<!USELESS_IS_CHECK!>x !is T<!>) {
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?")!>x<!>
        }
    }
}

// TESTCASE NUMBER: 15
inline fun <reified T, reified K>case_15(x: Any?) {
    if (x !is T) {
        if (x is K) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?")!>x<!>
        }
    }
}
