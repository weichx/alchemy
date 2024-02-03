package com.alchemy;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AlchemyTokenType extends IElementType {

    public AlchemyTokenType(@NotNull @NonNls String debugName) {
        super(debugName, AlchemyLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "AlchemyTokenType." + super.toString();
    }

}