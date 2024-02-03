package com.alchemy;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.tree.IElementType;

public interface AlchemyPsiCompositeElement extends NavigatablePsiElement {
    IElementType getTokenType();
}