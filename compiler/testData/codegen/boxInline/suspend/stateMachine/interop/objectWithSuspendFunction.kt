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
    fun dummy()
}

inline fun inlineMe(crossinline c: () -> Unit) = object : SuspendRunnable {
    override suspend fun run() {
        StateMachineChecker.suspendHere()
        StateMachineChecker.suspendHere()
    }

    override fun dummy() {
        c() // to force object transformation
    }
}

// FILE: A.java

import test.InlineMeKt;
import kotlin.Unit;
import kotlin.jvm.functions.*;
import COROUTINES_PACKAGE.*;

public class A {
    public static Object call(Object c) {
        return InlineMeKt.inlineMe((Function0<Unit>) c);
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
        (A.call({}) as SuspendRunnable).run()
    }
    StateMachineChecker.check(2)
    StateMachineChecker.reset()
    builder {
        inlineMe {}.run()
    }
    StateMachineChecker.check(2)
    return "OK"
}
