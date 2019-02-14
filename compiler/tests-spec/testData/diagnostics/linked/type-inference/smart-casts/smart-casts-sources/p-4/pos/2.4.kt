// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 4
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    if (x is Int) {
        if (<!USELESS_IS_CHECK!>x !is Int<!>) {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Any? & kotlin.Int")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int"), DEBUG_INFO_SMARTCAST!>x<!>.inv()
        }
    }
}

// TESTCASE NUMBER: 2
fun case_2(x: Any) {
    if (x !is Unit) {
        if (x is Unit) {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Unit")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & kotlin.Unit")!>x<!>.toString()
        }
    }
}

// TESTCASE NUMBER: 3
fun case_3(x: Any?) {
    if (x !is Class) {
        if (x !is Class) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("Class & kotlin.Any & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Class"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
        }
    }
}

// TESTCASE NUMBER: 4
fun case_4(x: Any) {
    if (x !is EnumClass) else {
        if (<!USELESS_IS_CHECK!>x !is EnumClass<!>) {
            <!DEBUG_INFO_EXPRESSION_TYPE("EnumClass & kotlin.Any")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & EnumClass"), DEBUG_INFO_SMARTCAST!>x<!>.fun_1()
        }
    }
}

// TESTCASE NUMBER: 5
fun case_5(x: Any?) {
    if (!(x !is Class.NestedClass?)) {
        if (!!(<!USELESS_IS_CHECK!>x !is Class.NestedClass?<!>)) {
            <!DEBUG_INFO_EXPRESSION_TYPE("Class.NestedClass? & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Class.NestedClass?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_4
        }
    }
}

// TESTCASE NUMBER: 6
fun case_6(x: Any?) {
    if (!(x is Object)) {
        if (!(x !is Object)) {
            <!DEBUG_INFO_EXPRESSION_TYPE("Object & kotlin.Any & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Object"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
        }
    }
}

// TESTCASE NUMBER: 7
fun case_7(x: Any) {
    if (!(x is DeepObject.A.B.C.D.E.F.G.J)) {
        if (!(x is DeepObject.A.B.C.D.E.F.G.J)) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("DeepObject.A.B.C.D.E.F.G.J & kotlin.Any")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any & DeepObject.A.B.C.D.E.F.G.J"), DEBUG_INFO_SMARTCAST!>x<!>.prop_1
        }
    }
}

// TESTCASE NUMBER: 8
fun case_8(x: Any?) {
    if (!!!!(x is Int?)) else {
        if (!(x is Int?)) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & kotlin.Int?"), DEBUG_INFO_SMARTCAST!>x<!>?.inv()
        }
    }
}

// TESTCASE NUMBER: 9
fun case_9(x: Any?) {
    if (!!!(x !is TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>)) else {
        if (!!(x !is TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>)) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableStringIndirect? /* = kotlin.String? */ & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & TypealiasNullableStringIndirect? /* = kotlin.String? */"), DEBUG_INFO_SMARTCAST!>x<!>?.get(0)
        }
    }
}

// TESTCASE NUMBER: 10
fun case_10(x: Any?) {
    if (!!(x is Interface3)) else {
        if (!!(x !is Interface3)) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("Interface3 & kotlin.Any & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Interface3"), DEBUG_INFO_SMARTCAST!>x<!>.itest()
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & Interface3"), DEBUG_INFO_SMARTCAST!>x<!>.itest3()
        }
    }
}

// TESTCASE NUMBER: 11
fun case_11(x: Any?) {
    if (x is SealedMixedChildObject1?) {
        if (<!USELESS_IS_CHECK!>x is SealedMixedChildObject1?<!>) else {
            <!DEBUG_INFO_EXPRESSION_TYPE("SealedMixedChildObject1? & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & SealedMixedChildObject1?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_1
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any? & SealedMixedChildObject1?"), DEBUG_INFO_SMARTCAST!>x<!>?.prop_2
        }
    }
}
