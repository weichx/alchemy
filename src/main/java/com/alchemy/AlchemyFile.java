package com.alchemy;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class AlchemyFile extends PsiFileBase {

    public AlchemyFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, AlchemyLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return AlchemyFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Alchemy File";
    }

}