// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 2
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    if (x is Int || false) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any? & kotlin.Int")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
    }
}

// TESTCASE NUMBER: 2
fun case_2(x: Any) {
    if (x is Unit || false || false || false || false || false) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Unit")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Unit")!>x<!>.toString()
    }
}

// TESTCASE NUMBER: 3
fun case_3(x: Any?) {
    if (true && x !is Class) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class & kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Class"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
    }
}

/*
 * TESTCASE NUMBER: 4
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_4(x: Any) {
    if (true && true && x !is EnumClass) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>x<!>.<!UNRESOLVED_REFERENCE!>fun_1<!>()
    }
}

// TESTCASE NUMBER: 5
fun case_5(x: Any?) {
    if (false || !(x !is Class.NestedClass?)) {
        <!DEBUG_INFO_EXPRESSION_TYPE("Class.NestedClass? & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Class.NestedClass?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_4
    }
}

/*
 * TESTCASE NUMBER: 6
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_6(x: Any?) {
    if (false || false || !(x !is Object)) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>.<!UNRESOLVED_REFERENCE!>prop_1<!>
    }
}

// TESTCASE NUMBER: 7
fun case_7(x: Any) {
    if (!(x is DeepObject.A.B.C.D.E.F.G.J) && true && true && true) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("DeepObject.A.B.C.D.E.F.G.J & kotlin.Any")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & DeepObject.A.B.C.D.E.F.G.J"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
    }
}

// TESTCASE NUMBER: 8
fun case_8(x: Any?) {
    if (!(x is Int?) && true) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>?.inv()
    }
}

/*
 * TESTCASE NUMBER: 9
 * UNEXPECTED BEHAVIOUR
 * ISSUES: KT-28329
 */
fun case_9(x: Any?) {
    if (true && true && !!(x !is TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>) && true && true && true && true) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>?.<!DEBUG_INFO_UNRESOLVED_WITH_TARGET, UNRESOLVED_REFERENCE_WRONG_RECEIVER!>get<!>(0)
    }
}

// TESTCASE NUMBER: 10
fun case_10(x: Any?) {
    if (true && !!(x !is Interface3) && true) else {
        <!DEBUG_INFO_EXPRESSION_TYPE("Interface3 & kotlin.Any & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Interface3"), DEBUG_INFO_SMARTCAST!>x<!>.itest()
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Interface3"), DEBUG_INFO_SMARTCAST!>x<!>.itest3()
    }
}

// TESTCASE NUMBER: 11
fun case_11(x: Any?) {
    if (false || x is SealedMixedChildObject1? || false) {
        <!DEBUG_INFO_EXPRESSION_TYPE("SealedMixedChildObject1? & kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & SealedMixedChildObject1?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_1
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & SealedMixedChildObject1?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_2
    }
}
