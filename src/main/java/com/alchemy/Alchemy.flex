package com.alchemy;

import java.util.*;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static com.alchemy.AlchemyTokenTypes.*;
import static com.alchemy.AlchemyTokenSets.*;

//@SuppressWarnings("DuplicateBranchesInSwitch")
%%
%{
  private static final class State {
    final int lBraceCount;
    final int state;

    private State(int state, int lBraceCount) {
      this.state = state;
      this.lBraceCount = lBraceCount;
    }

    @Override
    public String toString() {
      return "yystate = " + state + (lBraceCount == 0 ? "" : "lBraceCount = " + lBraceCount);
    }
  }

  protected final Stack<State> myStateStack = new Stack<State>();
  protected int myLeftBraceCount;

  private void pushState(int state) {
    myStateStack.push(new State(yystate(), myLeftBraceCount));
    myLeftBraceCount = 0;
    yybegin(state);
  }

  private void popState() {
    State state = myStateStack.pop();
    myLeftBraceCount = state.lBraceCount;
    yybegin(state.state);
  }

  AlchemyLexer() {
    this(null);
  }
%}

%class AlchemyLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
  myLeftBraceCount = 0;
  myStateStack.clear();
%eof}

%xstate MULTI_LINE_COMMENT_STATE QUO_STRING THREE_QUO_STRING APOS_STRING SHORT_TEMPLATE_ENTRY LONG_TEMPLATE_ENTRY

DIGIT=[0-9]
HEX_DIGIT=[0-9a-fA-F]
LETTER=[a-z]|[A-Z]
WHITE_SPACE=[ \n\t\f]+
PROGRAM_COMMENT="#""!"[^\n]*
SINGLE_LINE_COMMENT="/""/"[^\n]*
SINGLE_LINE_COMMENTED_COMMENT="/""/""/""/"[^\n]*

MULTI_LINE_DEGENERATE_COMMENT = "/*" "*"+ "/"
MULTI_LINE_COMMENT_START      = "/*"
MULTI_LINE_COMMENT_END        = "*/"

RAW_TRIPLE_QUOTED_STRING= "r" {RAW_TRIPLE_QUOTED_LITERAL}

RAW_TRIPLE_QUOTED_LITERAL = {THREE_QUO}  ([^\"] | \"[^\"] | \"\"[^\"])* {THREE_QUO}?

THREE_QUO =  (\"\"\")

SHORT_TEMPLATE_ENTRY=\${IDENTIFIER_NO_DOLLAR}
LONG_TEMPLATE_ENTRY_START=\$\{

IDENTIFIER_START_NO_DOLLAR={LETTER}|"_"
IDENTIFIER_START={IDENTIFIER_START_NO_DOLLAR}|"$"
IDENTIFIER_PART_NO_DOLLAR={IDENTIFIER_START_NO_DOLLAR}|{DIGIT}
IDENTIFIER_PART={IDENTIFIER_START}|{DIGIT}
STANDARD_IDENTIFIER={IDENTIFIER_START}{IDENTIFIER_PART}*
IDENTIFIER_NO_DOLLAR={IDENTIFIER_START_NO_DOLLAR}{IDENTIFIER_PART_NO_DOLLAR}*

//NUMBER = ({DIGIT}+ ("." {DIGIT}+)? {EXPONENT}?) | ("." {DIGIT}+ {EXPONENT}?)
//EXPONENT = [Ee] ["+""-"]? {DIGIT}*
//HEX_NUMBER = 0 [Xx] {HEX_DIGIT}*

COMMENT_START=\/\/
DOUBLE_LITERAL=[0-9]+\.[0-9]+
FLOAT_LITERAL=([0-9]+(f|m)) | ([0-9]+\.[0-9]+(f|m))
INTEGER_LITERAL=[0-9][Ll]?+
UNSIGNED_INTEGER_LITERAL=[0-9]+(UL|ul|u)?
HEX_COLOR=#([a-fA-F0-9]{6}|[a-fA-F0-9]{3}) // todo -- figure out what to do here

%%

<YYINITIAL> "{"                { return LBRACE; }
<YYINITIAL> "}"                { return RBRACE; }
<LONG_TEMPLATE_ENTRY> "{"      { myLeftBraceCount++; return LBRACE; }
<LONG_TEMPLATE_ENTRY> "}"      {
                                   if (myLeftBraceCount == 0) {
                                     popState();
                                     return LONG_TEMPLATE_ENTRY_END;
                                   }
                                   myLeftBraceCount--;
                                   return RBRACE;
                               }

<YYINITIAL, LONG_TEMPLATE_ENTRY> {WHITE_SPACE}                   { return WHITE_SPACE;             }

// single-line comments
<YYINITIAL, LONG_TEMPLATE_ENTRY> {SINGLE_LINE_COMMENTED_COMMENT} { return SINGLE_LINE_COMMENT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> {SINGLE_LINE_COMMENT}           { return SINGLE_LINE_COMMENT;     }
<YYINITIAL>                      {PROGRAM_COMMENT}               { return SINGLE_LINE_COMMENT;     }

// multi-line comments
<YYINITIAL, LONG_TEMPLATE_ENTRY> {MULTI_LINE_DEGENERATE_COMMENT} { return MULTI_LINE_COMMENT;      } // without this rule /*****/ is parsed as doc comment and /**/ is parsed as not closed doc comment

// next rules return temporary IElementType's that are rplaced with AlchemyTokenTypesSets#MULTI_LINE_COMMENT or AlchemyTokenTypesSets#MULTI_LINE_DOC_COMMENT in com.jetbrains.lang.Alchemy.lexer.AlchemyLexer
<YYINITIAL, LONG_TEMPLATE_ENTRY> {MULTI_LINE_COMMENT_START}      { pushState(MULTI_LINE_COMMENT_STATE); return MULTI_LINE_COMMENT_START;                                                                 }

<MULTI_LINE_COMMENT_STATE>       {MULTI_LINE_COMMENT_START}      { pushState(MULTI_LINE_COMMENT_STATE); return MULTI_LINE_COMMENT_BODY;                                                                  }
<MULTI_LINE_COMMENT_STATE>       [^]                             {                                      return MULTI_LINE_COMMENT_BODY;                                                                  }
<MULTI_LINE_COMMENT_STATE>       {MULTI_LINE_COMMENT_END}        { popState();                          return yystate() == MULTI_LINE_COMMENT_STATE ? MULTI_LINE_COMMENT_BODY : MULTI_LINE_COMMENT_END; }

// reserved words
<YYINITIAL, LONG_TEMPLATE_ENTRY> "assert"               { return ASSERT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "break"                { return BREAK; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "case"                 { return CASE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "catch"                { return CATCH; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "class"                { return CLASS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "const"                { return CONST; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "continue"             { return CONTINUE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "default"              { return DEFAULT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "do"                   { return DO; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "else"                 { return ELSE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "enum"                 { return ENUM; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "extends"              { return EXTENDS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "false"                { return FALSE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "finally"              { return FINALLY; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "for"                  { return FOR; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "if"                   { return IF; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "in"                   { return IN; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "is"                   { return IS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "new"                  { return NEW; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "null"                 { return NULL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "return"               { return RETURN; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "switch"               { return SWITCH; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "this"                 { return THIS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "throw"                { return THROW; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "true"                 { return TRUE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "try"                  { return TRY; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "var"                  { return VAR; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "while"                { return WHILE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "with"                 { return WITH; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "remember"             { return REMEMBER; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "using"                { return USING; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "ref"                  { return REF; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "params"               { return PARAMS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "stackalloc"           { return STACKALLOC; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "out"                  { return OUT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "foreach"              { return FOREACH; }


// BUILT_IN_IDENTIFIER (can be used as normal identifiers)
<YYINITIAL, LONG_TEMPLATE_ENTRY> "abstract"             { return ABSTRACT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "as"                   { return AS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "base"                 { return BASE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "export"               { return EXPORT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "extension"            { return EXTENSION; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "external"             { return EXTERNAL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "implements"           { return IMPLEMENTS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "import"               { return IMPORT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "interface"            { return INTERFACE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "operator"             { return OPERATOR; }
 <YYINITIAL, LONG_TEMPLATE_ENTRY> "partial"                 { return PARTIAL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "sealed"               { return SEALED; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "set"                  { return SET; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "get"                  { return GET; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "static"               { return STATIC; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "when"                 { return WHEN; }

// next are not listed in spec, but they seem to have the same sense as BUILT_IN_IDENTIFIER: somewhere treated as keywords, but can be used as normal identifiers
<YYINITIAL, LONG_TEMPLATE_ENTRY> "of"                   { return OF; }


// Built in types
<YYINITIAL, LONG_TEMPLATE_ENTRY> "float2"    { return TYPE_FLOAT2; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "float3"    { return TYPE_FLOAT3; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "float4"    { return TYPE_FLOAT4; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "float"    { return TYPE_FLOAT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "double"    { return TYPE_DOUBLE; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "int8"    { return TYPE_INT8; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "int16"    { return TYPE_INT16; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "int32"    { return TYPE_INT32; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "int64"    { return TYPE_INT64; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "uint8"    { return TYPE_UINT8; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "uint16"    { return TYPE_UINT16; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "uint32"    { return TYPE_UINT32; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "uint64"    { return TYPE_UINT64; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "string"    { return TYPE_STRING; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "dynamic"    { return TYPE_DYNAMIC; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "object"    { return TYPE_OBJECT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "void"    { return TYPE_VOID; }

<YYINITIAL, LONG_TEMPLATE_ENTRY> "color32"    { return TYPE_COLOR32; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "color64"    { return TYPE_COLOR64; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "color"    { return TYPE_COLOR; }

<YYINITIAL, LONG_TEMPLATE_ENTRY> "char16"    { return TYPE_CHAR16; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "char32"    { return TYPE_CHAR32; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "char"    { return TYPE_CHAR; }

<YYINITIAL, LONG_TEMPLATE_ENTRY> {STANDARD_IDENTIFIER}           { return STANDARD_IDENTIFIER; }

<YYINITIAL, LONG_TEMPLATE_ENTRY> "["                { return LBRACKET; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "]"                { return RBRACKET; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "("                { return LPAREN; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ")"                { return RPAREN; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ";"                { return SEMICOLON; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "-"                { return MINUS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "-="               { return MINUS_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "--"               { return MINUS_MINUS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "+"                { return PLUS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "++"               { return PLUS_PLUS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "+="               { return PLUS_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "/"                { return DIV; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "/="               { return DIV_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "*"                { return MUL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "*="               { return MUL_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "%="               { return MOD_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "%"                { return PERCENT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "~"                { return BIN_NOT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "!"                { return NOT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "=>"               { return RIGHT_ARROW; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "="                { return EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "=="               { return EQ_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "!="               { return NEQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "."                { return DOT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ".."               { return DOT_DOT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "?.."              { return QUEST_DOT_DOT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "..."              { return DOT_DOT_DOT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "...?"             { return DOT_DOT_DOT_QUEST; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ","                { return COMMA; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "::"                { return DOUBLE_COLON; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ":"                { return COLON; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ">"                { return GT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> ">="               { return GT_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "<"                { return LT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "<="               { return LT_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "?"                { return QUESTION_MARK; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "?."               { return ELVIS; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "??"               { return QUEST_QUEST; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "??="              { return QUEST_QUEST_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "|"                { return OR; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "|="               { return OR_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "||"               { return OR_OR; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "||="              { return OR_OR_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "^"                { return XOR; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "^="               { return XOR_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "&"                { return AND; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "&="               { return AND_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "&&"               { return AND_AND; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "&&="              { return AND_AND_EQ; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "@"                { return AT; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> "#"                { return HASH; }


<YYINITIAL, LONG_TEMPLATE_ENTRY> {FLOAT_LITERAL}    { return FLOAT_LITERAL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> {DOUBLE_LITERAL}  { return DOUBLE_LITERAL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> {INTEGER_LITERAL}  { return INTEGER_LITERAL; }
<YYINITIAL, LONG_TEMPLATE_ENTRY> {UNSIGNED_INTEGER_LITERAL}  { return UNSIGNED_INTEGER_LITERAL; }

// raw strings
<YYINITIAL, LONG_TEMPLATE_ENTRY> {RAW_TRIPLE_QUOTED_STRING} { return RAW_TRIPLE_QUOTED_STRING; }

// string start
<YYINITIAL, LONG_TEMPLATE_ENTRY>      \"                  { pushState(QUO_STRING);        return OPEN_QUOTE;    }
<YYINITIAL, LONG_TEMPLATE_ENTRY>      \'                  { pushState(APOS_STRING);       return OPEN_QUOTE;    }
<YYINITIAL, LONG_TEMPLATE_ENTRY>      {THREE_QUO}         { pushState(THREE_QUO_STRING);  return OPEN_QUOTE;    }
// correct string end
<QUO_STRING>                          \"                  { popState();                   return CLOSING_QUOTE; }
<APOS_STRING>                         \'                  { popState();                   return CLOSING_QUOTE; }
<THREE_QUO_STRING>                    {THREE_QUO}         { popState();                   return CLOSING_QUOTE; }
<QUO_STRING, APOS_STRING>             \n                  { popState();                   return WHITE_SPACE;   } // not closed single-line string literal. Do not return BAD_CHARACTER here because red highlighting of bad \n looks awful
// string content
<QUO_STRING>                          ([^\\\"\n\$] | (\\ [^\n]))*   {                return REGULAR_STRING_PART; }
<APOS_STRING>                         ([^\\\'\n\$] | (\\ [^\n]))*   {                return REGULAR_STRING_PART; }
<THREE_QUO_STRING>                    ([^\\\"\$])*                  {                return REGULAR_STRING_PART; }
<THREE_QUO_STRING>                    (\"[^\"]) | (\"\"[^\"])       { yypushback(1); return REGULAR_STRING_PART; } // pushback because we could capture '\' that escapes something
<THREE_QUO_STRING> (\\[^])                       {                return REGULAR_STRING_PART; } // escape sequence
// bad string interpolation (no identifier after '$')
<QUO_STRING, APOS_STRING, THREE_QUO_STRING> \$   { return SHORT_TEMPLATE_ENTRY_START; }
// short string interpolation
<QUO_STRING, APOS_STRING, THREE_QUO_STRING> {SHORT_TEMPLATE_ENTRY}      { pushState(SHORT_TEMPLATE_ENTRY); yypushback(yylength() - 1); return SHORT_TEMPLATE_ENTRY_START;}
// long string interpolation
<QUO_STRING, APOS_STRING, THREE_QUO_STRING> {LONG_TEMPLATE_ENTRY_START} { pushState(LONG_TEMPLATE_ENTRY); return LONG_TEMPLATE_ENTRY_START; }
// Only *this* keyword is itself an expression valid in this position
// *null*, *true* and *false* are also keywords and expression, but it does not make sense to put them
// in a string template for it'd be easier to just type them in without a dollar
<SHORT_TEMPLATE_ENTRY> "this"          { popState(); return THIS; }
<SHORT_TEMPLATE_ENTRY> {IDENTIFIER_NO_DOLLAR}    { popState(); return STANDARD_IDENTIFIER; }

<YYINITIAL, MULTI_LINE_COMMENT_STATE, QUO_STRING, THREE_QUO_STRING, APOS_STRING, SHORT_TEMPLATE_ENTRY, LONG_TEMPLATE_ENTRY> [^] { return BAD_CHARACTER; }