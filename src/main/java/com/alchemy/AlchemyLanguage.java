package com.alchemy;

import com.intellij.lang.Language;

public class AlchemyLanguage extends Language {

    public static final AlchemyLanguage INSTANCE = new AlchemyLanguage();

    private AlchemyLanguage() {
        super("Alchemy");
    }

}
