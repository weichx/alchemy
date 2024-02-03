package com.alchemy;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public final class AlchemyFileType extends LanguageFileType {

    public static final AlchemyFileType INSTANCE = new AlchemyFileType();

    private AlchemyFileType() {
        super(AlchemyLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Alchemy File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Alchemy language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "Alchemy";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AlchemyIcons.FILE;
    }

}