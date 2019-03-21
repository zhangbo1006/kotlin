// IGNORE_BACKEND: JVM_IR
// COMMON_COROUTINES_TEST
// WITH_RUNTIME
// WITH_COROUTINES
// NO_CHECK_LAMBDA_INLINING
// CHECK_STATE_MACHINE

// FILE: inlineMe.kt

package test

import helpers.*

interface SuspendRunnable {
    suspend fun run()
}

inline fun inlineMe2(crossinline c: suspend () -> Unit) = inlineMe { c(); c() }

inline fun inlineMe(crossinline c: suspend () -> Unit) = object: SuspendRunnable {
    override suspend fun run() {
        c()
        c()
    }
}

inline fun inlineMe3(crossinline c: suspend () -> Unit) = object: SuspendRunnable {
    override suspend fun run() {
        inlineMe {
            c()
            c()
        }.run()
        inlineMe {
            c()
            c()
        }.run()
    }
}

// FILE: A.java

import test.InlineMeKt;
import kotlin.Unit;
import kotlin.jvm.functions.*;
import COROUTINES_PACKAGE.*;

public class A {
    public static Object call2(Object c) {
        return InlineMeKt.inlineMe2((Function1<? super Continuation<? super Unit>, Object>) c);
    }
    public static Object call3(Object c) {
        return InlineMeKt.inlineMe3((Function1<? super Continuation<? super Unit>, Object>) c);
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
        (A.call2(suspend {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }) as SuspendRunnable).run()
    }
    StateMachineChecker.check(8)
    StateMachineChecker.reset()

    builder {
        (A.call3(suspend {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }) as SuspendRunnable).run()
    }
    StateMachineChecker.check(16)
    StateMachineChecker.reset()

    builder {
        inlineMe2 {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }.run()
    }
    StateMachineChecker.check(8)
    StateMachineChecker.reset()

    builder {
        inlineMe3 {
            StateMachineChecker.suspendHere()
            StateMachineChecker.suspendHere()
        }.run()
    }
    StateMachineChecker.check(16)
    return "OK"
}
