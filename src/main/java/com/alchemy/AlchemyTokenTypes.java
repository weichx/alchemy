// This is a generated file. Not intended for manual editing.
package com.alchemy;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;

public interface AlchemyTokenTypes {

//  IElementType ADDITIVE_EXPRESSION = new AlchemyElementType("ADDITIVE_EXPRESSION");
//  IElementType ADDITIVE_OPERATOR = new AlchemyElementType("ADDITIVE_OPERATOR");
//  IElementType ARGUMENT_DECLARATION = new AlchemyElementType("ARGUMENT_DECLARATION");
//  IElementType ARGUMENT_LIST = new AlchemyElementType("ARGUMENT_LIST");
//  IElementType ARGUMENT_TYPE = new AlchemyElementType("ARGUMENT_TYPE");
//  IElementType ASSIGNMENT_OPERATOR = new AlchemyElementType("ASSIGNMENT_OPERATOR");
//  IElementType ASSIGN_EXPRESSION = new AlchemyElementType("ASSIGN_EXPRESSION");
//  IElementType AS_EXPRESSION = new AlchemyElementType("AS_EXPRESSION");
//  IElementType BINARY_OPERATOR = new AlchemyElementType("BINARY_OPERATOR");
//  IElementType BITWISE_EXPRESSION = new AlchemyElementType("BITWISE_EXPRESSION");
//  IElementType BITWISE_OPERATOR = new AlchemyElementType("BITWISE_OPERATOR");
//  IElementType BLOCK = new AlchemyElementType("BLOCK");
//  IElementType BUILTIN_GENERIC_TYPE = new AlchemyElementType("BUILTIN_GENERIC_TYPE");
//  IElementType CLASS_BODY = new AlchemyElementType("CLASS_BODY");
//  IElementType CLASS_DEFINITION = new AlchemyElementType("CLASS_DEFINITION");
//  IElementType CLASS_MEMBERS = new AlchemyElementType("CLASS_MEMBERS");
//  IElementType CLASS_MEMBER_DEFINITION = new AlchemyElementType("CLASS_MEMBER_DEFINITION");
//  IElementType COLOR_TYPE = new AlchemyElementType("COLOR_TYPE");
//  IElementType COMPARE_EXPRESSION = new AlchemyElementType("COMPARE_EXPRESSION");
//  IElementType COMPONENT_NAME = new AlchemyElementType("COMPONENT_NAME");
//  IElementType CS_LITERAL_EXPR = new AlchemyElementType("CS_LITERAL_EXPR");
//  IElementType EQUALITY_OPERATOR = new AlchemyElementType("EQUALITY_OPERATOR");
//  IElementType EXPRESSION = new AlchemyElementType("EXPRESSION");
//  IElementType FUNCTION_EXPRESSION = new AlchemyElementType("FUNCTION_EXPRESSION");
//  IElementType ID = new AlchemyElementType("ID");
//  IElementType IF_NULL_EXPRESSION = new AlchemyElementType("IF_NULL_EXPRESSION");
//  IElementType IS_EXPRESSION = new AlchemyElementType("IS_EXPRESSION");
//  IElementType LITERAL_EXPRESSION = new AlchemyElementType("LITERAL_EXPRESSION");
//  IElementType LOCAL_VARIABLE_DECLARATION = new AlchemyElementType("LOCAL_VARIABLE_DECLARATION");
//  IElementType LOCAL_VARIABLE_MODIFIER = new AlchemyElementType("LOCAL_VARIABLE_MODIFIER");
//  IElementType LOGIC_AND_EXPRESSION = new AlchemyElementType("LOGIC_AND_EXPRESSION");
//  IElementType LOGIC_OR_EXPRESSION = new AlchemyElementType("LOGIC_OR_EXPRESSION");
//  IElementType LONG_TEMPLATE_ENTRY = new AlchemyElementType("LONG_TEMPLATE_ENTRY");
//  IElementType METADATA = new AlchemyElementType("METADATA");
//  IElementType MULTIPLICATIVE_EXPRESSION = new AlchemyElementType("MULTIPLICATIVE_EXPRESSION");
//  IElementType MULTIPLICATIVE_OPERATOR = new AlchemyElementType("MULTIPLICATIVE_OPERATOR");
//  IElementType NAMED_INT_TYPE = new AlchemyElementType("NAMED_INT_TYPE");
//  IElementType NAMED_U_INT_TYPE = new AlchemyElementType("NAMED_U_INT_TYPE");
//  IElementType NAMESPACE_CHAIN = new AlchemyElementType("NAMESPACE_CHAIN");
//  IElementType PARTIAL = new AlchemyElementType("PARTIAL");
//  IElementType PREFIX_EXPRESSION = new AlchemyElementType("PREFIX_EXPRESSION");
//  IElementType PREFIX_OPERATOR = new AlchemyElementType("PREFIX_OPERATOR");
//  IElementType PRIMITIVE_REFERENCE_TYPE = new AlchemyElementType("PRIMITIVE_REFERENCE_TYPE");
//  IElementType REFERENCE_EXPRESSION = new AlchemyElementType("REFERENCE_EXPRESSION");
//  IElementType RELATIONAL_OPERATOR = new AlchemyElementType("RELATIONAL_OPERATOR");
//  IElementType SHIFT_EXPRESSION = new AlchemyElementType("SHIFT_EXPRESSION");
//  IElementType SHIFT_OPERATOR = new AlchemyElementType("SHIFT_OPERATOR");
//  IElementType SHORT_TEMPLATE_ENTRY = new AlchemyElementType("SHORT_TEMPLATE_ENTRY");
//  IElementType SIMPLE_TYPE = new AlchemyElementType("SIMPLE_TYPE");
//  IElementType SIZED_INT_TYPE = new AlchemyElementType("SIZED_INT_TYPE");
//  IElementType SIZED_U_INT_TYPE = new AlchemyElementType("SIZED_U_INT_TYPE");
//  IElementType STATEMENT = new AlchemyElementType("STATEMENT");
//  IElementType STATEMENT_LIST = new AlchemyElementType("STATEMENT_LIST");
//  IElementType STRING_LITERAL_EXPRESSION = new AlchemyElementType("STRING_LITERAL_EXPRESSION");
//  IElementType SUFFIX_EXPRESSION = new AlchemyElementType("SUFFIX_EXPRESSION");
//  IElementType SUPERCLASS = new AlchemyElementType("SUPERCLASS");
//  IElementType TERNARY_EXPRESSION = new AlchemyElementType("TERNARY_EXPRESSION");
//  IElementType THIS_EXPRESSION = new AlchemyElementType("THIS_EXPRESSION");
//  IElementType TOP_LEVEL_METHOD_DEFINITION = new AlchemyElementType("TOP_LEVEL_METHOD_DEFINITION");
//  IElementType TOP_LEVEL_METHOD_MODIFIER = new AlchemyElementType("TOP_LEVEL_METHOD_MODIFIER");
//  IElementType TOP_LEVEL_METHOD_MODIFIERS = new AlchemyElementType("TOP_LEVEL_METHOD_MODIFIERS");
//  IElementType TYPE = new AlchemyElementType("TYPE");
//  IElementType TYPE_LIST = new AlchemyElementType("TYPE_LIST");
//  IElementType TYPE_PARAMETER = new AlchemyElementType("TYPE_PARAMETER");
//  IElementType TYPE_PARAMETERS = new AlchemyElementType("TYPE_PARAMETERS");
//  IElementType VALUE_EXPRESSION = new AlchemyElementType("VALUE_EXPRESSION");
//  IElementType VARIABLE_DECL = new AlchemyElementType("VARIABLE_DECL");
//  IElementType VAR_EXPR = new AlchemyElementType("VAR_EXPR");
//  IElementType VECTOR_TYPE = new AlchemyElementType("VECTOR_TYPE");

  IElementType ABSTRACT = new AlchemyTokenType("abstract");
  IElementType AND = new AlchemyTokenType("&");
  IElementType AND_AND = new AlchemyTokenType("&&");
  IElementType AND_AND_EQ = new AlchemyTokenType("&&=");
  IElementType AND_EQ = new AlchemyTokenType("&=");
  IElementType AS = new AlchemyTokenType("as");
  IElementType ASSERT = new AlchemyTokenType("assert");
  IElementType AT = new AlchemyTokenType("@");
  IElementType BASE = new AlchemyTokenType("base");
  IElementType BIN_NOT = new AlchemyTokenType("~");
  IElementType BREAK = new AlchemyTokenType("break");
  IElementType CASE = new AlchemyTokenType("case");
  IElementType CATCH = new AlchemyTokenType("catch");
  IElementType CLASS = new AlchemyTokenType("class");
  IElementType CLOSING_QUOTE = new AlchemyTokenType("CLOSING_QUOTE");
  IElementType COLON = new AlchemyTokenType(":");
  IElementType DOUBLE_COLON = new AlchemyTokenType("::");
  IElementType COMMA = new AlchemyTokenType(",");
  IElementType CONST = new AlchemyTokenType("const");
  IElementType CONTINUE = new AlchemyTokenType("continue");
  IElementType DEFAULT = new AlchemyTokenType("default");
  IElementType DIV = new AlchemyTokenType("/");
  IElementType DIV_EQ = new AlchemyTokenType("/=");
  IElementType DO = new AlchemyTokenType("do");
  IElementType DOT = new AlchemyTokenType(".");
  IElementType DOT_DOT = new AlchemyTokenType("..");
  IElementType DOT_DOT_DOT = new AlchemyTokenType("...");
  IElementType DOT_DOT_DOT_QUEST = new AlchemyTokenType("...?");
  IElementType DOUBLE_LITERAL = new AlchemyTokenType("DOUBLE_LITERAL");
  IElementType ELSE = new AlchemyTokenType("else");
  IElementType ENUM = new AlchemyTokenType("enum");
  IElementType EQ = new AlchemyTokenType("=");
  IElementType EQ_EQ = new AlchemyTokenType("==");
  IElementType EXPORT = new AlchemyTokenType("export");
  IElementType EXPRESSION_BODY_DEF = new AlchemyTokenType("=>");
  IElementType EXTENDS = new AlchemyTokenType("extends");
  IElementType EXTENSION = new AlchemyTokenType("extension");
  IElementType EXTERNAL = new AlchemyTokenType("external");
  IElementType FALSE = new AlchemyTokenType("false");
  IElementType FINALLY = new AlchemyTokenType("finally");
  IElementType FOR = new AlchemyTokenType("for");
  IElementType GET = new AlchemyTokenType("get");
  IElementType GT = new AlchemyTokenType(">");
  IElementType GT_EQ = new AlchemyTokenType(">=");
  IElementType HASH = new AlchemyTokenType("#");
  IElementType STANDARD_IDENTIFIER = new AlchemyTokenType("STANDARD_IDENTIFIER");
  IElementType IF = new AlchemyTokenType("if");
  IElementType IMPLEMENTS = new AlchemyTokenType("implements");
  IElementType IMPORT = new AlchemyTokenType("import");
  IElementType IN = new AlchemyTokenType("in");
  IElementType INTERFACE = new AlchemyTokenType("interface");
  IElementType IS = new AlchemyTokenType("is");
  IElementType LBRACE = new AlchemyTokenType("{");
  IElementType LBRACKET = new AlchemyTokenType("[");
  IElementType PARTIAL = new AlchemyTokenType("partial");
  IElementType LONG_TEMPLATE_ENTRY_END = new AlchemyTokenType("LONG_TEMPLATE_ENTRY_END");
  IElementType LONG_TEMPLATE_ENTRY_START = new AlchemyTokenType("LONG_TEMPLATE_ENTRY_START");
  IElementType LPAREN = new AlchemyTokenType("(");
  IElementType LT = new AlchemyTokenType("<");
  IElementType LT_EQ = new AlchemyTokenType("<=");
  IElementType MINUS = new AlchemyTokenType("-");
  IElementType MINUS_EQ = new AlchemyTokenType("-=");
  IElementType MINUS_MINUS = new AlchemyTokenType("--");
  IElementType MUL = new AlchemyTokenType("*");
  IElementType MUL_EQ = new AlchemyTokenType("*=");
  IElementType MOD_EQ = new AlchemyTokenType("%=");
  IElementType MULTI_LINE_COMMENT = new AlchemyTokenType("MULTI_LINE_COMMENT");
  IElementType MULTI_LINE_COMMENT_START = new AlchemyTokenType("MULTI_LINE_COMMENT_START");
  IElementType MULTI_LINE_COMMENT_BODY = new AlchemyTokenType("MULTI_LINE_COMMENT_BODY");
  IElementType MULTI_LINE_COMMENT_END = new AlchemyTokenType("MULTI_LINE_COMMENT_END");
  IElementType NEQ = new AlchemyTokenType("!=");
  IElementType NEW = new AlchemyTokenType("new");
  IElementType NOT = new AlchemyTokenType("!");
  IElementType NULL = new AlchemyTokenType("null");
  IElementType FLOAT_LITERAL = new AlchemyTokenType("FLOAT_LITERAL");
  IElementType INTEGER_LITERAL = new AlchemyTokenType("INTEGER_LITERAL");
  IElementType UNSIGNED_INTEGER_LITERAL = new AlchemyTokenType("UNSIGNED_INTEGER_LITERAL");
  IElementType OF = new AlchemyTokenType("of");
  IElementType OPEN_QUOTE = new AlchemyTokenType("OPEN_QUOTE");
  IElementType OPERATOR = new AlchemyTokenType("operator");
  IElementType OR = new AlchemyTokenType("|");
  IElementType OR_EQ = new AlchemyTokenType("|=");
  IElementType OR_OR = new AlchemyTokenType("||");
  IElementType OR_OR_EQ = new AlchemyTokenType("||=");
  IElementType PLUS = new AlchemyTokenType("+");
  IElementType PLUS_EQ = new AlchemyTokenType("+=");
  IElementType PLUS_PLUS = new AlchemyTokenType("++");
  IElementType QUESTION_MARK = new AlchemyTokenType("?");
  IElementType ELVIS = new AlchemyTokenType("?.");
  IElementType QUEST_DOT_DOT = new AlchemyTokenType("?..");
  IElementType QUEST_QUEST = new AlchemyTokenType("??");
  IElementType QUEST_QUEST_EQ = new AlchemyTokenType("??=");
  IElementType RAW_TRIPLE_QUOTED_STRING = new AlchemyTokenType("RAW_TRIPLE_QUOTED_STRING");
  IElementType RBRACE = new AlchemyTokenType("}");
  IElementType RBRACKET = new AlchemyTokenType("]");
  IElementType REGULAR_STRING_PART = new AlchemyTokenType("REGULAR_STRING_PART");
  IElementType PERCENT = new AlchemyTokenType("%");
  IElementType RETURN = new AlchemyTokenType("return");
  IElementType RPAREN = new AlchemyTokenType(")");
  IElementType SEALED = new AlchemyTokenType("sealed");
  IElementType SEMICOLON = new AlchemyTokenType(";");
  IElementType SET = new AlchemyTokenType("set");
  IElementType SHORT_TEMPLATE_ENTRY_START = new AlchemyTokenType("SHORT_TEMPLATE_ENTRY_START");
  IElementType SINGLE_LINE_COMMENT = new AlchemyTokenType("SINGLE_LINE_COMMENT");
  IElementType STATIC = new AlchemyTokenType("static");
  IElementType SWITCH = new AlchemyTokenType("switch");
  IElementType THIS = new AlchemyTokenType("this");
  IElementType THROW = new AlchemyTokenType("throw");
  IElementType TRUE = new AlchemyTokenType("true");
  IElementType TRY = new AlchemyTokenType("try");
  IElementType VAR = new AlchemyTokenType("var");
  IElementType VOID = new AlchemyTokenType("void");
  IElementType WHEN = new AlchemyTokenType("when");
  IElementType WHILE = new AlchemyTokenType("while");
  IElementType WITH = new AlchemyTokenType("with");
  IElementType XOR = new AlchemyTokenType("^");
  IElementType XOR_EQ = new AlchemyTokenType("^=");

  IElementType TYPE_INT8 = new AlchemyTokenType("int8");
  IElementType TYPE_INT16 = new AlchemyTokenType("int16");
  IElementType TYPE_INT32 = new AlchemyTokenType("int32");
  IElementType TYPE_INT64 = new AlchemyTokenType("int64");

  IElementType TYPE_UINT8 = new AlchemyTokenType("uint8");
  IElementType TYPE_UINT16 = new AlchemyTokenType("uint16");
  IElementType TYPE_UINT32 = new AlchemyTokenType("uint32");
  IElementType TYPE_UINT64 = new AlchemyTokenType("uint64");

  IElementType TYPE_FLOAT = new AlchemyTokenType("float");
  IElementType TYPE_FLOAT2 = new AlchemyTokenType("float2");
  IElementType TYPE_FLOAT3 = new AlchemyTokenType("float3");
  IElementType TYPE_FLOAT4 = new AlchemyTokenType("float4");

  IElementType TYPE_CHAR16 = new AlchemyTokenType("char16");
  IElementType TYPE_CHAR32 = new AlchemyTokenType("char32");
  IElementType TYPE_CHAR = new AlchemyTokenType("char");

  IElementType TYPE_DOUBLE = new AlchemyTokenType("double");
  IElementType TYPE_DYNAMIC = new AlchemyTokenType("dynamic");
  IElementType TYPE_OBJECT = new AlchemyTokenType("object");
  IElementType TYPE_STRING = new AlchemyTokenType("string");
  IElementType TYPE_VOID = new AlchemyTokenType("void");

  IElementType TYPE_COLOR32 = new AlchemyTokenType("color32");
  IElementType TYPE_COLOR64 = new AlchemyTokenType("color64");
  IElementType TYPE_COLOR = new AlchemyTokenType("color");

  IElementType WHITE_SPACE = TokenType.WHITE_SPACE;
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

//  class Factory {
//    public static PsiElement createElement(ASTNode node) {
//      IElementType type = node.getElementType();
//
//      if(node instanceof AlchemyElementType) {
//        AlchemyElementType t = (AlchemyElementType) node;
//
//        switch (t.tokenTypeId) {
//
//          case AdditiveExpression: {
//              return new AlchemyAdditiveExpression(node);
//          }
//
//        }
//
//      }
//
//      if (type == ADDITIVE_EXPRESSION) {
//        return new AlchemyAdditiveExpressionImpl(node);
//      }
//      else if (type == ADDITIVE_OPERATOR) {
//        return new AlchemyAdditiveOperatorImpl(node);
//      }
//      else if (type == ARGUMENT_DECLARATION) {
//        return new AlchemyArgumentDeclarationImpl(node);
//      }
//      else if (type == ARGUMENT_LIST) {
//        return new AlchemyArgumentListImpl(node);
//      }
//      else if (type == ARGUMENT_TYPE) {
//        return new AlchemyArgumentTypeImpl(node);
//      }
//      else if (type == ASSIGNMENT_OPERATOR) {
//        return new AlchemyAssignmentOperatorImpl(node);
//      }
//      else if (type == ASSIGN_EXPRESSION) {
//        return new AlchemyAssignExpressionImpl(node);
//      }
//      else if (type == AS_EXPRESSION) {
//        return new AlchemyAsExpressionImpl(node);
//      }
//      else if (type == BINARY_OPERATOR) {
//        return new AlchemyBinaryOperatorImpl(node);
//      }
//      else if (type == BITWISE_EXPRESSION) {
//        return new AlchemyBitwiseExpressionImpl(node);
//      }
//      else if (type == BITWISE_OPERATOR) {
//        return new AlchemyBitwiseOperatorImpl(node);
//      }
//      else if (type == BLOCK) {
//        return new AlchemyBlockImpl(node);
//      }
//      else if (type == BUILTIN_GENERIC_TYPE) {
//        return new AlchemyBuiltinGenericTypeImpl(node);
//      }
//      else if (type == CLASS_BODY) {
//        return new AlchemyClassBodyImpl(node);
//      }
//      else if (type == CLASS_DEFINITION) {
//        return new AlchemyClassDefinitionImpl(node);
//      }
//      else if (type == CLASS_MEMBERS) {
//        return new AlchemyClassMembersImpl(node);
//      }
//      else if (type == CLASS_MEMBER_DEFINITION) {
//        return new AlchemyClassMemberDefinitionImpl(node);
//      }
//      else if (type == COLOR_TYPE) {
//        return new AlchemyColorTypeImpl(node);
//      }
//      else if (type == COMPARE_EXPRESSION) {
//        return new AlchemyCompareExpressionImpl(node);
//      }
//      else if (type == COMPONENT_NAME) {
//        return new AlchemyComponentNameImpl(node);
//      }
//      else if (type == CS_LITERAL_EXPR) {
//        return new AlchemyCsLiteralExprImpl(node);
//      }
//      else if (type == EQUALITY_OPERATOR) {
//        return new AlchemyEqualityOperatorImpl(node);
//      }
//      else if (type == EXPRESSION) {
//        return new AlchemyExpressionImpl(node);
//      }
//      else if (type == FUNCTION_EXPRESSION) {
//        return new AlchemyFunctionExpressionImpl(node);
//      }
//      else if (type == ID) {
//        return new AlchemyIdImpl(node);
//      }
//      else if (type == IF_NULL_EXPRESSION) {
//        return new AlchemyIfNullExpressionImpl(node);
//      }
//      else if (type == IS_EXPRESSION) {
//        return new AlchemyIsExpressionImpl(node);
//      }
//      else if (type == LITERAL_EXPRESSION) {
//        return new AlchemyLiteralExpressionImpl(node);
//      }
//      else if (type == LOCAL_VARIABLE_DECLARATION) {
//        return new AlchemyLocalVariableDeclarationImpl(node);
//      }
//      else if (type == LOCAL_VARIABLE_MODIFIER) {
//        return new AlchemyLocalVariableModifierImpl(node);
//      }
//      else if (type == LOGIC_AND_EXPRESSION) {
//        return new AlchemyLogicAndExpressionImpl(node);
//      }
//      else if (type == LOGIC_OR_EXPRESSION) {
//        return new AlchemyLogicOrExpressionImpl(node);
//      }
//      else if (type == LONG_TEMPLATE_ENTRY) {
//        return new AlchemyLongTemplateEntryImpl(node);
//      }
//      else if (type == METADATA) {
//        return new AlchemyMetadataImpl(node);
//      }
//      else if (type == MULTIPLICATIVE_EXPRESSION) {
//        return new AlchemyMultiplicativeExpressionImpl(node);
//      }
//      else if (type == MULTIPLICATIVE_OPERATOR) {
//        return new AlchemyMultiplicativeOperatorImpl(node);
//      }
//      else if (type == NAMED_INT_TYPE) {
//        return new AlchemyNamedIntTypeImpl(node);
//      }
//      else if (type == NAMED_U_INT_TYPE) {
//        return new AlchemyNamedUIntTypeImpl(node);
//      }
//      else if (type == NAMESPACE_CHAIN) {
//        return new AlchemyNamespaceChainImpl(node);
//      }
//      else if (type == PARTIAL) {
//        return new AlchemyPartialImpl(node);
//      }
//      else if (type == PREFIX_EXPRESSION) {
//        return new AlchemyPrefixExpressionImpl(node);
//      }
//      else if (type == PREFIX_OPERATOR) {
//        return new AlchemyPrefixOperatorImpl(node);
//      }
//      else if (type == PRIMITIVE_REFERENCE_TYPE) {
//        return new AlchemyPrimitiveReferenceTypeImpl(node);
//      }
//      else if (type == REFERENCE_EXPRESSION) {
//        return new AlchemyReferenceExpressionImpl(node);
//      }
//      else if (type == RELATIONAL_OPERATOR) {
//        return new AlchemyRelationalOperatorImpl(node);
//      }
//      else if (type == SHIFT_EXPRESSION) {
//        return new AlchemyShiftExpressionImpl(node);
//      }
//      else if (type == SHIFT_OPERATOR) {
//        return new AlchemyShiftOperatorImpl(node);
//      }
//      else if (type == SHORT_TEMPLATE_ENTRY) {
//        return new AlchemyShortTemplateEntryImpl(node);
//      }
//      else if (type == SIMPLE_TYPE) {
//        return new AlchemySimpleTypeImpl(node);
//      }
//      else if (type == SIZED_INT_TYPE) {
//        return new AlchemySizedIntTypeImpl(node);
//      }
//      else if (type == SIZED_U_INT_TYPE) {
//        return new AlchemySizedUIntTypeImpl(node);
//      }
//      else if (type == STATEMENT) {
//        return new AlchemyStatementImpl(node);
//      }
//      else if (type == STATEMENT_LIST) {
//        return new AlchemyStatementListImpl(node);
//      }
//      else if (type == STRING_LITERAL_EXPRESSION) {
//        return new AlchemyStringLiteralExpressionImpl(node);
//      }
//      else if (type == SUFFIX_EXPRESSION) {
//        return new AlchemySuffixExpressionImpl(node);
//      }
//      else if (type == SUPERCLASS) {
//        return new AlchemySuperclassImpl(node);
//      }
//      else if (type == TERNARY_EXPRESSION) {
//        return new AlchemyTernaryExpressionImpl(node);
//      }
//      else if (type == THIS_EXPRESSION) {
//        return new AlchemyThisExpressionImpl(node);
//      }
//      else if (type == TOP_LEVEL_METHOD_DEFINITION) {
//        return new AlchemyTopLevelMethodDefinitionImpl(node);
//      }
//      else if (type == TOP_LEVEL_METHOD_MODIFIER) {
//        return new AlchemyTopLevelMethodModifierImpl(node);
//      }
//      else if (type == TOP_LEVEL_METHOD_MODIFIERS) {
//        return new AlchemyTopLevelMethodModifiersImpl(node);
//      }
//      else if (type == TYPE) {
//        return new AlchemyTypeImpl(node);
//      }
//      else if (type == TYPE_LIST) {
//        return new AlchemyTypeListImpl(node);
//      }
//      else if (type == TYPE_PARAMETER) {
//        return new AlchemyTypeParameterImpl(node);
//      }
//      else if (type == TYPE_PARAMETERS) {
//        return new AlchemyTypeParametersImpl(node);
//      }
//      else if (type == VALUE_EXPRESSION) {
//        return new AlchemyValueExpressionImpl(node);
//      }
//      else if (type == VARIABLE_DECL) {
//        return new AlchemyVariableDeclImpl(node);
//      }
//      else if (type == VAR_EXPR) {
//        return new AlchemyVarExprImpl(node);
//      }
//      else if (type == VECTOR_TYPE) {
//        return new AlchemyVectorTypeImpl(node);
//      }
//      throw new AssertionError("Unknown element type: " + type);
//    }
//  }
}
