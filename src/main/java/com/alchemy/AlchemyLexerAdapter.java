package com.alchemy;

import com.intellij.lexer.FlexAdapter;

public class AlchemyLexerAdapter extends FlexAdapter {

    public AlchemyLexerAdapter() {
        super(new AlchemyLexer(null));
    }

}