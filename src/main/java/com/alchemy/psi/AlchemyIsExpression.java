package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyIsExpression extends AlchemyPsiElement {

    public AlchemyIsExpression(@NotNull ASTNode node) {
        super(node);
    }

}
