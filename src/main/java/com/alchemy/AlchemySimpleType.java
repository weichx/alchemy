package com.alchemy;

import com.alchemy.psi.AlchemyPsiElement;
import com.intellij.lang.ASTNode;

// this one maybe isn't composite
public class AlchemySimpleType extends AlchemyPsiElement {

    public AlchemySimpleType(ASTNode node) {
        super(node);
    }

}
