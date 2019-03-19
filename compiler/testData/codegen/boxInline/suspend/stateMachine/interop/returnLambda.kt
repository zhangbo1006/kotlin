// IGNORE_BACKEND: JVM_IR
// COMMON_COROUTINES_TEST
// WITH_RUNTIME
// WITH_COROUTINES
// NO_CHECK_LAMBDA_INLINING
// CHECK_STATE_MACHINE

// FILE: inlineMe.kt

package test

inline fun inlineMe(crossinline c: suspend () -> Unit) = suspend { c(); c() }

// FILE: A.java

import test.InlineMeKt;
import helpers.StateMachineChecker;
import helpers.EmptyContinuation;
import kotlin.Unit;

public class A {
    public static void call() {
        InlineMeKt.inlineMe((continuation) -> {
            StateMachineChecker.INSTANCE.suspendHere(continuation);
            return Unit.INSTANCE;
        }).invoke(new EmptyContinuation());
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
    A.call()
    StateMachineChecker.check(2)
    StateMachineChecker.reset()
    builder {
        inlineMe {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }()
    }
    StateMachineChecker.check(4)
    return "OK"
}
