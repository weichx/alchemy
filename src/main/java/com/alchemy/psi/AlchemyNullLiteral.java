package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyNullLiteral extends AlchemyPsiElement {

    public AlchemyNullLiteral(@NotNull ASTNode node) {
        super(node);
    }

}
