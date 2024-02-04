package com.alchemy.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class AlchemyNumericLiteral extends AlchemyPsiElement {

    public AlchemyNumericLiteral(@NotNull ASTNode node) {
        super(node);
    }

}
