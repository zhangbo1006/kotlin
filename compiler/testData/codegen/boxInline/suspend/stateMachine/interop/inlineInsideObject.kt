// TODO: Enable when retransformation of INVOKEVIRTUAL is fixed.
// IGNORE_BACKEND: JVM, JVM_IR
// COMMON_COROUTINES_TEST
// WITH_RUNTIME
// WITH_COROUTINES
// NO_CHECK_LAMBDA_INLINING
// CHECK_STATE_MACHINE

// FILE: inlineMe.kt

package test

interface SuspendRunnable {
    suspend fun run()
    suspend fun run2()
}

inline fun inlineMe(crossinline c: suspend () -> Unit, crossinline c2: suspend () -> Unit) =
    object : SuspendRunnable {
        override suspend fun run() {
//            c()
//            c()
        }

        override suspend fun run2() {
//            object : SuspendRunnable {
//                override suspend fun run() {
//                    inlineMe2 {
//                        c2()
//                        c2()
//                    }.run()
//                }
//
//                override suspend fun run2() {
//                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                }
//
//                inline fun inlineMe2(crossinline c: suspend () -> Unit) = object : SuspendRunnable {
//                    override suspend fun run() {
//                        c()
//                        c()
//                    }
//
//                    override suspend fun run2() {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//                }
//            }.run()
        }
    }

// FILE: A.java

import test.InlineMeKt;
import kotlin.Unit;
import kotlin.jvm.functions.*;
import COROUTINES_PACKAGE.*;

public class A {
    public static Object call(Object c, Object c2) {
        return InlineMeKt.inlineMe((Function1<? super Continuation<? super Unit>, Object>) c, (Function1<? super Continuation<? super Unit>, Object>) c2);
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
//    var r = A.call(
//        suspend {
//            StateMachineChecker.suspendHere()
//            StateMachineChecker.suspendHere()
//        },
//        suspend {
//            StateMachineChecker.suspendHere()
//            StateMachineChecker.suspendHere()
//        }
//    ) as SuspendRunnable
//    builder {
//        r.run()
//    }
//    StateMachineChecker.check(4)
//    StateMachineChecker.reset()
//    builder {
//        r.run2()
//    }
//    StateMachineChecker.check(16)
//    StateMachineChecker.reset()
//    r = inlineMe ({
//        StateMachineChecker.suspendHere()
//        StateMachineChecker.suspendHere()
//    }) {
//        StateMachineChecker.suspendHere()
//        StateMachineChecker.suspendHere()
//    }
//    builder {
//        r.run()
//    }
//    StateMachineChecker.check(4)
//    StateMachineChecker.reset()
//    builder {
//        r.run2()
//    }
//    StateMachineChecker.check(16)
    return "OK"
}
