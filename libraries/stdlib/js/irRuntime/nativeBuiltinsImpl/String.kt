/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NON_ABSTRACT_FUNCTION_WITH_NO_BODY")
package kotlin

/**
 * The `String` class represents character strings. All string literals in Kotlin programs, such as `"abc"`, are
 * implemented as instances of this class.
 */
@Suppress("EXTERNAL_TYPE_EXTENDS_NON_EXTERNAL_TYPE")
public class String : Comparable<String>, CharSequence {
    companion object {}
    
    /**
     * Returns a string obtained by concatenating this string with the string representation of the given [other] object.
     */
    public operator fun plus(other: Any?): String

    @Suppress("MUST_BE_INITIALIZED_OR_BE_ABSTRACT")
    public override val length: Int

    public override fun get(index: Int): Char

    public override fun subSequence(startIndex: Int, endIndex: Int): CharSequence

    public override fun compareTo(other: String): Int
}
