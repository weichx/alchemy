package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyUnaryExpression extends AlchemyPsiElement {

    public AlchemyUnaryExpression(@NotNull ASTNode node) {
        super(node);
    }

}
