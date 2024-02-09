package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyConstantExpression extends AlchemyPsiElement {

    public AlchemyConstantExpression(@NotNull ASTNode node) {
        super(node);
    }

}
