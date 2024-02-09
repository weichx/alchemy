package com.alchemy;

import com.alchemy.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

public class AlchemyElementFactory {

    public static PsiElement createElement(ASTNode node) {

        IElementType type = node.getElementType();

        if(type instanceof AlchemyElementType elementType) {
            return switch (elementType.id) {
                case BLOCK -> new AlchemyBlock(node);
                case LOCAL_VARIABLE_DECLARATION -> new AlchemyLocalVariableDeclaration(node);
                case THIS_EXPRESSION -> new AlchemyThisExpression(node);
                case NAMESPACE_PART -> new AlchemyNamespacePart(node);
                case NAMESPACE_CHAIN -> new AlchemyNamespaceChain(node);
                case TYPE_ARGUMENT_LIST -> new AlchemyTypeArgumentList(node);
                case BASE_TYPE -> new AlchemyBaseType(node);
                case ARRAY_SPECIFIER -> new AlchemyArraySpecifier(node);
                case TYPE_PATH -> new AlchemyTypePath(node);
                case CUSTOM_TYPE_NAME -> new AlchemyCustomTypeName(node);
                case BUILTIN_VALUE_TYPE_NAME -> new AlchemyBuiltInValueType(node);
                case BUILTIN_REFERENCE_TYPE_NAME -> new AlchemyBuiltInReferenceType(node);
                case ASSIGNMENT_EXPRESSION -> new AlchemyAssignmentExpression(node);
                case CAST_EXPRESSION -> new AlchemyCastExpression(node);
                case TERNARY_EXPRESSION -> new AlchemyTernaryExpression(node);
                case NULL_COALESCING_EXPRESSION -> new AlchemyNullCoalescingExpression(node);
                case CONDITIONAL_OR_EXPRESSION -> new AlchemyConditionalOrExpression(node);
                case CONDITIONAL_AND_EXPRESSION -> new AlchemyConditionalAndExpression(node);
                case EQUALITY_EXPRESSION -> new AlchemyEqualityExpression(node);
                case RELATIONAL_EXPRESSION -> new AlchemyRelationalExpression(node);
                case IS_EXPRESSION_TYPE -> new AlchemyIsExpression(node);
                case AS_EXPRESSION -> new AlchemyAsExpression(node);
                case BINARY_EXPRESSION -> new AlchemyBinaryExpression(node);
                case BINARY_OPERATOR -> new AlchemyBinaryOperator(node);
                case NUMERIC_LITERAL -> new AlchemyNumericLiteral(node);
                case DEFAULT_LITERAL -> new AlchemyDefaultLiteral(node);
                case NULL_LITERAL -> new AlchemyNullLiteral(node);
                case CHAR_LITERAL -> new AlchemyCharLiteral(node);
                case BOOL_LITERAL -> new AlchemyBoolLiteral(node);
                case BUILTIN_TYPE -> new AlchemyBuiltinType(node);
                case PRIMARY_IDENTIFIER -> new AlchemyPrimaryIdentifier(node);
                case PRIMARY_EXPRESSION -> new AlchemyPrimaryExpression(node);
                case INDEXER_ARGUMENT -> new AlchemyIndexerArgument(node);
                case UNARY_EXPRESSION -> new AlchemyUnaryExpression(node);
                case EQUALITY_OPERATOR -> new AlchemyEqualityOperator(node);
                case CASE_GUARD -> new AlchemyCaseGuard(node);
                case SWITCH_EXPRESSION_ARM -> new AlchemySwitchExpressionArm(node);
                case SWITCH_EXPRESSION_ARMS -> new AlchemySwitchExpressionArms(node);
                case SWITCH_EXPRESSION -> new AlchemySwitchExpression(node);
                case THROW_EXPRESSION -> new AlchemyThrowableExpression(node);
                case CONSTANT_EXPRESSION -> new AlchemyConstantExpression(node);
                case TYPE_PARAMETER_LIST -> new AlchemyTypeParameterList(node);
                case PARAMETER_ARRAY -> new AlchemyParameterArray(node);
                case PARAMETER_MODIFIER -> new AlchemyParameterModifier(node);
                case ARG_DECLARATION -> new AlchemyArgDeclaration(node);
                case FIXED_PARAMETER -> new AlchemyFixedParameter(node);
                case FORMAL_PARAMETER_LIST -> new AlchemyFormalParameterList(node);
                case FIXED_PARAMETERS -> new AlchemyFixedParameters(node);
                case LOCAL_FUNCTION_DECLARATION -> new AlchemyLocalFunctionDeclaration(node);
                case BREAK_STATEMENT -> new AlchemyBreakStatement(node);
                case CONTINUE_STATEMENT -> new AlchemyContinueStatement(node);
                case IF_STATEMENT -> new AlchemyIfStatement(node);
                case SWITCH_STATEMENT -> new AlchemySwitchStatement(node);
                case SWITCH_LABEL -> new AlchemySwitchLabel(node);
                case SWITCH_DEFAULT_LABEL -> new AlchemySwitchDefaultLabel(node);
                case SWITCH_SECTION -> new AlchemySwitchSection(node);
                case WHILE_LOOP -> new AlchemyWhileLoop(node);
                case DO_WHILE_LOOP -> new AlchemyDoWhileLoop(node);
                case FOR_LOOP_INITIALIZER -> new AlchemyForLoopInitializer(node);
                case FOR_LOOP_ITERATOR -> new AlchemyForLoopIterator(node);
                case FOR_LOOP_CONDITION -> new AlchemyForLoopCondition(node);
                case FOR_LOOP -> new AlchemyForLoop(node);
                case FOR_EACH_LOOP -> new AlchemyForEachLoop(node);
                case RETURN_STATEMENT -> new AlchemyReturnStatement(node);
                case EXPRESSION_STATEMENT -> new AlchemyExpressionStatement(node);
            };
        }
        else {
            throw new AssertionError("Unknown element type: " + type);
        }

    }

}