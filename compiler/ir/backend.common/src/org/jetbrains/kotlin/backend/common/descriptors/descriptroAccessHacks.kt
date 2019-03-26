/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.descriptors

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.ir.declarations.DescriptorInIrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrSymbolDeclaration
import org.jetbrains.kotlin.ir.symbols.IrBindableSymbol


@Deprecated("...")
internal val <D : DeclarationDescriptor> IrSymbolDeclaration<IrBindableSymbol<D, *>>.descriptorWithoutAccessCheck: D
    get() = symbol.descriptor

@UseExperimental(DescriptorInIrDeclaration::class)
@Deprecated("...")
internal val IrDeclaration.descriptorWithoutAccessCheck: DeclarationDescriptor
    get() = descriptor
