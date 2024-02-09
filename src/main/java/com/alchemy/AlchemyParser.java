package com.alchemy;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.alchemy.AlchemyTokenTypes.*;
import static com.alchemy.AlchemyElementTypes.*;

public class AlchemyParser implements PsiParser, LightPsiParser {

    public @NotNull ASTNode parse(@NotNull IElementType t, @NotNull PsiBuilder b) {
        parseLight(t, b);
        return b.getTreeBuilt();
    }

    public void parseLight(IElementType t, PsiBuilder b) {
        boolean r;
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

//    static boolean tryParseBlock(PsiBuilder b) {
//
//        var open = b.getTokenType();
//
//        if (open != LBRACE) {
//            return false;
//        }
//
//        Marker m = markAndAdvance(b);
//
//        boolean finished = false;
//
//        while (!b.eof()) {
//
//            IElementType token = b.getTokenType();
//
//            if (token == LBRACE) {
//
//                if (!tryParseBlock(b)) {
//                    // fail somehow, nothing is valid here that isn't a block with {
//                }
//
//            } else if (token == RBRACE) {
//                b.advanceLexer();
//                m.done(BLOCK);
//                finished = true;
//                break;
//            }
//
//            // parse other rules
//            Marker statement = b.mark();
//
//            if (parseBlockStatement(b)) {
//                statement.done(STATEMENT);
//            } else {
//                statement.error("Expected statement");
//                b.advanceLexer();
//            }
//
//        }
//
//        if (!finished) {
//            m.error("Expected closed block");
//        }
//
//        return true;
//
//    }

    static boolean consume(PsiBuilder b, IElementType tokenType) {
        if (b.getTokenType() == tokenType) {
            b.advanceLexer();
            return true;
        }
        return false;
    }


    private static boolean done(Marker m, IElementType type) {
        m.done(type);
        return true;
    }

    private static boolean rollback(Marker m) {
        m.rollbackTo();
        return false;
    }

    static boolean parseLocalVariableDeclaration(PsiBuilder b, boolean requireSemiColon) {
        // local_variable_declaration ::= ('using' | 'ref' | 'remember') local_variable_type local_variable_declarator
        // local_variable_type ::= 'var' | type_path
        // local_variable_declarator ::= identifier ('=' 'ref'? local_variable_initializer)
        // local_variable_initializer ::= expression | array_initializer |  stackalloc_initializer

        Marker m = b.mark();

        // these are exclusive with each other, verified by annotator
        // need to loop this to read them all, then decide which ones are valid later probably
        while (b.getTokenType() == REMEMBER || b.getTokenType() == REF || b.getTokenType() == USING) {
            b.advanceLexer();
        }

        if (!consume(b, VAR) && !parseTypePath(b)) {
            m.rollbackTo();
            return false;
        }

        if (consume(b, STANDARD_IDENTIFIER)) {

            if (consume(b, EQ)) {

                // semi-colon handled later
                if (parseExpression(b) != null && !parseArrayInitializer(b) && !parseStackAllocInitializer(b)) {
                    m.error("Expected an expression after variable declaration '='");
                    return true;
                }

            }

            if (requireSemiColon && !consume(b, SEMICOLON)) {
                m.error("Expected a semicolon");
                return true;
            }

            m.done(AlchemyElementTypes.LOCAL_VARIABLE_DECLARATION);
            return true;

        }

        m.rollbackTo();
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

    private static boolean parseConstantDeclaration(PsiBuilder b) {
        // local_constant_declaration ::= 'const' type_path identifier '=' expression ';'

        if (b.getTokenType() != CONST) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();
        if (!parseTypePath(b)) {
            m.error("Expected a type");
            return true;
        }
        if (!consume(b, STANDARD_IDENTIFIER)) {
            m.error("Expected an identifier");
            return true;
        }

        if (!consume(b, EQ)) {
            m.error("Expected an '='");
            return true;
        }

        Marker expr = parseExpression(b);
        if (expr == null) {
            m.error("Expected an expression");
            return true;
        }

        if (!consume(b, SEMICOLON)) {
            m.error("Expected a semicolon");
            return true;
        }

        m.done(AlchemyElementTypes.CONSTANT_EXPRESSION);
        return true;

    }

    private static boolean parseTypeParameter(PsiBuilder b) {
        // type_parameter ::= attributes? identifier -- we don't support attributes atm
        return consume(b, STANDARD_IDENTIFIER);
    }

    private static boolean parseTypeParameterList(PsiBuilder b) {
        // type_parameter_list ::= '<' type_parameter (',' type_parameter)* '>'

        if (b.getTokenType() != LT) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();

        if (!parseTypeParameter(b)) {
            m.rollbackTo();
            return false;
        }

        while (b.getTokenType() == COMMA) {
            b.advanceLexer();
            if (!parseTypeParameter(b)) {
                // probably want to scoot forward until > or other breaking character type
                m.error("Expected a type parameter");
                return true;
            }
        }

        if (b.getTokenType() != GT) {
            m.error("Expected a closing angle bracket");
            return true;
        }

        m.done(AlchemyElementTypes.TYPE_PARAMETER_LIST);
        return true;
    }

    private static boolean pareTypeParameterConstraintsClauses(PsiBuilder b) {
        //type_parameter_constraints_clauses ::= type_parameter_constraints_clause*
        return false;
    }

    private static boolean parseArrayType(PsiBuilder b) {
        // array_type ::= base_type '?'? rank_specifier
        Marker m = b.mark();
        if (!parseBaseType(b)) {
            m.rollbackTo();
            return false;
        }
        consume(b, QUESTION_MARK);
        if (!parseArraySpecifier(b)) {
            m.error("Expected an array specifier");
        }
        return true;
    }

    private static boolean parseParameterArray(PsiBuilder b) {
        // parameter_array ::= PARAMS array_type identifier
        if (b.getTokenType() != PARAMS) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();
        if (!parseArrayType(b)) {
            m.error("Expected an array type ");
            return true;
        }
        if (!consume(b, STANDARD_IDENTIFIER)) {
            m.error("Expected an identifier");
            return true;
        }
        m.done(AlchemyElementTypes.PARAMETER_ARRAY);
        return true;

    }

    private static boolean parseParameterModifier(PsiBuilder b) {
        // parameter_modifier ::= 'ref' | 'out' | 'stackalloc'
        // todo -- maybe we support stackalloc + ref or stackalloc + out, maybe rename stackalloc to temp or ephemeral

        if (b.getTokenType() == REF || b.getTokenType() == OUT || b.getTokenType() == STACKALLOC) {
            Marker m = b.mark();
            b.advanceLexer();
            m.done(AlchemyElementTypes.PARAMETER_MODIFIER);
            return true;
        }
        return false;
    }

    private static boolean parseArgDeclaration(PsiBuilder b) {
        // arg_declaration ::= type_ identifier ('=' expression)?

        Marker m = b.mark();
        if (!parseTypePath(b)) {
            m.rollbackTo();
            return false;
        }

        if (!consume(b, STANDARD_IDENTIFIER)) {
            m.error("Expected an identifier");
            return true;
        }

        if (b.getTokenType() == EQ) {
            b.advanceLexer();
            Marker exp = parseExpression(b);
            if (exp == null) {
                m.error("Expected an expression");
                return true;
            }
        }
        m.done(AlchemyElementTypes.ARG_DECLARATION);
        return true;
    }

    private static boolean parseFixedParameter(PsiBuilder b) {
        // fixed_parameter ::= parameter_modifier? arg_declaration

        Marker m = b.mark();

        boolean hasModifier = parseParameterModifier(b);

        if (parseArgDeclaration(b)) {
            m.done(AlchemyElementTypes.FIXED_PARAMETER);
            return true;
        } else {
            if (hasModifier) {
                m.error("Expected an argument declaration");
                return true;
            }
            m.rollbackTo();
            return false;
        }
    }

    private static boolean parseFixedParameters(PsiBuilder b) {
        // fixed_parameters ::= fixed_parameter (',' fixed_parameter)*

        Marker m = b.mark();

        if (!parseFixedParameter(b)) {
            m.rollbackTo();
            return false;
        }

        while (consume(b, COMMA)) {
            if (!parseFixedParameter(b)) {
                m.error("Expected a parameter declaration");
                return true;
            }
        }

        m.done(AlchemyElementTypes.FIXED_PARAMETERS);
        return true;
    }

    private static boolean parseFormalParameterList(PsiBuilder b) {
        // formal_parameter_list ::= parameter_array | fixed_parameters (',' parameter_array)?

        Marker m = b.mark();

        if (parseParameterArray(b)) {
            m.done(AlchemyElementTypes.FORMAL_PARAMETER_LIST);
            return true;
        }

        if (!parseFixedParameters(b)) {
            m.rollbackTo();
            return false;
        }

        if (consume(b, COMMA) && !parseParameterArray(b)) {
            m.error("Expected a params array or no trailing comma");
            return true;
        }

        m.done(AlchemyElementTypes.FORMAL_PARAMETER_LIST);
        return true;
    }

    private static boolean parseLocalFunctionDeclaration(PsiBuilder b) {
        // local_function_declaration ::= local_function_header local_function_body

        Marker m = b.mark();

        if (b.getTokenType() == STATIC) {
            b.advanceLexer();
        }

        // maybe we commit after seeing 'static'
        if (!parseTypePath(b)) {
            m.rollbackTo();
            return false;
        }

        if (!consume(b, STANDARD_IDENTIFIER)) {
            m.rollbackTo();
            return false;
        }

        parseTypeArgumentList(b);

        if (!consume(b, LPAREN)) {
            m.rollbackTo();
            return false;
        }

        parseFormalParameterList(b);

        if (!consume(b, LPAREN)) {
            m.rollbackTo();
            return false;
        }

        m.done(AlchemyElementTypes.LOCAL_FUNCTION_DECLARATION);
        return true;
    }

    private static boolean parseDeclarationStatement(PsiBuilder b) {
        //declaration_statement ::= local_variable_declaration ';'
        //                       | local_constant_declaration ';'
        //                       | local_function_declaration

        return parseLocalVariableDeclaration(b, true) ||
                parseConstantDeclaration(b) ||
                parseLocalFunctionDeclaration(b);
    }

    private static boolean parseBlock(PsiBuilder b) {
        if (b.getTokenType() != LBRACE) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();

        while (parseStatement(b)) {
        }

        if (!consume(b, RBRACE)) {
            m.error("Expected a closing brace");
        } else {
            m.done(AlchemyElementTypes.BLOCK);
        }
        return true;
    }

    private static boolean parseEmbeddedStatement(PsiBuilder b) {
        // embedded_statement ::= block | simple_embedded_statement

        if (parseBlock(b)) {
            return true;
        }

        return parseSimpleEmbeddedStatement(b);
    }

    private static boolean parseReturnStatement(PsiBuilder b) {
        // return_statement ::= 'return' expression? ';'
        if(b.getTokenType() != RETURN) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();

        // optional
        parseExpression(b);

        if(!requireSemiColon(b, m)) {
            return true;
        }
        m.done(AlchemyElementTypes.RETURN_STATEMENT);
        return true;
    }

    private static boolean parseSimpleEmbeddedStatement(PsiBuilder b) {
        // simple_embedded_statement ::= ';' 
        // | expression ';' 
        // | if_statement              
        // | switch_statement
        // | while_loop
        // | do_while_loop
        // | for_loop  
        // | foreach_loop
        // | break_statement
        // | continue_statement
        // | return_statement
        // | throw_expression ';'
        // | -- todo -- using/other stack scoped expressions USING OPEN_PARENS resource_acquisition CLOSE_PARENS embedded_statement # usingStatement
        // | -- todo --  TRY block (catch_clauses finally_clause? | finally_clause)             # tryStatement

        if (b.getTokenType() == SEMICOLON) {
            b.advanceLexer();
            return true;
        }

        if (parseIfStatement(b)) {
            return true;
        }

        if (parseSwitchStatement(b)) {
            return true;
        }

        if (parseWhileStatement(b)) {
            return true;
        }

        if (parseDoWhileLoopStatement(b)) {
            return true;
        }
        
        if(parseForLoop(b)){
            return true;
        }

        if(parseForEachLoop(b)){
            return true;
        }

        if(parseReturnStatement(b)) {
            return true;
        }

        if (parseBreakStatement(b)) {
            return true;
        }

        if (parseContinueStatement(b)) {
            return true;
        }

        if(parseThrowExpression(b)) { // todo -- semi colon
            return true;
        }

        return parseExpressionStatement(b);
    }

    private static boolean parseContinueStatement(PsiBuilder b) {
        if (b.getTokenType() != CONTINUE) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();

        if (!requireSemiColon(b, m)) {
            return true;
        }

        m.done(AlchemyElementTypes.CONTINUE_STATEMENT);
        return true;
    }

    private static boolean parseBreakStatement(PsiBuilder b) {
        // break_statement ::= 'break' ';'
        if (b.getTokenType() != BREAK) {
            return false;
        }

        Marker m = b.mark();
        b.advanceLexer();

        if (!requireSemiColon(b, m)){
            return true;
        }

        m.done(AlchemyElementTypes.BREAK_STATEMENT);
        return true;
    }

    private static boolean parseForEachLoop(PsiBuilder b) {
        // foreach_loop ::= FOREACH OPEN_PARENS local_variable_type identifier IN expression CLOSE_PARENS embedded_statement

        if(b.getTokenType() != FOREACH){
            return false;
        }

        Marker m = b.mark();
        b.advanceLexer();

        if(!requireOpenParen(b, m)) {
            return true;
        }

        if(!consume(b, VAR) && !parseTypePath(b)) {
            m.error("Expected a variable type");
            return true;
        }

        if(!consume(b, STANDARD_IDENTIFIER)) {
            m.error("Expected an identifier");
            return true;
        }

        if(!consume(b, IN)) {
            m.error("Expected an 'in' keyword");
            return true;
        }

        Marker expr = parseExpression(b);

        if(expr == null) {
            m.error("Expected an expression");
            return true;
        }

        if(!requireClosingParen(b, m)) {
            return true;
        }

        if(!parseEmbeddedStatement(b)){
            m.error("Expected a loop body");
            return true;
        }

        m.done(AlchemyElementTypes.FOR_EACH_LOOP);
        return true;
    }

    private static boolean requireOpenParen(PsiBuilder b, Marker m) {
        if(!consume(b, LPAREN)) {
            m.error("Expected an open paren");
            return false;
        }
        return true;
    }

    private static boolean requireClosingParen(PsiBuilder b, Marker m) {
        if(!consume(b, RPAREN)) {
            m.error("Expected a closing paren");
            return false;
        }
        return true;
    }

    private static boolean requireSemiColon(PsiBuilder b, Marker m) {
        if(!consume(b, SEMICOLON)) {
            m.error("Expected a semicolon");
            return false;
        }
        return true;
    }

    private static void parseForLoopCondition(PsiBuilder b) {
        // for_loop_condition ::= expression

        Marker condition = parseExpression(b);
        if(condition == null) {
            return;
        }

        Marker m = condition.precede();
        m.done(AlchemyElementTypes.FOR_LOOP_CONDITION);
    }

    private static boolean parseForLoop(PsiBuilder b) {
        // for_loop ::= FOR OPEN_PARENS for_initializer? ';' for_loop_condition? ';' for_iterator? CLOSE_PARENS embedded_statement

        if(b.getTokenType() != FOR) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();

        if (!requireOpenParen(b, m)) {
            return true;
        }

        // optional 
        parseForLoopInitializer(b);
        
        if(!requireSemiColon(b, m)) {
            // scan to matched ) or new line or statement keyword 
            return true;
        }

        // optional
        parseForLoopCondition(b);

        if(!requireSemiColon(b, m)) {
            // scan to matched ) or new line or statement keyword 
            return true;
        }
        
        // optional
        parseForLoopIterator(b);
        
        if(!requireClosingParen(b, m)) {
            return true;
        }

        if(!parseEmbeddedStatement(b)){
            m.error("Expected a for loop body");
            return true;
        }

        m.done(AlchemyElementTypes.FOR_LOOP);
        return true;
        
    }

    private static void parseForLoopIterator(PsiBuilder b) {
        // for_loop_iterator ::= expression (',' expression)*

        Marker expr = parseExpression(b);
        if(expr == null) {
            return;
        }
        Marker m = expr.precede();
        while(consume(b, COMMA) && parseExpression(b) != null) {
            // no op
        }
        m.done(AlchemyElementTypes.FOR_LOOP_ITERATOR);
    }

    private static void parseForLoopInitializer(PsiBuilder b) {
        // for_loop_initializer ::= local_variable_declaration (', 'local_variable_declaration)*
        //                   | expression (',' expression)*

        Marker m = b.mark();

        if(parseLocalVariableDeclaration(b, false)){

            while(consume(b, COMMA) && parseLocalVariableDeclaration(b, false)) {
                // no op
            }
            m.done(AlchemyElementTypes.FOR_LOOP_INITIALIZER);
            return;
        }
        
        Marker firstExpr = parseExpression(b);
        if(firstExpr != null) {
            while(consume(b, COMMA) && parseExpression(b) != null) {
                // no-op
            }
            m.done(AlchemyElementTypes.FOR_LOOP_INITIALIZER);
            return;
        }
        
        m.rollbackTo();
        
    }

    private static boolean parseDoWhileLoopStatement(PsiBuilder b) {
        // do_while_loop ::= DO embedded_statement WHILE OPEN_PARENS expression CLOSE_PARENS ';'

        if (b.getTokenType() != DO) {
            return false;
        }

        Marker m = b.mark();
        b.advanceLexer();

        if (!parseEmbeddedStatement(b)) {
            m.error("Expected a do while statement body");
            return true;
        }

        if (b.getTokenType() != WHILE) {
            m.error("Expected a 'while' keyword");
            return true;
        }

        b.advanceLexer();

        if (!consume(b, LPAREN)) {
            m.error("Expected a open paren");
            return true;
        }

        Marker expr = parseExpression(b);

        if (expr == null) {
            m.error("Expected an expression");
            return true;
        }

        if (!consume(b, RPAREN)) {
            m.error("Expected a closing paren");
            return true;
        }

        m.done(AlchemyElementTypes.DO_WHILE_LOOP);
        return true;

    }

    private static boolean parseWhileStatement(PsiBuilder b) {
        // while_statement ::= 'while' OPEN_PARENS expression CLOSE_PARENS embedded_statement
        if (b.getTokenType() != WHILE) {
            return false;
        }

        Marker m = b.mark();
        b.advanceLexer();

        if (!consume(b, LPAREN)) {
            m.error("Expected an open paren");
            return true;
        }

        Marker expr = parseExpression(b);
        if (expr == null) {
            m.error("Expected an expression");
            return true;
        }

        if (!consume(b, RPAREN)) {
            m.error("Expected a closing paren");
            return true;
        }

        if (!parseEmbeddedStatement(b)) {
            m.error("Expected a while loop body");
            return true;
        }

        m.done(AlchemyElementTypes.WHILE_LOOP);
        return true;

    }

    private static boolean parseSwitchStatement(PsiBuilder b) {
        // switch_statement ::= SWITCH OPEN_PARENS expression CLOSE_PARENS OPEN_BRACE switch_section* CLOSE_BRACE
        if (b.getTokenType() != SWITCH) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();

        if (!consume(b, LPAREN)) {
            m.error("Expected an opening paren");
            return true;
        }

        Marker expr = parseExpression(b);
        if (expr == null) {
            m.error("Expected an opening paren");
            return true;
        }

        if (!consume(b, RPAREN)) {
            m.error("Expected a closing paren");
            return true;
        }

        if (!consume(b, LBRACE)) {
            m.error("Expected a switch body");
            return true;
        }

        while (parseSwitchSection(b)) {
            // no-op
        }

        if (!consume(b, LBRACE)) {
            m.error("Expected a closing brace");
            return true;
        }

        m.done(AlchemyElementTypes.SWITCH_STATEMENT);
        return true;
    }

    private static boolean parseSwitchSection(PsiBuilder b) {
        // switch_section ::= switch_label+ statement_list

        Marker firstLabel = parseSwitchLabel(b);

        if (firstLabel == null) {
            return false;
        }

        Marker section = firstLabel.precede();

        while (parseSwitchLabel(b) != null) {
            // no op
        }

        if (!parseStatement(b)) {
            section.error("Expected at least one statement in switch section body");
            return true;
        }

        while (parseStatement(b)) {
            // no op
        }

        section.done(AlchemyElementTypes.SWITCH_SECTION);
        return true;
    }

    private static Marker parseSwitchLabel(PsiBuilder b) {
        // switch_label ::= ('case' expression case_guard? ':') | ('default' ':')

        if (b.getTokenType() == CASE) {
            Marker m = b.mark();
            b.advanceLexer();
            Marker expr = parseExpression(b);
            if (expr == null) {
                m.error("Expected an expression");
                return m;
            }
            parseCaseGuard(b);

            if (!consume(b, COLON)) {
                m.error("Expected a colon");
                return m;
            }
            m.done(AlchemyElementTypes.SWITCH_LABEL);
            return m;

        } else if (b.getTokenType() == DEFAULT) {
            Marker m = b.mark();
            b.advanceLexer();
            if (!consume(b, COLON)) {
                m.error("Expected a colon");
                return m;
            }
            m.done(AlchemyElementTypes.SWITCH_DEFAULT_LABEL);
            return m;
        } else {
            return null;
        }
    }

    private static boolean parseIfBody(PsiBuilder b) {
        // if_body ::= block | simple_embedded_statement
        // todo -- maybe create a node?
        return parseBlock(b) || parseSimpleEmbeddedStatement(b);
    }

    private static boolean parseIfStatement(PsiBuilder b) {
        // if_statement ::= IF OPEN_PARENS expression CLOSE_PARENS if_body (ELSE if_body)?
        if (b.getTokenType() != IF) {
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();
        // todo -- maybe handle constexpr
        if (!consume(b, LPAREN)) {
            m.error("Expected an opening paren");
            return true;
        }

        Marker expr = parseExpression(b);
        if (expr == null) {
            m.error("Expected an expression");
            return true;
        }

        if (!consume(b, RPAREN)) {
            m.error("Expected a closing paren");
            return true;
        }

        if (!parseIfBody(b)) {
            m.error("Expected a body block or expression");
            return true;
        }

        if (b.getTokenType() == ELSE) {
            b.advanceLexer();
            if (!parseIfBody(b)) {
                m.error("Expected a body block or expression");
                return true;
            }
        }

        m.done(AlchemyElementTypes.IF_STATEMENT);
        return true;
    }

    private static boolean parseExpressionStatement(PsiBuilder b) {
        // expression_statement ::= expression ';'

        Marker e = parseExpression(b);

        if(e != null) {
            Marker m = e.precede();
            if(!requireSemiColon(b, m)) {
                return true;
            }
            m.done(AlchemyElementTypes.EXPRESSION_STATEMENT);
            return true;
        }
        return false;

    }

    private static boolean parseStatement(PsiBuilder b) {
        // statement_expression ::= (declaration_statement | embedded_statement)
        return parseDeclarationStatement(b) || parseEmbeddedStatement(b);
    }

    private static Marker parseNonAssignmentExpression(PsiBuilder b) {
        // non_assignment_expression ::= lambda_expression | ternary_expression

        Marker lambda = parseLambdaExpression(b);

        if (lambda != null) {
            return lambda;
        }

        return parseTernaryExpression(b);

    }

    private static Marker parseExpression(PsiBuilder b) {
        // expression ::= assignment | non_assignment_expression | 'ref' non_assignment_expression

        Marker assignment = parseAssignment(b);

        if (assignment != null) {
            return assignment;
        }

        return parseNonAssignmentExpression(b);
    }

    private static boolean parseAssignmentOperator(PsiBuilder b) {
        if (AlchemyTokenSets.ASSIGNMENT_OPERATORS.contains(b.getTokenType())) {
            b.advanceLexer();
            return true;
        }
        return false;
    }

    private static Marker parseNonAssignment(PsiBuilder b) {
        // non_assignment_expression ::= lambda_expression
        //                             | conditional_expression

        Marker m = parseLambdaExpression(b);
        if (m != null) {
            return m;
        }
        return parseTernaryExpression(b);
    }

    // this is a ternary
    private static Marker parseTernaryExpression(PsiBuilder b) {
        // conditional_expression ::= null_coalescing_expression ('?' throwable_expression ':' throwable_expression)?

        Marker m = parseNullCoalescingExpression(b);

        if (m == null) {
            return null;
        }

        if (b.getTokenType() == QUESTION_MARK) {
            m = m.precede();

            b.advanceLexer();

            Marker throwable = parseThrowableExpression(b);
            if (throwable == null) {
                m.error("Expected an expression");
                return m;
            }

            if (!consume(b, COLON)) {
                m.error("Expected a ':'");
                return m;
            }

            Marker throwable2 = parseThrowableExpression(b);
            if (throwable2 == null) {
                m.error("Expected a throwable expression");
                return m;
            }

            m.done(AlchemyElementTypes.TERNARY_EXPRESSION);

        }

        return m;
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
        // throw_expression :: 'throw' expression ';' | 'throw' expression

        if(b.getTokenType() != THROW){
            return false;
        }
        Marker m = b.mark();
        b.advanceLexer();
        Marker expr = parseExpression(b);
        if(expr != null) {
            m.done(AlchemyElementTypes.THROW_EXPRESSION);
        }
        else {
            m.error("Expected an expression");
        }
        return true;
    }

    private static Marker parseConditionalOrExpressionRHS(PsiBuilder b, Marker lhs) {
        // conditional_or_expression_rhs ::= ('||'  conditional_and_expression)*

        Marker retn = lhs;

        while (b.getTokenType() == OR) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseConditionalAndExpression(b);

            if (rhs == null) {
                return null;// nope
            }

            Marker m = lhs.precede();
            // should have 'lhs', 'op', 'rhs' now

            m.done(AlchemyElementTypes.CONDITIONAL_OR_EXPRESSION);

            lhs = m;
            retn = m;

        }

        return retn;
    }

    private static Marker parseConditionalOrExpression(PsiBuilder b) {
        // conditional_or_expression ::= conditional_and_expression conditional_or_expression_rhs

        Marker m = parseConditionalAndExpression(b);

        if (m == null) {
            return null;
        }

        return parseConditionalOrExpressionRHS(b, m);

    }

    private static Marker parseConditionalAndExpressionRHS(PsiBuilder b, Marker lhs) {
        // conditional_and_expression_rhs ::= ('&&' bitwise_or_expression)*

        Marker retn = lhs;

        while (b.getTokenType() == AND_AND) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR); // conditional operator?

            Marker rhs = parseBitwiseOrExpression(b);

            if (rhs == null) {
                return null;// nope
            }

            Marker m = lhs.precede();
            // should have 'lhs', 'op', 'rhs' now

            m.done(AlchemyElementTypes.CONDITIONAL_AND_EXPRESSION);

            lhs = m;
            retn = m;

        }

        return retn;

    }

    private static Marker parseConditionalAndExpression(PsiBuilder b) {
        // conditional_and_expression ::= bitwise_or_expression bitwise_or_expression_rhs

        Marker m = parseBitwiseOrExpression(b);

        if (m == null) {
            return null;
        }

        return parseConditionalAndExpressionRHS(b, m);
    }

    private static Marker parseBitwiseOrExpressionRHS(PsiBuilder b, Marker lhs) {
        //bitwise_or_expression_rhs ::= ('|' exclusive_or_expression)*
        Marker retn = lhs;

        while (b.getTokenType() == OR) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseXorExpression(b);

            if (rhs == null) {
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

    private static Marker parseBitwiseOrExpression(PsiBuilder b) {
        // inclusive_or_expression ::= exclusive_or_expression
        Marker m = parseXorExpression(b);

        if (m == null) {
            return null;
        }

        return parseBitwiseOrExpressionRHS(b, m);
    }

    private static Marker parseXorExpressionRHS(PsiBuilder b, Marker lhs) {
        // xor_expression_rhs ::= ('^' bitwise_and_expression)*
        Marker retn = lhs;

        while (b.getTokenType() == XOR) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseBitwiseAndExpression(b);

            if (rhs == null) {
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

    private static Marker parseXorExpression(PsiBuilder b) {
        // exclusive_or_expression ::= bitwise_and_expression xor_expression_rhs

        Marker m = parseBitwiseAndExpression(b);

        if (m == null) {
            return null;
        }

        return parseXorExpressionRHS(b, m);
    }

    private static Marker parseBitwiseAndExpressionRHS(PsiBuilder b, Marker lhs) {
        // bitwise_and_expression_rhs ::= ('&' equality_expression)*
        Marker retn = lhs;

        while (b.getTokenType() == AND) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseEqualityExpression(b);

            if (rhs == null) {
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

    private static Marker parseBitwiseAndExpression(PsiBuilder b) {
        // bitwise_and_expression ::= equality_expression and_expression_rhs

        Marker m = parseEqualityExpression(b);

        if (m == null) {
            return null;
        }

        return parseBitwiseAndExpressionRHS(b, m);
    }

    private static Marker parseEqualityExpressionRHS(PsiBuilder b, Marker lhs) {
        // equality_expression_rhs ::== ((OP_EQ | OP_NE) relational_expression)*
        Marker retn = lhs;

        while (b.getTokenType() == EQ_EQ || b.getTokenType() == NEQ) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.EQUALITY_OPERATOR); // -- maybe use an equality node?

            Marker rhs = parseRelationalExpression(b);

            if (rhs == null) {
                return null;// nope
            }

            Marker m = lhs.precede();
            // should have 'lhs', 'op', 'rhs' now

            m.done(AlchemyElementTypes.EQUALITY_EXPRESSION); // maybe an equality expression

            lhs = m;
            retn = m;

        }

        return retn;
    }

    private static Marker parseEqualityExpression(PsiBuilder b) {
        // equality_expression ::= relational_expression equality_expression_rhs
        Marker m = parseRelationalExpression(b);

        if (m == null) {
            return null;
        }

        return parseEqualityExpressionRHS(b, m);

    }

    private static Marker parseAsTypeExpression(PsiBuilder b) {
        // as_type_expression ::= 'as' type_path;
        return null;
    }

    private static Marker parseIsTypeExpression(PsiBuilder b) {
        // is_type_expression ::= 'is' base_type rank_specifier? '?'? identifier?
        if (b.getTokenType() != IS) {
            return null;
        }
        Marker m = b.mark();

        b.advanceLexer();

        if (!parseBaseType(b)) {
            m.error("Expected a type name");
            return m;
        }

        // optional
        parseArraySpecifier(b);
        // optional
        consume(b, QUESTION_MARK);
        // optional
        consume(b, STANDARD_IDENTIFIER);

        m.done(AlchemyElementTypes.IS_EXPRESSION_TYPE);
        return m;
    }

    private static Marker parseRelationalExpressionRHS(PsiBuilder b, Marker lhs) {
        // relational_expression_rhs ::= ( (relational_operator shift_expression) | is_type_expression | as_type_expression )*
        // relational_operator ::= ('<' | '>' | '<=' | '>=')

        // x is Type t
        // x as Type -- technically could do a cast and chain it with some operator, semantic analysis will need to check illegal cases
        // x > 5

        Marker retn = lhs;

        while (b.getTokenType() == IS || b.getTokenType() == AS || b.getTokenType() == LT || b.getTokenType() == GT || b.getTokenType() == LT_EQ || b.getTokenType() == GT_EQ) {

            if (b.getTokenType() == LT || b.getTokenType() == GT || b.getTokenType() == LT_EQ || b.getTokenType() == GT_EQ) {
                Marker op = b.mark();
                b.advanceLexer();
                op.done(AlchemyElementTypes.BINARY_OPERATOR); // maybe we use comparison operator instead

                Marker rhs = parseShiftExpression(b);

                if (rhs == null) {
                    // todo -- expect expression probably & return true
                    return null; // nope
                }

                Marker m = lhs.precede();
                // should have 'lhs', 'op', 'rhs' now

                m.done(AlchemyElementTypes.BINARY_EXPRESSION); // maybe we use comparison expression instead

                lhs = m;
                retn = m;
            } else if (b.getTokenType() == IS) {
                Marker m = lhs.precede();

                if (parseIsTypeExpression(b) == null) {
                    m.error("Expected an 'is' expression");
                } else {
                    m.done(AlchemyElementTypes.BINARY_EXPRESSION);
                }

                lhs = m;
                retn = m;
            } else { // as
                Marker m = lhs.precede();

                b.advanceLexer();

                if (!parseTypePath(b)) {
                    m.error("Expected a type name");
                } else {
                    m.done(AlchemyElementTypes.AS_EXPRESSION);
                }

                lhs = m;
                retn = m;
            }

        }

        return retn;

    }

    private static Marker parseRelationalExpression(PsiBuilder b) {
        // relational_expression ::= shift_expression

        Marker m = parseShiftExpression(b);

        if (m == null) {
            return null;
        }

        return parseRelationalExpressionRHS(b, m);
    }

    private static boolean sequenceWithoutSpace(PsiBuilder b, IElementType t0, IElementType t1) {
        IElementType tType0 = b.getTokenType();
        IElementType tType1 = b.rawLookup(1);
        return t0 == tType0 && t1 == tType1;
    }

    private static Marker parseShiftExpressionRHS(PsiBuilder b, Marker lhs) {
        // shift_expression_rhs ::= (('<' '<' | '>' '>') additive_expression)* -- no whitespace

        Marker retn = lhs;

        while (sequenceWithoutSpace(b, LT, LT) || sequenceWithoutSpace(b, GT, GT)) {

            Marker op = b.mark();
            b.advanceLexer();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseAdditiveExpression(b);

            if (rhs == null) {
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

        while (b.getTokenType() == PLUS || b.getTokenType() == MINUS) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseMultiplicativeExpression(b);

            if (rhs == null) {
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

    private static Marker parseSwitchExpressionArms(PsiBuilder b) {
        // switch_expression_arms ::= switch_expression_arm (',' switch_expression_arm)*

        Marker first = parseSwitchExpressionArm(b);

        if (first == null) {
            return null;
        }

        Marker m = first.precede();

        while (b.getTokenType() == COMMA) {
            b.advanceLexer();
            parseSwitchExpressionArm(b);
        }

        m.done(AlchemyElementTypes.SWITCH_EXPRESSION_ARMS);
        return m;
    }

    private static Marker parseCaseGuard(PsiBuilder b) {
        // case_guard ::= 'when' expression;
        if (b.getTokenType() == WHEN) {
            Marker m = b.mark();
            b.advanceLexer();
            Marker e = parseExpression(b);
            if (e == null) {
                m.error("Expected an expression");
                return m;
            }
            m.done(AlchemyElementTypes.CASE_GUARD);
            return m;
        }
        return null;
    }

    private static Marker parseSwitchExpressionArm(PsiBuilder b) {
        // switch_expression_arm ::= expression case_guard? right_arrow throwable_expression

        Marker m = parseExpression(b);
        if (m == null) {
            return null;
        }
        m = m.precede();
        // optional
        parseCaseGuard(b);

        if (b.getTokenType() != RIGHT_ARROW) {
            m.error("Expected a right arrow");
            return m;
        }

        b.advanceLexer();

        Marker throwable = parseThrowableExpression(b);
        if (throwable == null) {
            m.error("Expected an expression");
            return m;
        }

        m.done(AlchemyElementTypes.SWITCH_EXPRESSION_ARM);
        return m;
    }

    private static Marker parseSwitchExpression(PsiBuilder b) {
        // switch_expression ::= range_expression ('switch' '{' (switch_expression_arms ','?)? '}')?
        // I think i can change this to (switch_arm_expr | range_expression)

        Marker m = parseRangeExpression(b);

        if (!consume(b, SWITCH)) {
            return m;
        }

        m = m.precede();

        if (!consume(b, LBRACE)) {
            m.error("Expected an open brace after switch");
            return m;
        }

        Marker arms = parseSwitchExpressionArms(b);
        if (arms == null) {
            m.error("Expected switch expression arms");
            return m;
        }

        // optional
        consume(b, COMMA);

        if (!consume(b, RBRACE)) {
            m.error("Expected a closing brace after switch");
            return m;
        }

        m.done(AlchemyElementTypes.SWITCH_EXPRESSION);
        return m;
    }

    private static Marker parseRangeExpression(PsiBuilder b) {
        // range_expression ::= unary_expression
        //                    | unary_expression? OP_RANGE unary_expression? -- not supported right now
        return parseUnaryExpression(b);
    }

    private static Marker parseMultiplicativeExpressionRHS(PsiBuilder b, Marker lhs) {
        // multiplicative_expression_rhs ::= (('*' | '/' | '%')  switch_expression)*

        Marker retn = lhs;

        while (b.getTokenType() == MUL || b.getTokenType() == DIV || b.getTokenType() == PERCENT) {

            Marker op = b.mark();
            b.advanceLexer();
            op.done(AlchemyElementTypes.BINARY_OPERATOR);

            Marker rhs = parseSwitchExpression(b);

            if (rhs == null) {
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

    private static Marker parseLambdaExpression(PsiBuilder b) {
        return null;
    }

    private static Marker parseAssignment(PsiBuilder b) {
        // assignment ::= unary_expression assignment_operator expression
        //              | unary_expression '??=' throwable_expression

        Marker m = b.mark();

        if (parseUnaryExpression(b) == null) {
            m.rollbackTo();
            return null;
        }

        if (parseAssignmentOperator(b)) {

            if (parseExpression(b) == null) {
                m.rollbackTo();
                return null;
            }

            m.done(AlchemyElementTypes.ASSIGNMENT_EXPRESSION);
            return m;
        }

        if (consume(b, QUEST_QUEST_EQ)) {

            Marker throwable = parseThrowableExpression(b);
            if (throwable == null) {
                m.rollbackTo();
                return null;
            }

            m.done(AlchemyElementTypes.ASSIGNMENT_EXPRESSION);
            return m;
        }

        m.rollbackTo();
        return null;

    }

    private static Marker parseThrowableExpression(PsiBuilder b) {

        if (b.getTokenType() == THROW) {
            b.advanceLexer();
            Marker m = parseExpression(b);
            if (m != null) {
                m = m.precede();
                m.done(AlchemyElementTypes.THROW_EXPRESSION);
                return m;
            }
            return null;
        } else {
            return parseExpression(b);
        }
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
        // indexer_argument ::= expression
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

        Marker m = b.mark();

        b.advanceLexer();

        if (!parseTypePath(b)) {
            m.rollbackTo();
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

        b.advanceLexer();
        m.done(AlchemyElementTypes.TYPE_ARGUMENT_LIST);
        return true;

    }

    private static boolean parseUserDefinedType(PsiBuilder b) {
        Marker m = b.mark();

        parseNamespacePath(b);

        if (b.getTokenType() == STANDARD_IDENTIFIER) {

            // im not sure how to handle 'missing' or 'optional'
            // false means it didn't match, so if we recover we have to pass true, right?

            b.advanceLexer();

            parseTypeArgumentList(b);

            m.done(AlchemyElementTypes.CUSTOM_TYPE_NAME);

            return true;
        } else {
            m.rollbackTo();
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

            if (parseStatement(b)) {
                continue;
            }

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

    }
}
