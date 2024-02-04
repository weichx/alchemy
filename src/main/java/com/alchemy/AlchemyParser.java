package com.alchemy;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.psi.tree.IElementType;
import org.apache.batik.bridge.Mark;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.alchemy.AlchemyTokenTypes.*;
import static com.alchemy.AlchemyElementTypes.*;

public class AlchemyParser implements PsiParser, LightPsiParser {

    public @NotNull ASTNode parse(@NotNull IElementType t, @NotNull PsiBuilder b) {
        parseLight(t, b);
        return b.getTreeBuilt();
    }

    public static class StatefulBuilder extends PsiBuilderAdapter {
        public int braceMatch;
        public final PsiParser parser;

        public StatefulBuilder(PsiBuilder builder, PsiParser parser_) {
            super(builder);
            parser = parser_;
            braceMatch = 0;
        }

    }

    public void parseLight(IElementType t, PsiBuilder b) {
        boolean r;
        b = new StatefulBuilder(b, this);
        Marker m = b.mark(); // enter_section_(b, 0, _COLLAPSE_, null);
        r = parse_root_(t, b);
        if (r) {
            m.done(t);
        } else {
            m.drop();
        }
    }

    protected boolean parse_root_(IElementType t, PsiBuilder b) {
        return parse_root_(t, b, 0);
    }

    private static Marker markAndAdvance(PsiBuilder b) {
        Marker m = b.mark();
        b.advanceLexer();
        return m;
    }

    static boolean tryParseBlock(StatefulBuilder b) {

        var open = b.getTokenType();

        if (open != LBRACE) {
            return false;
        }

        Marker m = markAndAdvance(b);

        boolean finished = false;

        while (!b.eof()) {

            IElementType token = b.getTokenType();

            if (token == LBRACE) {

                if (!tryParseBlock(b)) {
                    // fail somehow, nothing is valid here that isn't a block with {
                }

            } else if (token == RBRACE) {
                b.advanceLexer();
                m.done(BLOCK);
                finished = true;
                break;
            }

            // parse other rules
            Marker statement = b.mark();

            if (parseBlockStatement(b)) {
                statement.done(STATEMENT);
            } else {
                statement.error("Expected statement");
                b.advanceLexer();
            }

        }

        if (!finished) {
            m.error("Expected closed block");
        }

        return true;

    }

    static boolean parseBlockStatement(PsiBuilder b) {

        if (parseLocalVariableDeclaration(b)) {
            return true;
        } else {
            return false;
        }
    }

    static boolean consume(PsiBuilder b, IElementType tokenType) {
        if (b.getTokenType() == tokenType) {
            b.advanceLexer();
            return true;
        }
        return false;
    }


    static boolean parseLocalVariableDeclaration(PsiBuilder b) {
        // local_variable_declaration ::= ('using' | 'ref') local_variable_type local_variable_declarator
        // local_variable_type ::= 'var' | type_path
        // local_variable_declarator ::= identifier ('=' 'ref'? local_variable_initializer)
        // local_variable_initializer ::= expression | array_initializer |  stackalloc_initializer

        Marker m = b.mark();

        if (!consume(b, VAR) && !parseTypePath(b)) {
            m.drop();
            return false;
        }

        if (consume(b, STANDARD_IDENTIFIER)) {

            if (consume(b, EQ)) {

                if (parseExpression(b) != null && !parseArrayInitializer(b) && !parseStackAllocInitializer(b)) {
                    m.error("Expected an expression after variable declaration '='");
                    return true;
                }

            }

            m.done(AlchemyElementTypes.LOCAL_VARIABLE_DECLARATION);
            return true;

        }

        m.drop();
        return false;

    }

    private static boolean parseStackAllocInitializer(PsiBuilder b) {
        // stack_alloc_initializer ::= 'stackalloc' type_path '[' expression ']'
        //                           | 'stackalloc' type_? '[' expression? ']' '{' expression '}'
        return false;
    }

    private static boolean parseArrayInitializer(PsiBuilder b) {
        return false;
    }

    private static boolean parseStatementExpression(PsiBuilder b) {
        // statement_expression ::= expression ';'

        Marker m = parseAdditiveExpression(b);

        if (m == null) {
            return false;
        }

        if (b.getTokenType() == SEMICOLON) {
            m = m.precede();
            b.advanceLexer();
            m.done(AlchemyElementTypes.STATEMENT);
            return true;
        }

        // todo -- recover
        m.drop();
        return false;

    }

    private static Marker parseExpression(PsiBuilder b) {
        // expression ::= assignment | non_assignment_expression | 'ref' non_assignment_expression

        Marker m = b.mark();

        if (parseAssignment(b)) {

        }

        m.drop();
        return null;
    }

    private static boolean parseAssignmentOperator(PsiBuilder b) {
        if (AlchemyTokenSets.ASSIGNMENT_OPERATORS.contains(b.getTokenType())) {
            b.advanceLexer();
            return true;
        }
        return false;
    }

    private static boolean parseNonAssignment(PsiBuilder b) {
        // non_assignment_expression ::= lambda_expression
        //                             | conditional_expression

        return parseLambdaExpression(b) || parseConditionalExpression(b);
    }

    // this is a ternary
    private static boolean parseConditionalExpression(PsiBuilder b) {
        // conditional_expression ::= null_coalescing_expression ('?' throwable_expression ':' throwable_expression)?

        Marker m = parseNullCoalescingExpression(b);

        if (m == null) {
            return false;
        }

        if (b.getTokenType() == QUESTION_MARK) {
            m = m.precede();

            b.advanceLexer();

            if (!parseThrowableExpression(b)) {
                m.error("Expected a throwable expression");
                return true;
            }

            if (!consume(b, COLON)) {
                m.error("Expected a ':'");
                return true;
            }

            if (!parseThrowableExpression(b)) {
                m.error("Expected a throwable expression");
                return true;
            }

            m.done(AlchemyElementTypes.TERNARY_EXPRESSION);

        }

        return true;
    }

    private static Marker parseNullCoalescingExpression(PsiBuilder b) {
        // null_coalescing_expression ::= conditional_or_expression ('??' (null_coalescing_expression | throw_expression))?

        Marker m = parseConditionalOrExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() == QUEST_QUEST) {
            m = m.precede();

            b.advanceLexer();

            Marker nce = parseNullCoalescingExpression(b);

            if (nce != null) {
                return m;
            }

            if (!parseThrowExpression(b)) {
                m.error("Expected an expression or throw expression");
                return m;
            }

            m.done(AlchemyElementTypes.NULL_COALESCING_EXPRESSION);

        }

        return m;
    }

    private static boolean parseThrowExpression(PsiBuilder b) {
        return false;
    }

    private static Marker parseConditionalOrExpression(PsiBuilder b) {
        // conditional_or_expression ::= conditional_and_expression ('||' conditional_and_expression)*

        Marker m = parseConditionalAndExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != OR_OR) {
            return m;
        }

        m = m.precede();

        while (consume(b, OR_OR)) {

            if (parseConditionalAndExpression(b) == null) {
                m.error("Expected an expression");
                return m;
            }

        }

        m.done(AlchemyElementTypes.CONDITIONAL_OR_EXPRESSION);
        return m;
    }

    private static Marker parseConditionalAndExpression(PsiBuilder b) {
        // conditional_and_expression ::= inclusive_or_expression (OP_AND inclusive_or_expression)*

        Marker m = parseInclusiveOrExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != AND_AND) {
            return m;
        }

        m = m.precede();

        while (consume(b, AND_AND)) {

            if (parseInclusiveOrExpression(b) == null) {
                m.error("Expected an expression");
                return m;
            }

        }

        m.done(AlchemyElementTypes.CONDITIONAL_AND_EXPRESSION);
        return m;
    }

    private static Marker parseInclusiveOrExpression(PsiBuilder b) {
        // inclusive_or_expression ::= exclusive_or_expression ('|' exclusive_or_expression)*
        Marker m = parseExclusiveOrExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != OR) {
            return m;
        }

        m = m.precede();

        while (consume(b, OR)) {

            if (parseExclusiveOrExpression(b) == null) {
                m.error("Expected an expression");
                return m;
            }

        }

        m.done(AlchemyElementTypes.BITWISE_OR_EXPRESSION);
        return m;
    }

    private static Marker parseExclusiveOrExpression(PsiBuilder b) {
        // exclusive_or_expression ::= and_expression ('^' and_expression)*

        Marker m = parseAndExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != XOR) {
            return m;
        }

        m = m.precede();

        while (consume(b, XOR)) {

            if (parseAndExpression(b) == null) {
                m.error("Expected an expression");
                return m;
            }

        }

        m.done(AlchemyElementTypes.BITWISE_XOR_EXPRESSION);
        return m;
    }

    private static Marker parseAndExpression(PsiBuilder b) {
        // and_expression ::= equality_expression ('&' equality_expression)*

        Marker m = parseEqualityExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != AND) {
            return m;
        }

        m = m.precede();

        while (consume(b, AND)) {

            if (parseEqualityExpression(b) == null) {
                m.error("Expected an expression");
                return m;
            }

        }

        m.done(AlchemyElementTypes.BITWISE_AND_EXPRESSION);
        return m;
    }

    private static Marker parseEqualityExpression(PsiBuilder b) {
        // equality_expression ::= relational_expression ((OP_EQ | OP_NE) relational_expression)*
        Marker m = parseRelationalExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != EQ && b.getTokenType() != NEQ) {
            return m;
        }

        m = m.precede();

        while (consume(b, EQ) || consume(b, NEQ)) {

            if (parseRelationalExpression(b) == null) {
                m.error("Expected an expression");
                return m;
            }

        }

        m.done(AlchemyElementTypes.EQUALITY_EXPRESSION);
        return m;
    }

    private static Marker parseRelationalExpression(PsiBuilder b) {
        // relational_expression ::= shift_expression ( (relational_operator shift_expression) | type_operation )*
        // relational_operator ::= ('<' | '>' | '<=' | '>=')
        // type_operation ::= 'is' is_type | 'as' type_path
        // is_type ::= base_type rank_specifier? '?'? identifier?

        Marker m = parseShiftExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() != LT &&
                b.getTokenType() != GT &&
                b.getTokenType() != LT_EQ &&
                b.getTokenType() != GT_EQ &&
                b.getTokenType() != IS &&
                b.getTokenType() != AS
        ) {
            return m;
        }

        m = m.precede();

        while (!b.eof()) {

            if (consume(b, LT) || consume(b, GT) || consume(b, LT_EQ) || consume(b, GT_EQ)) {

                // todo -- maybe this is a node type

                if (parseShiftExpression(b) == null) {
                    m.error("Expected an expression");
                    return m;
                }

            } else if (b.getTokenType() == IS) {

                Marker isType = b.mark();

                b.advanceLexer();

                // is_type
                if (!parseBaseType(b)) {
                    // todo -- recover
                    isType.drop();
                    m.error("Expected a type name");
                    return m;
                }

                // optional
                parseArraySpecifier(b);

                // optional
                consume(b, QUESTION_MARK);

                // optional
                consume(b, STANDARD_IDENTIFIER);

                isType.done(AlchemyElementTypes.IS_EXPRESSION_TYPE);

            } else if (b.getTokenType() == AS) {

                Marker asMarker = b.mark();

                b.advanceLexer();

                if (!parseTypePath(b)) {
                    asMarker.error("expected a type name");
                    m.error("Expected a type name");
                    return m;
                }

                asMarker.done(AlchemyElementTypes.AS_EXPRESSION);
            } else {
                m.error("Expected a type name");
                return m;
            }

        }

        m.done(AlchemyElementTypes.RELATIONAL_EXPRESSION);
        return m;
    }

    private static boolean sequenceWithoutSpace(PsiBuilder b, IElementType t0, IElementType t1) {
        IElementType tType0 = b.getTokenType();
        IElementType tType1 = b.rawLookup(1);
        return t0 == tType0 && t1 == tType1;
    }

    private static Marker parseShiftExpressionRHS(PsiBuilder b, Marker lhs) {
        // shift_expression_rhs ::= (('<' '<' | '>' '>') additive_expression)* -- no whitespace

        Marker retn = lhs;

        while(sequenceWithoutSpace(b, LT, LT) || sequenceWithoutSpace(b, GT, GT)) {

            Marker op = b.mark();
            b.advanceLexer();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseAdditiveExpression(b);

            if(rhs == null) {
                return null;// nope
            }

            Marker m = lhs.precede();
            // should have 'lhs', 'op', 'rhs' now

            m.done(AlchemyElementTypes.BINARY_EXPRESSION);

            lhs = m;
            retn = m;

        }

        return retn;
    }

    private static Marker parseShiftExpression(PsiBuilder b) {
        // shift_expression ::= additive_expression (('<<' | right_shift) additive_expression)*
        Marker m = parseAdditiveExpression(b);

        if (m == null) {
            return null;
        }

        return parseShiftExpressionRHS(b, m);

    }

    private static Marker parseAdditiveExpressionRHS(PsiBuilder b, Marker lhs) {
        // additive_expression_rhs ::= (('+' | '-')  multiplicative_expression)*

        Marker retn = lhs;

        while(b.getTokenType() == PLUS || b.getTokenType() == MINUS) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseMultiplicativeExpression(b);

            if(rhs == null) {
                return null;// nope
            }

            Marker m = lhs.precede();
            // should have 'lhs', 'op', 'rhs' now

            m.done(AlchemyElementTypes.BINARY_EXPRESSION);

            lhs = m;
            retn = m;

        }

        return retn;
    }

    private static Marker parseAdditiveExpression(PsiBuilder b) {
        // additive_expression ::= multiplicative_expression additive_expression_rhs
        // parse the right hand side, which might be empty, might be chained
        // x + y + z -> parses as ((x + y) + z)

        Marker m = parseMultiplicativeExpression(b);

        if (m == null) {
            return null;
        }

        return parseAdditiveExpressionRHS(b, m);
    }

    private static boolean parseMultiplicativeOperator(PsiBuilder b) {
        // multiplicative_operator ::= '*' | '/' | '%'
        IElementType token = b.getTokenType();
        if (token == MUL || token == DIV || token == PERCENT) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.BINARY_OPERATOR);
            return true;
        }
        return false;
    }

    private static Marker parseSwitchExpression(PsiBuilder b) {
        // switch_expression ::= range_expression ('switch' '{' (switch_expression_arms ','?)? '}')?
        return parseUnaryExpression(b);
    }

    private static Marker parseRangeExpression(PsiBuilder b) {
        // range_expression ::= unary_expression
        //                    | unary_expression? OP_RANGE unary_expression? -- not supported right now
        return parseUnaryExpression(b);
    }

    private static Marker parseMultiplicativeExpressionRHS(PsiBuilder b, Marker lhs) {
        // multiplicative_expression_rhs ::= (('*' | '/' | '%')  switch_expression)*

        Marker retn = lhs;

        while(b.getTokenType() == MUL || b.getTokenType() == DIV || b.getTokenType() == PERCENT) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseSwitchExpression(b);

            if(rhs == null) {
                return null;// nope
            }

            Marker m = lhs.precede();
            // should have 'lhs', 'op', 'rhs' now

            m.done(AlchemyElementTypes.BINARY_EXPRESSION);

            lhs = m;
            retn = m;

        }

        return retn;
    }

    private static Marker parseMultiplicativeExpression(PsiBuilder b) {
        // multiplicative_expression ::= switch_expression multiplicative_expression_rhs?

        Marker m = parseSwitchExpression(b);

        if (m == null) {
            return null;
        }

        return parseMultiplicativeExpressionRHS(b, m);
    }

    private static boolean parseLambdaExpression(PsiBuilder b) {
        return false;
    }

    private static boolean parseAssignment(PsiBuilder b) {
        // assignment ::= unary_expression assignment_operator expression
        //              | unary_expression '??=' throwable_expression

        Marker m = b.mark();

        if (parseUnaryExpression(b) == null) {
            m.drop();
            return false;
        }

        if (parseAssignmentOperator(b)) {

            if (parseExpression(b) == null) {
                m.drop();
                return false;
            }

            m.done(AlchemyElementTypes.ASSIGNMENT_EXPRESSION);
            return true;
        }

        if (consume(b, QUEST_QUEST_EQ)) {

            if (!parseThrowableExpression(b)) {
                m.drop();
                return false;
            }

            m.done(AlchemyElementTypes.ASSIGNMENT_EXPRESSION);
            return true;
        }

        m.drop();
        return false;

    }

    private static boolean parseThrowableExpression(PsiBuilder b) {
        return false;
    }


    private static Marker parseCastExpression(PsiBuilder b) {
        // cast_expression ::= '(' type_path ')' unary_expression
        if (b.getTokenType() != LPAREN) {
            return null;
        }

        Marker m = b.mark();
        b.advanceLexer();

        if (!parseTypePath(b)) {
            m.drop();
            return null;
        }

        // todo -- recover
        if (!consume(b, RPAREN)) {
            m.drop();
            return null;
        }

        if (parseUnaryExpression(b) == null) {
            m.drop();
            return null;
        }

        m.done(AlchemyElementTypes.CAST_EXPRESSION);
        return m;

    }

    private static Marker parseUnaryExpressionTail(PsiBuilder b, IElementType tokenType) {
        if (tokenType == b.getTokenType()) {
            b.advanceLexer();
            Marker m = parseUnaryExpression(b);
            if (m != null) {
                m = m.precede();
                m.done(AlchemyElementTypes.UNARY_EXPRESSION);
                return m;
            }
        }
        return null;
    }

    private static Marker parseUnaryExpression(PsiBuilder b) {
        // unary_expression ::= cast_expression
        //                    | primary_expression
        //                    | '+' unary_expression
        //                    | '-' unary_expression
        //                    | '!' unary_expression
        //                    | '~' unary_expression
        //                    | '++' unary_expression
        //                    | '--' unary_expression
        //                    | '&' unary_expression -- pointers
        //                    | '*' unary_expression -- pointers

        Marker m = parseCastExpression(b);
        if (m != null) {
            return m;
        }

        m = parsePrimaryExpression(b);
        if (m != null) {
            return m;
        }

        m = parseUnaryExpressionTail(b, PLUS);
        if (m != null) {
            return m;
        }
        m = parseUnaryExpressionTail(b, MINUS);
        if (m != null) {
            return m;
        }
        m = parseUnaryExpressionTail(b, NOT);
        if (m != null) {
            return m;
        }
        m = parseUnaryExpressionTail(b, BIN_NOT);
        if (m != null) {
            return m;
        }
        m = parseUnaryExpressionTail(b, PLUS_PLUS);
        if (m != null) {
            return m;
        }
        m = parseUnaryExpressionTail(b, MINUS_MINUS);
        if (m != null) {
            return m;
        }
        m = parseUnaryExpressionTail(b, AND);
        if (m != null) {
            return m;
        }

        m = parseUnaryExpressionTail(b, MUL);

        return m;

    }

    private static Marker parsePrimaryExpressionTail(PsiBuilder b) {
        // primary_expression_tail ::= (member_access | method_invocation | '++' | '--' | pointer_deference)
        return null;
    }

    private static Marker parseBracketExpression(PsiBuilder b) {
        // bracket_expression ::= '?'? '[' indexer_argument ']'

        if (b.getTokenType() != QUESTION_MARK && b.getTokenType() != LBRACKET) {
            return null;
        }

        Marker m = b.mark();

        consume(b, QUESTION_MARK);

        if (b.getTokenType() != LBRACKET) {
            m.error("Expected an opening bracket");
            return m;
        }

        if (parseIndexerArgument(b) == null) {
            m.error("Expected an indexer expression");
            return m;
        }

        if (!consume(b, RBRACKET)) {
            m.error("Expected a closing bracket");
            return m;
        }

        m.done(AlchemyElementTypes.INDEXER_ARGUMENT);
        return m;

    }

    private static Marker parseIndexerArgument(PsiBuilder b) {


        return parseExpression(b);
    }

    private static Marker parsePrimaryExpression(PsiBuilder b) {
        // primary_expression ::= primary_expression_start bracket_expression*  (primary_expression_tail* bracket_expression*)*

        Marker pes = parsePrimaryExpressionStart(b);
        if (pes == null) {
            return null;
        }

        Marker m = pes.precede();

        while (parseBracketExpression(b) != null) {
        }

        while (parsePrimaryExpressionTail(b) != null) {
        }

        m.done(AlchemyElementTypes.PRIMARY_EXPRESSION);
        return m;

    }

    private static Marker parseNumericLiteral(PsiBuilder b) {
        IElementType type = b.getTokenType();
        if (type == FLOAT_LITERAL || type == DOUBLE_LITERAL || type == INTEGER_LITERAL || type == UNSIGNED_INTEGER_LITERAL) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.NUMERIC_LITERAL);
            return m;
        }
        return null;
    }

    private static Marker parseNullLiteral(PsiBuilder b) {
        if (b.getTokenType() == NULL) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.NULL_LITERAL);
            return m;
        }
        return null;
    }

    private static Marker parseCharLiteral(PsiBuilder b) {
        if (b.getTokenType() == CHAR_LITERAL) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.CHAR_LITERAL);
            return m;
        }
        return null;
    }

    private static Marker parseBoolLiteral(PsiBuilder b) {
        if (b.getTokenType() == TRUE || b.getTokenType() == FALSE) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.BOOL_LITERAL);
            return m;
        }
        return null;
    }

    private static Marker parseLiteral(PsiBuilder b) {
        Marker numericLiteralMarker = parseNumericLiteral(b);
        if (numericLiteralMarker != null) {
            return numericLiteralMarker;
        }

        Marker defaultMarker = parseDefaultLiteral(b);
        if (defaultMarker != null) {
            return defaultMarker;
        }

        Marker nullMarker = parseNullLiteral(b);
        if (nullMarker != null) {
            return nullMarker;
        }

        Marker charMarker = parseCharLiteral(b);
        if (charMarker != null) {
            return charMarker;
        }

        Marker boolMarker = parseBoolLiteral(b);
        if (boolMarker != null) {
            return boolMarker;
        }

        // todo -- strings & string templates & style literals
        return null;
    }

    private static Marker parsePrimaryIdentifier(PsiBuilder b) {
        // primary_identifier_expression ::= namespace_chain? STANDARD_IDENTIFIER type_argument_list?

        if (b.getTokenType() == STANDARD_IDENTIFIER) {
            Marker m = b.mark();

            parseNamespacePath(b);

            if (b.getTokenType() != STANDARD_IDENTIFIER) {
                m.drop();
                return null;
            }

            b.advanceLexer();

            parseTypeArgumentList(b);

            m.done(AlchemyElementTypes.PRIMARY_IDENTIFIER);

            return m;
        }
        return null;
    }

    private static Marker parseBuiltinType(PsiBuilder b) {
        // builtin_type ::= any_builtin_type_name_except_void

        IElementType token = b.getTokenType();
        if (AlchemyTokenSets.BUILT_IN_TYPES.contains(token)) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.BUILTIN_TYPE);
            return m;
        }
        return null;
    }

    private static Marker parsePrimaryExpressionStart(PsiBuilder b) {
        // primary_expression_start ::= literal
        //                            | identifier
        //                            | '(' expression ')'
        // | identifier type_argument_list?            #simpleNameExpression
        // | OPEN_PARENS expression CLOSE_PARENS       #parenthesisExpressions
        // | predefined_type                           #memberAccessExpression
        // | LITERAL_ACCESS                            #literalAccessExpression
        // | THIS                                      #thisReferenceExpression
        // | BASE ('.' identifier type_argument_list? | '[' expression_list ']') #baseAccessExpression
        // | NEW (type_ (object_creation_expression
        //               | object_or_collection_initializer
        //               | '[' expression_list ']' rank_specifier* array_initializer?
        //               | rank_specifier+ array_initializer)
        //            | anonymous_object_initializer
        //            | rank_specifier array_initializer)                       #objectCreationExpression
        // | OPEN_PARENS argument ( ',' argument )+ CLOSE_PARENS           #tupleExpression
        // | TYPEOF OPEN_PARENS (unbound_type_name | type_ | VOID) CLOSE_PARENS   #typeofExpression
        // | DEFAULT (OPEN_PARENS type_ CLOSE_PARENS)?                     #defaultValueExpression
        // | DELEGATE (OPEN_PARENS explicit_anonymous_function_parameter_list? CLOSE_PARENS)? block #anonymousMethodExpression
        // | SIZEOF OPEN_PARENS type_ CLOSE_PARENS                          #sizeofExpression
        // | NAMEOF OPEN_PARENS (identifier '.')* identifier CLOSE_PARENS  #nameofExpression

        Marker literalMarker = parseLiteral(b);
        if (literalMarker != null) {
            return literalMarker;
        }

        Marker builtinType = parseBuiltinType(b);
        if (builtinType != null) {
            return builtinType;
        }

        Marker primaryIdentifier = parsePrimaryIdentifier(b);
        if (primaryIdentifier != null) {
            return primaryIdentifier;
        }

        return null;

    }

    @Nullable
    private static Marker parseDefaultLiteral(PsiBuilder b) {
        if (b.getTokenType() == DEFAULT) {
            Marker m = b.mark();
            b.advanceLexer();

            if (b.getTokenType() == LPAREN) {

                b.advanceLexer();
                if (!parseTypePath(b)) {
                    m.error("Expected a type path");
                    return m;
                }
                if (!consume(b, RPAREN)) {
                    m.error("Unmatched paren");
                    return m;
                }
            }

            m.done(AlchemyElementTypes.DEFAULT_LITERAL);
            return m;
        }
        return null;
    }

    private static boolean parseBuiltinValueTypeName(PsiBuilder b) {

        if (AlchemyTokenSets.BUILT_IN_PRIMITIVE_TYPES.contains(b.getTokenType())) {
            Marker marker = b.mark();
            b.advanceLexer();
            marker.done(AlchemyElementTypes.BUILTIN_VALUE_TYPE_NAME);
            return true;
        }

        return false;

    }

    private static boolean parseNamespacePart(PsiBuilder b, boolean mark) {
        if (b.getTokenType() == STANDARD_IDENTIFIER) {
            IElementType scopeOperatorCandidate = b.rawLookup(1);
            if (scopeOperatorCandidate == DOUBLE_COLON) {
                if (mark) {
                    Marker m = b.mark();
                    b.advanceLexer();
                    b.advanceLexer();
                    m.done(AlchemyElementTypes.NAMESPACE_PART);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean parseNamespacePath(PsiBuilder b) {

        if (!parseNamespacePart(b, false)) {
            return false;
        }

        Marker m = b.mark();

        while (true) {
            if (!parseNamespacePart(b, true)) {
                break;
            }
        }

        m.done(AlchemyElementTypes.NAMESPACE_CHAIN);
        return true;

    }

    private static boolean parseTypeArgumentList(PsiBuilder b) {
        // type_argument_list ::= '<' type_path (',' type_path)* '>'

        if (b.getTokenType() != LT) {
            return false;
        }

        b.advanceLexer();

        Marker m = b.mark();

        if (!parseTypePath(b)) {
            m.drop();
            return false; // recover?
        }

        while (b.getTokenType() == COMMA) {
            b.advanceLexer();
            if (!parseTypePath(b)) {
                break;
            }
        }

        if (b.getTokenType() != GT) {
            m.error("Expected closing >");
            return true;
        }

        m.done(AlchemyElementTypes.TYPE_ARGUMENT_LIST);
        return true;

    }

    private static boolean parseUserDefinedType(PsiBuilder b) {
        Marker m = b.mark();

        parseNamespacePath(b);

        if (b.getTokenType() == STANDARD_IDENTIFIER) {

            // im not sure how to handle 'missing' or 'optional'
            // false means it didn't match, so if we recover we have to pass true, right?

            parseTypeArgumentList(b);

            m.done(AlchemyElementTypes.CUSTOM_TYPE_NAME);

            return true;
        } else {
            m.drop();
            return false;
        }

    }

    private static boolean parseArraySpecifier(PsiBuilder b) {
        // array_specifier ::= '[' ']'
        if (b.getTokenType() != RBRACKET) {
            return false;
        }

        if (b.lookAhead(1) == LBRACKET) {
            Marker m = b.mark();
            b.advanceLexer();
            b.advanceLexer();
            m.done(AlchemyElementTypes.ARRAY_SPECIFIER);
            return true;
        }

        // todo -- if we see a [ we know its an array specifier at this point, just accept w/ an error

        return false;

    }

    private static boolean parseBuiltinReferenceType(PsiBuilder b) {
        // not sure yet if we will add map types or things w/ generic requirements, but we'll need to figure that out if so
        if (AlchemyTokenSets.BUILT_IN_PRIMITIVE_TYPES.contains(b.getTokenType())) {
            Marker marker = b.mark();
            b.advanceLexer();
            marker.done(BUILTIN_REFERENCE_TYPE_NAME);
            return true;
        }

        return false;
    }

    private static boolean parseBaseType(PsiBuilder b) {
        // base_type ::= simple_type
        //             | builtin_reference_type
        //             | user_defined_type -- represents types: enum, class, interface, delegate, type_parameter
        //             | VOID '*' -- we don't do pointers (yet)
        //             | tuple_type -- not implemented atm

        Marker m = b.mark();

        if (parseBuiltinValueTypeName(b)) {
            m.done(AlchemyElementTypes.BASE_TYPE);
            return true;
        }

        if (parseBuiltinReferenceType(b)) {
            m.done(AlchemyElementTypes.BASE_TYPE);
            return true;
        }

        if (parseUserDefinedType(b)) {
            m.done(AlchemyElementTypes.BASE_TYPE);
            return true;
        }

        m.drop();

        return false;

    }

    private static boolean parseTypePath(PsiBuilder b) {
        // type_path ::= base_type ('?')?

        // we use :: as a namespace scope operator but . for property access
        // System::Collections::Generic::List.Create();

        Marker m = b.mark();

        if (!parseBaseType(b)) {
            m.drop();
            return false;
        }

        // optional
        parseArraySpecifier(b);

        // optional
        consume(b, QUESTION_MARK);

        m.done(AlchemyElementTypes.TYPE_PATH);

        return true;

    }

    static boolean parse_root_(IElementType t, PsiBuilder b, int l) {

        while (!b.eof()) {
            int c = b.rawTokenIndex();

            if (parseStatementExpression(b)) {
                continue;
            }

            // if (tryParseBlock((StatefulBuilder) b)) {
            //     continue;
            // }

            Marker m = b.mark();

            if (consume(b, RBRACE)) {
                m.error("Unexpected closing brace");
            } else {
                b.advanceLexer();
                m.done(THIS_EXPRESSION);
            }

            // no progress
            if (c == b.rawTokenIndex()) {
                b.error("Parsed empty sequence");
                break;
            }

        }

        return true;

        // return alchemyUnit(b, l + 1);
    }

    public void parseStatement(PsiBuilder builder) {
        PsiBuilder.Marker statementMarker = builder.mark();

//        if (!parseIdentifier(builder)) {
//            statementMarker.error("Expected identifier");
//            // Attempt to recover here before returning
//            recover(builder);
//            statementMarker.drop(); // Drop the marker if we couldn't start the statement correctly
//            return;
//        }
//
//        if (builder.getTokenType() != EQ) {
//            builder.error("Expected '='");
//            recover(builder);
//            // Don't return; attempt to parse the rest of the statement
//        } else {
//            builder.advanceLexer(); // Consume '='
//        }
//
//        if (!parseNumber(builder)) {
//            builder.error("Expected number");
//            recover(builder);
//        }
//
//        if (builder.getTokenType() != SEMICOLON) {
//            builder.error("Expected ';'");
//            recover(builder);
//        } else {
//            builder.advanceLexer(); // Consume ';'
//        }
//
//        statementMarker.done(MY_STATEMENT);
    }
}
