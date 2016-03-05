/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kotlin.jvm.internal;

import kotlin.Function;
import kotlin.jvm.functions.*;
import kotlin.jvm.internal.markers.*;

import java.util.*;

@SuppressWarnings({"unused", "deprecation"})
@Deprecated
@kotlin.Deprecated(message = "This class supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
public class TypeIntrinsics {
    private static <T extends Throwable> T sanitizeStackTrace(T throwable) {
        return Intrinsics.sanitizeStackTrace(throwable, TypeIntrinsics.class.getName());
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static void throwCce(Object argument, String requestedClassName) {
        String argumentClassName = argument == null ? "null" : argument.getClass().getName();
        throwCce(argumentClassName + " cannot be cast to " + requestedClassName);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static void throwCce(String message) {
        throw throwCce(new ClassCastException(message));
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static ClassCastException throwCce(ClassCastException e) {
        throw sanitizeStackTrace(e);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableIterator(Object obj) {
        return obj instanceof Iterator &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableIterator);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Iterator asMutableIterator(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableIterator)) {
            throwCce(obj, "kotlin.collections.MutableIterator");
        }
        return castToIterator(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Iterator asMutableIterator(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableIterator)) {
            throwCce(message);
        }
        return castToIterator(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Iterator castToIterator(Object obj) {
        try {
            return (Iterator) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableListIterator(Object obj) {
        return obj instanceof ListIterator &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableListIterator);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static ListIterator asMutableListIterator(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableListIterator)) {
            throwCce(obj, "kotlin.collections.MutableListIterator");
        }
        return castToListIterator(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static ListIterator asMutableListIterator(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableListIterator)) {
            throwCce(message);
        }
        return castToListIterator(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static ListIterator castToListIterator(Object obj) {
        try {
            return (ListIterator) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableIterable(Object obj) {
        return obj instanceof Iterable &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableIterable);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Iterable asMutableIterable(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableIterable)) {
            throwCce(obj, "kotlin.collections.MutableIterable");
        }
        return castToIterable(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Iterable asMutableIterable(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableIterable)) {
            throwCce(message);
        }
        return castToIterable(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Iterable castToIterable(Object obj) {
        try {
            return (Iterable) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableCollection(Object obj) {
        return obj instanceof Collection &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableCollection);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Collection asMutableCollection(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableCollection)) {
            throwCce(obj, "kotlin.collections.MutableCollection");
        }
        return castToCollection(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Collection asMutableCollection(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableCollection)) {
            throwCce(message);
        }
        return castToCollection(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Collection castToCollection(Object obj) {
        try {
            return (Collection) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableList(Object obj) {
        return obj instanceof List &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableList);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static List asMutableList(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableList)) {
            throwCce(obj, "kotlin.collections.MutableList");
        }
        return castToList(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static List asMutableList(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableList)) {
            throwCce(message);
        }
        return castToList(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static List castToList(Object obj) {
        try {
            return (List) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableSet(Object obj) {
        return obj instanceof Set &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableSet);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Set asMutableSet(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableSet)) {
            throwCce(obj, "kotlin.collections.MutableSet");
        }
        return castToSet(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Set asMutableSet(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableSet)) {
            throwCce(message);
        }
        return castToSet(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Set castToSet(Object obj) {
        try {
            return (Set) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableMap(Object obj) {
        return obj instanceof Map &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableMap);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Map asMutableMap(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableMap)) {
            throwCce(obj, "kotlin.collections.MutableMap");
        }
        return castToMap(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Map asMutableMap(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableMap)) {
            throwCce(message);
        }
        return castToMap(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Map castToMap(Object obj) {
        try {
            return (Map) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isMutableMapEntry(Object obj) {
        return obj instanceof Map.Entry &&
               (!(obj instanceof KMappedMarker) || obj instanceof KMutableMap.Entry);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Map.Entry asMutableMapEntry(Object obj) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableMap.Entry)) {
            throwCce(obj, "kotlin.collections.MutableMap.MutableEntry");
        }
        return castToMapEntry(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Map.Entry asMutableMapEntry(Object obj, String message) {
        if (obj instanceof KMappedMarker && !(obj instanceof KMutableMap.Entry)) {
            throwCce(message);
        }
        return castToMapEntry(obj);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Map.Entry castToMapEntry(Object obj) {
        try {
            return (Map.Entry) obj;
        }
        catch (ClassCastException e) {
            throw throwCce(e);
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static int getFunctionArity(Object obj) {
        if (obj instanceof FunctionImpl) {
            return ((FunctionImpl) obj).getArity();
        }
        else if (obj instanceof Function0) {
            return 0;
        }
        else if (obj instanceof Function1) {
            return 1;
        }
        else if (obj instanceof Function2) {
            return 2;
        }
        else if (obj instanceof Function3) {
            return 3;
        }
        else if (obj instanceof Function4) {
            return 4;
        }
        else if (obj instanceof Function5) {
            return 5;
        }
        else if (obj instanceof Function6) {
            return 6;
        }
        else if (obj instanceof Function7) {
            return 7;
        }
        else if (obj instanceof Function8) {
            return 8;
        }
        else if (obj instanceof Function9) {
            return 9;
        }
        else if (obj instanceof Function10) {
            return 10;
        }
        else if (obj instanceof Function11) {
            return 11;
        }
        else if (obj instanceof Function12) {
            return 12;
        }
        else if (obj instanceof Function13) {
            return 13;
        }
        else if (obj instanceof Function14) {
            return 14;
        }
        else if (obj instanceof Function15) {
            return 15;
        }
        else if (obj instanceof Function16) {
            return 16;
        }
        else if (obj instanceof Function17) {
            return 17;
        }
        else if (obj instanceof Function18) {
            return 18;
        }
        else if (obj instanceof Function19) {
            return 19;
        }
        else if (obj instanceof Function20) {
            return 20;
        }
        else if (obj instanceof Function21) {
            return 21;
        }
        else if (obj instanceof Function22) {
            return 22;
        }
        else {
            return -1;
        }
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static boolean isFunctionOfArity(Object obj, int arity) {
        return obj instanceof Function && getFunctionArity(obj) == arity;
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Object beforeCheckcastToFunctionOfArity(Object obj, int arity) {
        // TODO should we instead inline bytecode for this in TypeIntrinsics.kt?
        if (obj != null && !isFunctionOfArity(obj, arity)) {
            throwCce(obj, "kotlin.jvm.functions.Function" + arity);
        }
        return obj;
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static Object beforeCheckcastToFunctionOfArity(Object obj, int arity, String message) {
        if (obj != null && !isFunctionOfArity(obj, arity)) {
            throwCce(message);
        }
        return obj;
    }
}
