package org.emberjs.lang;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.highlighting.JSHighlighter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.emberjs.lang.lexer.EmberJSLexer;
import org.emberjs.lang.lexer.EmberJSTokenTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Dennis.Ushakov
 */
public class EmberJSSyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {
    @NotNull
    protected SyntaxHighlighter createHighlighter() {
        return new AngularJSSyntaxHighlighter();
    }

    private static class AngularJSSyntaxHighlighter extends JSHighlighter {
        private final Map<IElementType, TextAttributesKey> myKeysMap = new THashMap<IElementType, TextAttributesKey>();

        public AngularJSSyntaxHighlighter() {
            super(EmberJSLanguage.INSTANCE.getOptionHolder());
            myKeysMap.put(JSTokenTypes.AS_KEYWORD, DefaultLanguageHighlighterColors.KEYWORD);
            myKeysMap.put(EmberJSTokenTypes.TRACK_BY_KEYWORD, DefaultLanguageHighlighterColors.KEYWORD);
        }

        @NotNull
        public TextAttributesKey[] getTokenHighlights(final IElementType tokenType) {
            if (myKeysMap.containsKey(tokenType)) {
                return pack(myKeysMap.get(tokenType));
            }
            return super.getTokenHighlights(tokenType);
        }

        @NotNull
        @Override
        public Lexer getHighlightingLexer() {
            return new EmberJSLexer();
        }
    }
}
