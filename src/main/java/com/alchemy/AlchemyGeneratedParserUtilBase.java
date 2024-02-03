package com.alchemy;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

import static com.alchemy.AlchemyTokenTypes.*;

public class AlchemyGeneratedParserUtilBase extends GeneratedParserUtilBase {

    public static boolean nonStrictID(PsiBuilder builder_, int level_) {
        final PsiBuilder.Marker marker_ = builder_.mark();
        final boolean result_ = consumeToken(builder_, IDENTIFIER);
        if (result_) {
            marker_.done(com.alchemy.AlchemyTokenTypes.ID);
            return true;
        }
//        else if (Boolean.TRUE == builder_.getUserData(INSIDE_SYNC_OR_ASYNC_FUNCTION) && IDENTIFIERS_FORBIDDEN_INSIDE_ASYNC_FUNCTIONS.contains(builder_.getTokenType())) {
//            marker_.rollbackTo();
//            return false;
//        }
//        else if (DartTokenTypesSets.BUILT_IN_IDENTIFIERS.contains(builder_.getTokenType())) {
//            builder_.advanceLexer();
//            marker_.done(ID);
//            return true;
//        }
        marker_.rollbackTo();
        return false;
    }

    public static boolean gtGt(PsiBuilder builder_, int level_) {
        final PsiBuilder.Marker marker_ = builder_.mark();
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        marker_.collapse(GT_GT);
        return true;
    }

    public static boolean gtGtGt(PsiBuilder builder_, int level_) {
        final PsiBuilder.Marker marker_ = builder_.mark();
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        marker_.collapse(GT_GT_GT);
        return true;
    }

    public static boolean gtEq(PsiBuilder builder_, int level_) {
        final PsiBuilder.Marker marker_ = builder_.mark();
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, EQ)) {
            marker_.rollbackTo();
            return false;
        }
        marker_.collapse(GT_EQ);
        return true;
    }

    public static boolean gtGtEq(PsiBuilder builder_, int level_) {
        final PsiBuilder.Marker marker_ = builder_.mark();
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, EQ)) {
            marker_.rollbackTo();
            return false;
        }
        marker_.collapse(GT_GT_EQ);
        return true;
    }

    public static boolean gtGtGtEq(PsiBuilder builder_, int level_) {
        final PsiBuilder.Marker marker_ = builder_.mark();
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, GT)) {
            marker_.rollbackTo();
            return false;
        }
        if (!consumeToken(builder_, EQ)) {
            marker_.rollbackTo();
            return false;
        }
        marker_.collapse(GT_GT_GT_EQ);
        return true;
    }

}