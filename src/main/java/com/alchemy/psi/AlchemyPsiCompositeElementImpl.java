package com.alchemy.psi;

import com.alchemy.AlchemyPsiCompositeElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class AlchemyPsiCompositeElementImpl extends ASTWrapperPsiElement implements AlchemyPsiCompositeElement {

    public AlchemyPsiCompositeElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public IElementType getTokenType() {
        return getNode().getElementType();
    }
}
