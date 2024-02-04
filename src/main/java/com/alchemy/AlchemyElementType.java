package com.alchemy;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AlchemyElementType extends IElementType {

    public AlchemyElementTypes.Id id;

    public AlchemyElementType(@NotNull @NonNls String debugName, AlchemyElementTypes.Id id) {
        super(debugName, AlchemyLanguage.INSTANCE);
        this.id = id;
    }

}