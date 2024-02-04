package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyPrimaryExpression extends AlchemyPsiElement {

    public AlchemyPrimaryExpression(@NotNull ASTNode node) {
        super(node);
    }

}
