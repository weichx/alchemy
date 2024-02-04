package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyTernaryExpression extends AlchemyPsiElement {

    public AlchemyTernaryExpression(@NotNull ASTNode node) {
        super(node);
    }

}
