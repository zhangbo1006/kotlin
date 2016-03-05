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

import kotlin.reflect.*;

/**
 * This class serves as a facade to the actual reflection implementation. JVM back-end generates calls to static methods of this class
 * on any reflection-using construct.
 */
@SuppressWarnings({"unused", "deprecation"})
@Deprecated
@kotlin.Deprecated(message = "This class supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
public class Reflection {
    private static final ReflectionFactory factory;

    static {
        ReflectionFactory impl;
        try {
            Class<?> implClass = Class.forName("kotlin.reflect.jvm.internal.ReflectionFactoryImpl");
            impl = (ReflectionFactory) implClass.newInstance();
        }
        catch (ClassCastException e) { impl = null; }
        catch (ClassNotFoundException e) { impl = null; }
        catch (InstantiationException e) { impl = null; }
        catch (IllegalAccessException e) { impl = null; }

        factory = impl != null ? impl : new ReflectionFactory();
    }

    /* package */ static final String REFLECTION_NOT_AVAILABLE = " (Kotlin reflection is not available)";

    private static final KClass[] EMPTY_K_CLASS_ARRAY = new KClass[0];

    @Deprecated
    public static KClass createKotlinClass(Class javaClass) {
        return factory.createKotlinClass(javaClass);
    }

    @Deprecated
    public static KClass createKotlinClass(Class javaClass, String internalName) {
        return factory.createKotlinClass(javaClass, internalName);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KDeclarationContainer getOrCreateKotlinPackage(Class javaClass, String moduleName) {
        return factory.getOrCreateKotlinPackage(javaClass, moduleName);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KClass getOrCreateKotlinClass(Class javaClass) {
        return factory.getOrCreateKotlinClass(javaClass);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KClass getOrCreateKotlinClass(Class javaClass, String internalName) {
        return factory.getOrCreateKotlinClass(javaClass, internalName);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KClass[] getOrCreateKotlinClasses(Class[] javaClasses) {
        int size = javaClasses.length;
        if (size == 0) return EMPTY_K_CLASS_ARRAY;
        KClass[] kClasses = new KClass[size];
        for (int i = 0; i < size; i++) {
            kClasses[i] = getOrCreateKotlinClass(javaClasses[i]);
        }
        return kClasses;
    }

    // Functions

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KFunction function(FunctionReference f) {
        return factory.function(f);
    }

    // Properties

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KProperty0 property0(PropertyReference0 p) {
        return factory.property0(p);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KMutableProperty0 mutableProperty0(MutablePropertyReference0 p) {
        return factory.mutableProperty0(p);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KProperty1 property1(PropertyReference1 p) {
        return factory.property1(p);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KMutableProperty1 mutableProperty1(MutablePropertyReference1 p) {
        return factory.mutableProperty1(p);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KProperty2 property2(PropertyReference2 p) {
        return factory.property2(p);
    }

    @Deprecated
    @kotlin.Deprecated(message = "This function supports the compiler infrastructure and is not intended to be used directly from user code.", level = kotlin.DeprecationLevel.HIDDEN)
    public static KMutableProperty2 mutableProperty2(MutablePropertyReference2 p) {
        return factory.mutableProperty2(p);
    }
}
