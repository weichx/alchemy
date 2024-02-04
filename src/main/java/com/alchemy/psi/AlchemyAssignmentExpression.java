package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyAssignmentExpression extends AlchemyPsiElement {

    public AlchemyAssignmentExpression(@NotNull ASTNode node) {
        super(node);
    }

}
