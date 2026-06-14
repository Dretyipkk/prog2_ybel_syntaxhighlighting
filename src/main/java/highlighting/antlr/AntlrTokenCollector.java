package highlighting.antlr;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaColours;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public class AntlrTokenCollector extends SyntaxHighlighter {

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    MiniJavaLexer lexer = new MiniJavaLexer(CharStreams.fromString(text));
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    tokenStream.fill();

    List<HighlightRegion> regions = new ArrayList<>();
    List<Token> tokens = tokenStream.getTokens();

    for (int i = 0; i < tokens.size(); i++) {
      Token token = tokens.get(i);

      if (token.getType() == Token.EOF) {
        continue;
      }

      Color colour = colourForToken(token.getType());

      if (colour != null) {
        regions.add(toRegion(token, colour));
      }

      if (token.getType() == MiniJavaLexer.AT) {
        regions.add(toRegion(token, MiniJavaColours.ANNOTATION_COLOUR));

        if (i + 1 < tokens.size()) {
          Token nextToken = tokens.get(i + 1);

          if (nextToken.getType() == MiniJavaLexer.IDENTIFIER) {
            regions.add(toRegion(nextToken, MiniJavaColours.ANNOTATION_COLOUR));
          }
        }
      }
    }

    return regions;
  }

  private HighlightRegion toRegion(Token token, Color colour) {
    int start = token.getStartIndex();
    int end = token.getStopIndex() + 1;

    return new HighlightRegion(start, end, colour);
  }

  private Color colourForToken(int tokenType) {
    return switch (tokenType) {
      case MiniJavaLexer.PACKAGE,
          MiniJavaLexer.IMPORT,
          MiniJavaLexer.CLASS,
          MiniJavaLexer.PUBLIC,
          MiniJavaLexer.PRIVATE,
          MiniJavaLexer.FINAL,
          MiniJavaLexer.RETURN,
          MiniJavaLexer.NULL,
          MiniJavaLexer.NEW,
          MiniJavaLexer.IF,
          MiniJavaLexer.ELSE,
          MiniJavaLexer.WHILE,
          MiniJavaLexer.EXTENDS,
          MiniJavaLexer.IMPLEMENTS ->
          MiniJavaColours.KEYWORD_COLOUR;

      case MiniJavaLexer.STRING_LITERAL -> MiniJavaColours.STRING_LITERAL_COLOUR;

      case MiniJavaLexer.CHAR_LITERAL -> MiniJavaColours.CHAR_LITERAL_COLOUR;

      case MiniJavaLexer.LINE_COMMENT -> MiniJavaColours.LINE_COMMENT_COLOUR;

      case MiniJavaLexer.BLOCK_COMMENT -> MiniJavaColours.BLOCK_COMMENT_COLOUR;

      case MiniJavaLexer.JAVADOC_COMMENT -> MiniJavaColours.JAVADOC_COMMENT_COLOUR;

      default -> null;
    };
  }
}
