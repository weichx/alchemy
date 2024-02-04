package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyAsExpression extends AlchemyPsiElement {

    public AlchemyAsExpression(@NotNull ASTNode node) {
        super(node);
    }

}
