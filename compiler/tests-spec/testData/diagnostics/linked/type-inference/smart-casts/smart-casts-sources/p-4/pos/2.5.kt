// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-draft
 * PLACE: type-inference, smart-casts, smart-casts-sources -> paragraph 4 -> sentence 2
 * NUMBER: 5
 * DESCRIPTION: Smartcasts from nullability condition (value or reference equality) using if expression and simple types.
 * HELPERS: classes, objects, typealiases, enumClasses, interfaces, sealedClasses
 */

// TESTCASE NUMBER: 1
class Case1<T : Number> {
    inline fun <reified T : CharSequence>case_1(x: Any?) {
        if (x is T) {
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any & kotlin.Any?")!>x<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.length
            <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.get(0)
        }
    }
}

// TESTCASE NUMBER: 2
fun <T> case_2() where T: CharSequence, T: Number {
    class Case1<K> where K : T {
        inline fun <reified T : K> case_1(x: Any?) {
            if (x is T) {
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any & kotlin.Any?")!>x<!>
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.toByte()
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.length
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.get(0)
            }
        }
    }
}

// TESTCASE NUMBER: 3
fun <T> case_3(x: Any?) where T: CharSequence, T: Number {
    class Case1<K> where K : T {
        inline fun <reified T : K> case_1() {
            if (x is T) {
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any & kotlin.Any?")!>x<!>
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.toByte()
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.length
                <!DEBUG_INFO_EXPRESSION_TYPE("T & kotlin.Any?"), DEBUG_INFO_SMARTCAST!>x<!>.get(0)
            }
        }
    }
}