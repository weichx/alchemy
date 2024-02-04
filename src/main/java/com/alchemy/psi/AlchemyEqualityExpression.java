package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyEqualityExpression extends AlchemyPsiElement {

    public AlchemyEqualityExpression(@NotNull ASTNode node) {
        super(node);
    }

}
