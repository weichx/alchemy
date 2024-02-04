package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyNullCoalescingExpression extends AlchemyPsiElement {

    public AlchemyNullCoalescingExpression(@NotNull ASTNode node) {
        super(node);
    }

}
