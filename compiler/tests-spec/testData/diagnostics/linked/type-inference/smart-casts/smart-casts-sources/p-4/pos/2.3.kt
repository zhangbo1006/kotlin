// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 3
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    if (x is Int == true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any? & kotlin.Int")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
    }
}

/*
 * TESTCASE NUMBER: 2
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_2(x: Any) {
    if (<!DEPRECATED_IDENTITY_EQUALS!>x is Int === true<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inv<!>()
    }
}

// TESTCASE NUMBER: 3
fun case_3(x: Any?) {
    if (x !is Class == true == true == true == true == true) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class & kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Class"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
    }
}

// TESTCASE NUMBER: 4
fun case_4(x: Any) {
    if (x !is EnumClass != false) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("EnumClass & kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & EnumClass"), DEBUG_INFO_SMARTCAST!>x<!>.fun_1()
    }
}

// TESTCASE NUMBER: 5
fun case_5(x: Any?) {
    if (!(x !is Class.NestedClass?) != false == true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class.NestedClass? & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Class.NestedClass?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_4
    }
}

// TESTCASE NUMBER: 6
fun case_6(x: Any?) {
    if (!(x !is Object) != false != false != false) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Object & kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Object"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
    }
}

/*
 * TESTCASE NUMBER: 7
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_7(x: Any) {
    if (<!DEPRECATED_IDENTITY_EQUALS!>!(x is DeepObject.A.B.C.D.E.F.G.J) !== false<!>) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>.<!UNRESOLVED_REFERENCE!>prop_1<!>
    }
}

/*
 * TESTCASE NUMBER: 8
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_8(x: Any?) {
    if (<!DEPRECATED_IDENTITY_EQUALS!><!DEPRECATED_IDENTITY_EQUALS!><!DEPRECATED_IDENTITY_EQUALS!>!(x is Int?) !== false<!> !== false<!> !== false<!>) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>?.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>inv<!>()
    }
}

/*
 * TESTCASE NUMBER: 9
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_9(x: Any?) {
    if (<!DEPRECATED_IDENTITY_EQUALS!><!DEPRECATED_IDENTITY_EQUALS!>!!(x !is TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>) !== false<!> === true<!>) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>?.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>get<!>(0)
    }
}

/*
 * TESTCASE NUMBER: 10
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_10(x: Any?) {
    if (<!DEPRECATED_IDENTITY_EQUALS!>!!(x !is Interface3) === true<!> && true) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE!>itest<!>()
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE!>itest3<!>()
    }
}

// TESTCASE NUMBER: 11
fun case_11(x: Any?) {
    if (x is SealedMixedChildObject1? != false || false) {
        <!DEBUG_INFO_EXPRESSION_TYPE("SealedMixedChildObject1? & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & SealedMixedChildObject1?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_1
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & SealedMixedChildObject1?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_2
    }
}
