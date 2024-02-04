package com.alchemy;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.tree.IElementType;

public interface AlchemyPsiElement extends NavigatablePsiElement {
    IElementType getTokenType();
}