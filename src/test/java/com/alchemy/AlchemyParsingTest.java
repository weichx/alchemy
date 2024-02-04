package com.alchemy;

import com.intellij.testFramework.ParsingTestCase;

public class AlchemyParsingTest extends ParsingTestCase {

    public AlchemyParsingTest() {
        super("", AlchemyFile.getDefaultExtension(), new AlchemyParserDefinition());
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }

    private void doTest() {
        doTest(true);
    }

    public void testBlock() {
        doTest();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
}
