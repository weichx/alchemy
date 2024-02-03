package com.alchemy;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AlchemyElementType extends IElementType {

    public AlchemyElementType(@NotNull @NonNls String debugName) {
        super(debugName, AlchemyLanguage.INSTANCE);
    }

}