package com.alchemy.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class AlchemyPsiElement extends ASTWrapperPsiElement implements com.alchemy.AlchemyPsiElement {

    public AlchemyPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getTokenType() {
        return getNode().getElementType();
    }
}
