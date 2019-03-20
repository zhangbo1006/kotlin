// IGNORE_BACKEND: JVM_IR
// COMMON_COROUTINES_TEST
// WITH_RUNTIME
// WITH_COROUTINES
// NO_CHECK_LAMBDA_INLINING
// CHECK_STATE_MACHINE

// FILE: inlineMe.kt

package test

interface SuspendRunnable {
    suspend fun run()
}

inline fun inlineMe(crossinline c: suspend () -> Unit) = object : SuspendRunnable {
    override suspend fun run() {
        c()
        c()
    }
}

// FILE: A.java

import test.InlineMeKt;
import kotlin.Unit;
import kotlin.jvm.functions.*;
import COROUTINES_PACKAGE.*;

public class A {
    public static Object call(Object c) {
        return InlineMeKt.inlineMe((Function1<? super Continuation<? super Unit>, Object>) c);
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
        (A.call(suspend {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }) as SuspendRunnable).run()
    }
    StateMachineChecker.check(4)
    StateMachineChecker.reset()
    builder {
        inlineMe {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }.run()
    }
    StateMachineChecker.check(4)
    return "OK"
}
