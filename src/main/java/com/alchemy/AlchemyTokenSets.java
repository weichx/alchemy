package com.alchemy;

import com.intellij.psi.tree.TokenSet;
import groovyjarjarantlr.Token;

import static com.alchemy.AlchemyTokenTypes.*;

public interface AlchemyTokenSets {

    TokenSet STRINGS = TokenSet.create(); //RAW_SINGLE_QUOTED_STRING, RAW_TRIPLE_QUOTED_STRING, OPEN_QUOTE, CLOSING_QUOTE, REGULAR_STRING_PART);

//    TokenSet RESERVED_WORDS = TokenSet.create(ASSERT,
//            BREAK,
//            CASE,
//            CATCH,
//            CLASS,
//            CONST,
//            CONTINUE,
//            DEFAULT,
//            DO,
//            ELSE,
//            ENUM,
//            EXTENDS,
//            FALSE,
//            FINAL,
//            FINALLY,
//            FOR,
//            IF,
//            IN,
//            IS,
//            NEW,
//            NULL,
//            RETHROW,
//            RETURN,
//            SUPER,
//            SWITCH,
//            THIS,
//            THROW,
//            TRUE,
//            TRY,
//            VAR,
//            WHILE,
//            WITH,
//            // 'void' is not listed as reserved word in spec but it may only be used as the return type of a function, so may be treated as reserved word
//            VOID);
//
//    TokenSet BUILT_IN_IDENTIFIERS = TokenSet.create(ABSTRACT,
//            AS,
//            BASE,
//            COVARIANT,
//            DEFERRED,
//            EXPORT,
//            EXTENSION,
//            EXTERNAL,
//            FACTORY,
//            GET,
//            IMPLEMENTS,
//            IMPORT,
//            INTERFACE,
//            LIBRARY,
//            MIXIN,
//            OPERATOR,
//            PART,
//            SEALED,
//            SET,
//            STATIC,
//            TYPEDEF,
//            WHEN,
//            // next are not listed in spec, but they seem to have the same sense as BUILT_IN_IDENTIFIERS: somewhere treated as keywords, but can be used as normal identifiers
//            ON,
//            OF,
//            NATIVE,
//            SHOW,
//            HIDE,
//            SYNC,
//            ASYNC,
//            AWAIT,
//            YIELD,
//            LATE,
//            REQUIRED);
//
//    TokenSet OPERATORS = TokenSet.create(
//            MINUS, MINUS_EQ, MINUS_MINUS, PLUS, PLUS_PLUS, PLUS_EQ, DIV, DIV_EQ, MUL, MUL_EQ, INT_DIV, INT_DIV_EQ, REM_EQ, REM, BIN_NOT, NOT,
//            EQ, EQ_EQ, NEQ, GT, GT_EQ, GT_GT_EQ, GT_GT, GT_GT_GT_EQ, GT_GT_GT, LT, LT_EQ, LT_LT, LT_LT_EQ, OR, OR_EQ, OR_OR, OR_OR_EQ, XOR, XOR_EQ,
//            AND, AND_EQ, AND_AND, AND_AND_EQ, LBRACKET, RBRACKET, AS, QUEST_QUEST, QUEST_QUEST_EQ
//    );
//
//    TokenSet ASSIGNMENT_OPERATORS = TokenSet.create(
//            // '=' | '*=' | '/=' | '~/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '>>>=' | '&=' | '&&=' | '^=' | '|=' | '||=' | '??='
//            EQ, MUL_EQ, DIV_EQ, INT_DIV_EQ, REM_EQ, PLUS_EQ, MINUS_EQ, LT_LT_EQ, GT_GT_EQ, GT_GT_GT_EQ, AND_EQ, AND_AND_EQ, XOR_EQ, OR_EQ, OR_OR_EQ,
//            QUEST_QUEST_EQ
//    );
//
//    TokenSet BINARY_EXPRESSIONS = TokenSet.create(
//            IF_NULL_EXPRESSION,
//            LOGIC_OR_EXPRESSION,
//            LOGIC_AND_EXPRESSION,
//            COMPARE_EXPRESSION,
//            SHIFT_EXPRESSION,
//            ADDITIVE_EXPRESSION,
//            MULTIPLICATIVE_EXPRESSION
//    );
//

    TokenSet BUILT_IN_PRIMITIVE_TYPES = TokenSet.create(
            TYPE_INT8,
            TYPE_INT16,
            TYPE_INT32,
            TYPE_INT64,

            TYPE_UINT8,
            TYPE_UINT16,
            TYPE_UINT32,
            TYPE_UINT64,

            TYPE_FLOAT,
            TYPE_FLOAT2,
            TYPE_FLOAT3,
            TYPE_FLOAT4,

            // TYPE_FLOAT4x4,
            // TYPE_FLOAT3x2,

            TYPE_COLOR,
            TYPE_COLOR32,
            TYPE_COLOR64,

            TYPE_CHAR,
            TYPE_CHAR16,
            TYPE_CHAR32,

            TYPE_DOUBLE
    );


    TokenSet BUILT_IN_REFERENCE_TYPES = TokenSet.create(
            TYPE_DYNAMIC,
            TYPE_OBJECT,
            TYPE_STRING
    );

    TokenSet BUILT_IN_TYPES = TokenSet.orSet(TokenSet.orSet(BUILT_IN_PRIMITIVE_TYPES, BUILT_IN_REFERENCE_TYPES, TokenSet.create(TYPE_VOID)));

    TokenSet BINARY_OPERATORS = TokenSet.create(
            // '??
            QUEST_QUEST,
            // '&&' '||'
            AND_AND, OR_OR,
            // '==' '!='
            EQ_EQ, NEQ,
            // '<' '<=' '>' '>='
            LT, LT_EQ, GT, GT_EQ,
            // '&' '|' '^'
            AND, OR, XOR,
            // '<<' '>>' '>>>'
            //  LT_LT, GT_GT, GT_GT_GT, -- todo
            // '+' '-'
            PLUS, MINUS,
            // '*' '/' '%'
            MUL, DIV, PERCENT
    );

    TokenSet LOGIC_OPERATORS = TokenSet.create(
            OR_OR, AND_AND,
            // Strictly speaking, this isn't a logical operator, but should be formatted the same.
            QUEST_QUEST
    );

    TokenSet UNARY_OPERATORS = TokenSet.create(
            // '-' '!' '~' '++' '--'
            MINUS, NOT, BIN_NOT, PLUS_PLUS, MINUS_MINUS
    );

//    TokenSet BITWISE_OPERATORS = TokenSet.create(BITWISE_OPERATOR);

    //    TokenSet FUNCTION_DEFINITION = TokenSet.create(
//            FUNCTION_FORMAL_PARAMETER,
//            FUNCTION_DECLARATION_WITH_BODY,
//            FUNCTION_DECLARATION_WITH_BODY_OR_NATIVE,
//            METHOD_DECLARATION,
//            GETTER_DECLARATION,
//            SETTER_DECLARATION
//    );
//
    TokenSet COMMENTS = TokenSet.create(SINGLE_LINE_COMMENT, MULTI_LINE_COMMENT);

    TokenSet ASSIGNMENT_OPERATORS = TokenSet.create(
            EQ,
            PLUS_EQ,
            MINUS_EQ,
            MUL_EQ,
            DIV_EQ,
            MOD_EQ,
            AND_EQ,
            OR_EQ,
            XOR_EQ
    );

//    TokenSet BLOCKS = TokenSet.create(
//            BLOCK,
//    );
//
//    TokenSet BLOCKS_EXT = TokenSet.create(
//            BLOCK,
//            CLASS_MEMBERS,
//            AlchemyParserDefinition.ALCHEMY_FILE,
//            EMBEDDED_CONTENT
//    );
//
//    TokenSet DECLARATIONS = TokenSet.create(
//            CLASS_DEFINITION,
//            FUNCTION_DECLARATION_WITH_BODY,
//            FUNCTION_DECLARATION_WITH_BODY_OR_NATIVE,
//            GETTER_DECLARATION,
//            SETTER_DECLARATION,
//            VAR_DECLARATION_LIST,
//            FUNCTION_TYPE_ALIAS
//    );

}