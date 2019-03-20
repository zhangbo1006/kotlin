// IGNORE_BACKEND: JVM_IR
// COMMON_COROUTINES_TEST
// WITH_RUNTIME
// WITH_COROUTINES
// NO_CHECK_LAMBDA_INLINING
// CHECK_STATE_MACHINE

// FILE: inlineMe.kt

package test

import helpers.*

inline fun inlineMe() = suspend {
    StateMachineChecker.suspendHere()
    StateMachineChecker.suspendHere()
}

// FILE: A.java

import test.InlineMeKt;
import kotlin.Unit;
import kotlin.jvm.functions.*;
import COROUTINES_PACKAGE.*;

public class A {
    public static Object call() {
        return InlineMeKt.inlineMe();
    }
}

// FILE: box.kt

import test.*
import helpers.*
import COROUTINES_PACKAGE.*

fun builder(c: suspend () -> Unit) {
    c.startCoroutine(CheckStateMachineContinuation)
}

fun box(): String {
    builder {
        (A.call() as (suspend () -> Unit))()
    }
    StateMachineChecker.check(2)
    StateMachineChecker.reset()
    builder {
        inlineMe()()
    }
    StateMachineChecker.check(2)
    return "OK"
}
