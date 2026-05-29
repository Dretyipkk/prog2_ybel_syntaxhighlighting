package highlighting.presets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import highlighting.core.HighlightRegion;
import highlighting.regex.Token;
import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  @Test
  void stringToken_givenTextWithString_whenTesting_thenFindsStringRegion() {
    // given
    Token stringToken = tokenWithColour(MiniJavaColours.STRING_LITERAL_COLOUR);
    String text = "String name = \"Anton\";";

    // when
    List<HighlightRegion> result = stringToken.test(text);

    // then
    assertEquals(1, result.size());
    assertEquals(14, result.get(0).start());
    assertEquals(21, result.get(0).end());
    assertEquals(MiniJavaColours.STRING_LITERAL_COLOUR, result.get(0).colour());
  }

  @Test
  void lineCommentToken_givenTextWithLineComment_whenTesting_thenFindsCommentRegion() {
    // given
    Token lineCommentToken = tokenWithColour(MiniJavaColours.LINE_COMMENT_COLOUR);
    String text = "int x = 5; // Kommentar";

    // when
    List<HighlightRegion> result = lineCommentToken.test(text);

    // then
    assertEquals(1, result.size());
    assertEquals(11, result.get(0).start());
    assertEquals(23, result.get(0).end());
    assertEquals(MiniJavaColours.LINE_COMMENT_COLOUR, result.get(0).colour());
  }

  private Token tokenWithColour(Color colour) {
    return MiniJavaTokens.defaultTokens().stream()
        .filter(token -> token.colour().equals(colour))
        .findFirst()
        .orElseThrow();
  }

  @Test
  void keywordToken_givenTextWithSeveralKeywords_whenTesting_thenFindsAllKeywordRegions() {
    // given
    Token keywordToken = tokenWithColour(MiniJavaColours.KEYWORD_COLOUR);
    String text = "public class Test { return null; }";

    // when
    List<HighlightRegion> result = keywordToken.test(text);

    // then
    assertEquals(4, result.size());

    assertEquals(0, result.get(0).start());
    assertEquals(6, result.get(0).end());

    assertEquals(7, result.get(1).start());
    assertEquals(12, result.get(1).end());

    assertEquals(20, result.get(2).start());
    assertEquals(26, result.get(2).end());

    assertEquals(27, result.get(3).start());
    assertEquals(31, result.get(3).end());

    for (HighlightRegion region : result) {
      assertEquals(MiniJavaColours.KEYWORD_COLOUR, region.colour());
    }
  }

  @Test
  void keywordToken_givenKeywordsInsideOtherWords_whenTesting_thenFindsNoRegions() {
    // given
    Token keywordToken = tokenWithColour(MiniJavaColours.KEYWORD_COLOUR);
    String text = "publicName myclass returnValue newObject";

    // when
    List<HighlightRegion> result = keywordToken.test(text);

    // then
    assertEquals(0, result.size());
  }

  @Test
  void annotationToken_givenTextWithAnnotations_whenTesting_thenFindsAnnotationRegions() {
    // given
    Token annotationToken = tokenWithColour(MiniJavaColours.ANNOTATION_COLOUR);
    String text = "@Override @Test";

    // when
    List<HighlightRegion> result = annotationToken.test(text);

    // then
    assertEquals(2, result.size());

    assertEquals(0, result.get(0).start());
    assertEquals(9, result.get(0).end());

    assertEquals(10, result.get(1).start());
    assertEquals(15, result.get(1).end());

    for (HighlightRegion region : result) {
      assertEquals(MiniJavaColours.ANNOTATION_COLOUR, region.colour());
    }
  }

  @Test
  void charToken_givenTextWithCharLiterals_whenTesting_thenFindsCharRegions() {
    // given
    Token charToken = tokenWithColour(MiniJavaColours.CHAR_LITERAL_COLOUR);
    String text = "char a = 'x'; char b = '\\n';";

    // when
    List<HighlightRegion> result = charToken.test(text);

    // then
    assertEquals(2, result.size());

    assertEquals(9, result.get(0).start());
    assertEquals(12, result.get(0).end());

    assertEquals(23, result.get(1).start());
    assertEquals(27, result.get(1).end());

    for (HighlightRegion region : result) {
      assertEquals(MiniJavaColours.CHAR_LITERAL_COLOUR, region.colour());
    }
  }

  @Test
  void blockCommentToken_givenMultilineBlockComment_whenTesting_thenFindsBlockCommentRegion() {
    // given
    Token blockCommentToken = tokenWithColour(MiniJavaColours.BLOCK_COMMENT_COLOUR);
    String text = "int x = 0; /* block\ncomment */ int y = 1;";
    String expected = "/* block\ncomment */";

    // when
    List<HighlightRegion> result = blockCommentToken.test(text);

    // then
    assertEquals(1, result.size());

    int expectedStart = text.indexOf(expected);
    int expectedEnd = expectedStart + expected.length();

    assertEquals(expectedStart, result.get(0).start());
    assertEquals(expectedEnd, result.get(0).end());
    assertEquals(MiniJavaColours.BLOCK_COMMENT_COLOUR, result.get(0).colour());
  }

  @Test
  void javadocCommentToken_givenMultilineJavadoc_whenTesting_thenFindsJavadocRegion() {
    // given
    Token javadocToken = tokenWithColour(MiniJavaColours.JAVADOC_COMMENT_COLOUR);
    String text = "/**\n * Javadoc\n */\npublic class Test {}";
    String expected = "/**\n * Javadoc\n */";

    // when
    List<HighlightRegion> result = javadocToken.test(text);

    // then
    assertEquals(1, result.size());

    int expectedStart = text.indexOf(expected);
    int expectedEnd = expectedStart + expected.length();

    assertEquals(expectedStart, result.get(0).start());
    assertEquals(expectedEnd, result.get(0).end());
    assertEquals(MiniJavaColours.JAVADOC_COMMENT_COLOUR, result.get(0).colour());
  }
}
