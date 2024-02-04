package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyRelationalExpression extends AlchemyPsiElement {

    public AlchemyRelationalExpression(@NotNull ASTNode node) {
        super(node);
    }

}
