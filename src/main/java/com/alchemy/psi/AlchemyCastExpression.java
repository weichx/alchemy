package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyCastExpression extends AlchemyPsiElement {

    public AlchemyCastExpression(@NotNull ASTNode node) {
        super(node);
    }

}
