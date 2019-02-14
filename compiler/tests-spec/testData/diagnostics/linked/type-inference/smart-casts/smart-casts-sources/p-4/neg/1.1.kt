// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (NEGATIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 1
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, properties, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
fun case_1(x: Any?) {
    if (x != null is Boolean) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any?")!>x<!><!UNSAFE_CALL!>.<!>equals(x)
    }
}

// TESTCASE NUMBER: 3
fun case_3() {
    if (<!SENSELESS_COMPARISON!>Object.prop_1 == null !== null<!>)
    else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number?")!>Object.prop_1<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number?")!>Object.prop_1<!><!UNSAFE_CALL!>.<!>equals(Object.prop_1)
    }
}

// TESTCASE NUMBER: 4
fun case_4(x: Char?) {
    if (x != null || <!USELESS_IS_CHECK!>false is Boolean<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Char?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Char?")!>x<!><!UNSAFE_CALL!>.<!>equals(x)
    }
}

// TESTCASE NUMBER: 5
fun case_5() {
    val x: Unit? = null

    if (<!EQUALITY_NOT_APPLICABLE!>x !== <!USELESS_IS_CHECK!>null is Boolean?<!><!>) <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Unit?")!>x<!>
    if (<!SENSELESS_COMPARISON!>x !== null == null<!>) <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Unit?")!>x<!><!UNSAFE_CALL!>.<!>equals(x)
}

// TESTCASE NUMBER: 6
fun case_6(x: EmptyClass?) {
    val y = true

    if (<!USELESS_IS_CHECK!>(x != null && !y) is Boolean<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("EmptyClass?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("EmptyClass?")!>x<!><!UNSAFE_CALL!>.<!>equals(x)
    }
}

// TESTCASE NUMBER: 7
fun case_7() {
    if (nullableNumberProperty != null || <!EQUALITY_NOT_APPLICABLE!><!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("kotlin.Nothing? & kotlin.Number?")!>nullableNumberProperty<!> != null is Boolean<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number?")!>nullableNumberProperty<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number?")!>nullableNumberProperty<!><!UNSAFE_CALL!>.<!>equals(nullableNumberProperty)
    }
}

// TESTCASE NUMBER: 8
fun case_8(x: TypealiasNullableString) {
    if (x !== null === null && <!SENSELESS_COMPARISON!><!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableString /* = kotlin.String */ & TypealiasNullableString /* = kotlin.String? */")!>x<!> != null<!> != null) <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableString /* = kotlin.String */ & TypealiasNullableString /* = kotlin.String? */")!>x<!>
    if (x !== null != null && <!SENSELESS_COMPARISON!><!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableString /* = kotlin.String */ & TypealiasNullableString /* = kotlin.String? */")!>x<!> != null<!> === null) <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableString /* = kotlin.String? */ & TypealiasNullableString /* = kotlin.String */"), DEBUG_INFO_SMARTCAST!>x<!>.equals(x)
}

// TESTCASE NUMBER: 9
fun case_9(x: TypealiasNullableString<!REDUNDANT_NULLABLE!>?<!>) {
    if (<!SENSELESS_COMPARISON!>x === null === null<!>) {

    } else if (<!USELESS_IS_CHECK!>false is Boolean<!>) {
        <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableString? /* = kotlin.String? */")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableString? /* = kotlin.String? */")!>x<!><!UNSAFE_CALL!>.<!>get(0)
    }
}

// TESTCASE NUMBER: 10
fun case_10() {
    val a = Class()

    if (a.prop_4 === null || <!USELESS_IS_CHECK!>true is Boolean<!>) {
        if (<!SENSELESS_COMPARISON!>a.prop_4 != null !== null<!>) {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Float?")!>a.prop_4<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Float?")!>a.prop_4<!><!UNSAFE_CALL!>.<!>equals(a.prop_4)
        }
    }
}

// TESTCASE NUMBER: 11
fun case_11(x: TypealiasNullableStringIndirect<!REDUNDANT_NULLABLE!>?<!>, y: TypealiasNullableStringIndirect) {
    val t: TypealiasNullableStringIndirect = null

    if (x == null is Boolean) {

    } else {
        if (y != null is Boolean == true) {
            if ((nullableStringProperty == null) !is Boolean) {
                if (t != null is Boolean) {
                    <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableStringIndirect /* = kotlin.String */ & TypealiasNullableStringIndirect? /* = kotlin.String? */")!>x<!>
                    <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableStringIndirect? /* = kotlin.String? */ & TypealiasNullableStringIndirect /* = kotlin.String */"), DEBUG_INFO_SMARTCAST!>x<!>.equals(x)
                }
            }
        }
    }
}

// TESTCASE NUMBER: 12
fun case_12(x: TypealiasNullableStringIndirect, y: TypealiasNullableStringIndirect) =
    <!DEBUG_INFO_EXPRESSION_TYPE("{Comparable<{Boolean & String}> & java.io.Serializable}")!>if ((x == null !is Boolean) === false) "1"
    else if ((y === null !== null) is Boolean) <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableStringIndirect /* = kotlin.String */ & TypealiasNullableStringIndirect /* = kotlin.String? */"), DEBUG_INFO_SMARTCAST!>x<!>
    else if (<!SENSELESS_COMPARISON!>y === null != null<!>) <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableStringIndirect /* = kotlin.String? */ & TypealiasNullableStringIndirect /* = kotlin.String */"), DEBUG_INFO_SMARTCAST!>x<!>.equals(x)
    else "-1"<!>

// TESTCASE NUMBER: 13
fun case_13(x: otherpackage.Case13?) =
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Boolean")!>if ((x == null !is Boolean) !== true) {
        throw Exception()
    } else {
        <!DEBUG_INFO_EXPRESSION_TYPE("otherpackage.Case13 & otherpackage.Case13?")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("otherpackage.Case13? & otherpackage.Case13"), DEBUG_INFO_SMARTCAST!>x<!>.equals(x)
    }<!>

// TESTCASE NUMBER: 14
class Case14 {
    val x: otherpackage.Case14<!REDUNDANT_NULLABLE!>?<!>
    init {
        x = otherpackage.Case14()
    }
}

fun case_14() {
    val a = Case14()

    if (a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> <!USELESS_IS_CHECK!>null !is Boolean !is Boolean<!>) {
        if (a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null == true) {
            if (a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> !== null == false) {
                if (<!SENSELESS_COMPARISON!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null == null<!>) {
                    if (<!SENSELESS_COMPARISON!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null !== null<!>) {
                        if (<!DEPRECATED_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null === true<!>) {
                            if (<!DEPRECATED_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> !== null === <!USELESS_IS_CHECK!>true !is Boolean<!><!> == true) {
                                if (<!DEPRECATED_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null !== false<!>) {
                                    if (<!DEPRECATED_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null === false<!>) {
                                        if (<!DEPRECATED_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> !== null === true<!>) {
                                            if (<!USELESS_IS_CHECK!>(a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null != true) !is Boolean<!>) {
                                                if (a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null is Boolean) {
                                                    if (a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> <!USELESS_IS_CHECK!>null is Boolean is Boolean<!>) {
                                                        if (<!IMPLICIT_BOXING_IN_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> !== null is Boolean<!>) {
                                                            if (a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>!=<!> null is Boolean) {
                                                                if ((<!IMPLICIT_BOXING_IN_IDENTITY_EQUALS!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!> !== null !is Boolean<!>) == false) {
                                                                    <!DEBUG_INFO_EXPRESSION_TYPE("[ERROR : otherpackage.Case14]?")!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!><!>
                                                                    <!DEBUG_INFO_EXPRESSION_TYPE("[ERROR : otherpackage.Case14]?")!>a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!><!>.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>equals<!>(a.<!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE!>x<!>)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// TESTCASE NUMBER: 15
fun case_15(x: EmptyObject) {
    val <!UNUSED_VARIABLE!>t<!> = <!DEBUG_INFO_EXPRESSION_TYPE("{Comparable<{Boolean & String}> & java.io.Serializable}")!>if (<!SENSELESS_COMPARISON!>x === null<!> is Boolean is Boolean is Boolean) "" else {
        <!DEBUG_INFO_EXPRESSION_TYPE("EmptyObject")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("EmptyObject")!>x<!>.equals(x)
    }<!>
}

// TESTCASE NUMBER: 16
fun case_16() {
    val x: TypealiasNullableNothing = null

    if (<!SENSELESS_COMPARISON!><!DEBUG_INFO_CONSTANT!>x<!> != null<!> !is Boolean !is Boolean !is Boolean !is Boolean !is Boolean) {
        <!DEBUG_INFO_CONSTANT, DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableNothing /* = kotlin.Nothing? */")!>x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("TypealiasNullableNothing /* = kotlin.Nothing? */")!>x<!>.equals(<!DEBUG_INFO_CONSTANT!>x<!>)
    }
}

// TESTCASE NUMBER: 17
val case_17 = <!DEBUG_INFO_EXPRESSION_TYPE("{Comparable<{Boolean & Byte & Int & Long & Short}> & java.io.Serializable}")!>if (nullableIntProperty == null == true == false) 0 else {
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>nullableIntProperty<!>
    <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Int"), DEBUG_INFO_SMARTCAST!>nullableIntProperty<!>.equals(nullableIntProperty)
}<!>

//TESTCASE NUMBER: 18
fun case_18(a: DeepObject.A.B.C.D.E.F.G.J?) {
    if (a != null !== null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("DeepObject.A.B.C.D.E.F.G.J & DeepObject.A.B.C.D.E.F.G.J?")!>a<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("DeepObject.A.B.C.D.E.F.G.J? & DeepObject.A.B.C.D.E.F.G.J"), DEBUG_INFO_SMARTCAST!>a<!>.equals(a)
    }
}

// TESTCASE NUMBER: 19
fun case_19(b: Boolean) {
    val a = if (b) {
        object {
            val B19 = if (b) {
                object {
                    val C19 = if (b) {
                        object {
                            val D19 = if (b) {
                                object {
                                    val x: Number? = 10
                                }
                            } else null
                        }
                    } else null
                }
            } else null
        }
    } else null

    if (a != null !is Boolean && <!DEBUG_INFO_SMARTCAST!>a<!>.B19 != null is Boolean && <!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!>a<!>.B19<!>.C19 != null is Boolean && <!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!>a<!>.B19<!>.C19<!>.D19 != null == null && <!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!>a<!>.B19<!>.C19<!>.D19<!>.x != null !== null) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number & kotlin.Number?")!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!>a<!>.B19<!>.C19<!>.D19<!>.x<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number"), DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!><!DEBUG_INFO_SMARTCAST!>a<!>.B19<!>.C19<!>.D19<!>.x<!>.equals(null)
    }
}

// TESTCASE NUMBER: 20
fun case_20(b: Boolean) {
    val a = object {
        val B19 = object {
            val C19 = object {
                val D19 =  if (b) {
                    object {}
                } else null
            }
        }
    }

    if (a.B19.C19.D19 !== null !is Boolean) {
        <!DEBUG_INFO_EXPRESSION_TYPE("case_20.<no name provided>.B19.<no name provided>.C19.<no name provided>.D19.<no name provided> & case_20.<no name provided>.B19.<no name provided>.C19.<no name provided>.D19.<no name provided>?")!>a.B19.C19.D19<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("case_20.<no name provided>.B19.<no name provided>.C19.<no name provided>.D19.<no name provided>"), DEBUG_INFO_SMARTCAST!>a.B19.C19.D19<!>.equals(a.B19.C19.D19)
    }
}

// TESTCASE NUMBER: 21
fun case_21() {
    if (EnumClassWithNullableProperty.B.prop_1 !== null is Boolean == true !is Boolean != true) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>EnumClassWithNullableProperty.B.prop_1<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int"), DEBUG_INFO_SMARTCAST!>EnumClassWithNullableProperty.B.prop_1<!>.equals(EnumClassWithNullableProperty.B.prop_1)
    }
}

// TESTCASE NUMBER: 22
fun case_22(a: (() -> Unit)?) {
    if (a != null !is Boolean) {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Unit")!><!DEBUG_INFO_EXPRESSION_TYPE("(() -> kotlin.Unit)? & () -> kotlin.Unit"), DEBUG_INFO_SMARTCAST!>a<!>()<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Unit")!><!DEBUG_INFO_EXPRESSION_TYPE("(() -> kotlin.Unit)? & () -> kotlin.Unit"), DEBUG_INFO_SMARTCAST!>a<!>()<!>.equals(a)
    }
}

// TESTCASE NUMBER: 23
fun case_23(a: ((Float) -> Int?)?, b: Float?) {
    if (a != null !is Boolean && b !== null is Boolean) {
        val x = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!><!DEBUG_INFO_EXPRESSION_TYPE("((kotlin.Float) -> kotlin.Int?)? & (kotlin.Float) -> kotlin.Int?"), DEBUG_INFO_SMARTCAST!>a<!>(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Float & kotlin.Float?"), DEBUG_INFO_SMARTCAST!>b<!>)<!>
        if (x != null) {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Int"), DEBUG_INFO_SMARTCAST!>x<!>.equals(x)
        }
    }
}

// TESTCASE NUMBER: 24
fun case_24(a: ((() -> Unit) -> Unit)?, b: (() -> Unit)?) =
    if (a !== null is Boolean && b !== null !is Boolean) {
        <!DEBUG_INFO_EXPRESSION_TYPE("((() -> kotlin.Unit) -> kotlin.Unit)? & (() -> kotlin.Unit) -> kotlin.Unit"), DEBUG_INFO_SMARTCAST!>a<!>(<!DEBUG_INFO_EXPRESSION_TYPE("(() -> kotlin.Unit)? & () -> kotlin.Unit"), DEBUG_INFO_SMARTCAST!>b<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("((() -> kotlin.Unit) -> kotlin.Unit)? & (() -> kotlin.Unit) -> kotlin.Unit"), DEBUG_INFO_SMARTCAST!>a<!>(<!DEBUG_INFO_SMARTCAST!>b<!>)
        <!DEBUG_INFO_EXPRESSION_TYPE("(() -> kotlin.Unit)? & () -> kotlin.Unit"), DEBUG_INFO_SMARTCAST!>b<!>.equals(null)
    } else null

// TESTCASE NUMBER: 25
fun case_25(b: Boolean) {
    val x = {
        if (b) object {
            val a = 10
        } else null
    }

    val y = if (b) x else null

    if (y !== null === true) {
        val z = <!DEBUG_INFO_EXPRESSION_TYPE("case_25.<anonymous>.<no name provided>?")!><!DEBUG_INFO_EXPRESSION_TYPE("(() -> case_25.<anonymous>.<no name provided>?)? & () -> case_25.<anonymous>.<no name provided>?"), DEBUG_INFO_SMARTCAST!>y<!>()<!>

        if (z != null !== false) {
            <!DEBUG_INFO_EXPRESSION_TYPE("case_25.<anonymous>.<no name provided>? & case_25.<anonymous>.<no name provided>"), DEBUG_INFO_SMARTCAST!>z<!>.a
            <!DEBUG_INFO_EXPRESSION_TYPE("case_25.<anonymous>.<no name provided>? & case_25.<anonymous>.<no name provided>"), DEBUG_INFO_SMARTCAST!>z<!>.a.equals(<!DEBUG_INFO_SMARTCAST!>z<!>.a)
        }
    }
}

// TESTCASE NUMBER: 26
fun case_26(a: ((Float) -> Int?)?, b: Float?) {
    if (a != null == true == false && b != null == true == false) {
        val x = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int?")!><!DEBUG_INFO_EXPRESSION_TYPE("((kotlin.Float) -> kotlin.Int?)? & (kotlin.Float) -> kotlin.Int?"), DEBUG_INFO_SMARTCAST!>a<!>(<!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Float & kotlin.Float?"), DEBUG_INFO_SMARTCAST!>b<!>)<!>
        if (x != null == true === false) {
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int & kotlin.Int?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int? & kotlin.Int"), DEBUG_INFO_SMARTCAST!>x<!>.equals(x)
        }
    }
}

// TESTCASE NUMBER: 27
fun case_27() {
    if (Object.prop_1 == null == true == true == true == true == true == true == true == true == true == true == true == true == true == true is Boolean)
    else {
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number & kotlin.Number?")!>Object.prop_1<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Number"), DEBUG_INFO_SMARTCAST!>Object.prop_1<!>.equals(Object.prop_1)
    }
}