package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyBinaryExpression extends AlchemyPsiElement {

    public AlchemyBinaryExpression(@NotNull ASTNode node) {
        super(node);
    }

}
