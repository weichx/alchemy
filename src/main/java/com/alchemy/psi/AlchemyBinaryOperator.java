package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyBinaryOperator extends AlchemyPsiElement {

    public AlchemyBinaryOperator(@NotNull ASTNode node) {
        super(node);
    }

}
