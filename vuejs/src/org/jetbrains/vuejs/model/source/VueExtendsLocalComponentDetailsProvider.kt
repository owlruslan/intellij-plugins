// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.model.source

import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.stubs.JSImplicitElement
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.vuejs.codeInsight.attributes.findProperty
import org.jetbrains.vuejs.index.EXTENDS_PROP
import org.jetbrains.vuejs.index.LOCAL
import org.jetbrains.vuejs.index.VueExtendsBindingIndex
import org.jetbrains.vuejs.index.resolve

class VueExtendsLocalComponentDetailsProvider : VueAdvancedComponentDetailsProvider {
  override fun getIndexedData(descriptor: JSObjectLiteralExpression?, project: Project): Collection<JSImplicitElement> {
    if (descriptor == null) return emptyList()
    val extendsProperty = findProperty(descriptor, EXTENDS_PROP) ?: return emptyList()
    val elements = resolve(LOCAL, GlobalSearchScope.fileScope(extendsProperty.containingFile), VueExtendsBindingIndex.KEY)
                   ?: return emptyList()
    return elements.filter { PsiTreeUtil.isAncestor(extendsProperty, it.parent, false) }
  }

  override fun getDescriptorFinder(): (JSImplicitElement) -> JSObjectLiteralExpression? {
    return { VueComponents.vueMixinDescriptorFinder(it) }
  }
}