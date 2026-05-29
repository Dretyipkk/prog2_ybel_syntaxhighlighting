package highlighting.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.Test;

class RegexHighlighterTest {

  @Test
  void collectMatches_givenTextWithDifferentTokens_whenCollecting_thenFindsAllRegions() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "public class Test { String name = \"Anton\"; // Kommentar\n}";

    // when
    List<HighlightRegion> result = highlighter.collectMatches(text);

    // then
    assertTrue(containsRegion(result, text, "public", MiniJavaColours.KEYWORD_COLOUR));
    assertTrue(containsRegion(result, text, "class", MiniJavaColours.KEYWORD_COLOUR));
    assertTrue(containsRegion(result, text, "\"Anton\"", MiniJavaColours.STRING_LITERAL_COLOUR));
    assertTrue(containsRegion(result, text, "// Kommentar", MiniJavaColours.LINE_COMMENT_COLOUR));
  }

  @Test
  void resolveConflicts_givenOverlappingRegions_whenResolving_thenKeepsFirstRegion() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();

    HighlightRegion comment = new HighlightRegion(0, 15, MiniJavaColours.LINE_COMMENT_COLOUR);
    HighlightRegion keywordPublic = new HighlightRegion(3, 9, MiniJavaColours.KEYWORD_COLOUR);
    HighlightRegion keywordClass = new HighlightRegion(10, 15, MiniJavaColours.KEYWORD_COLOUR);
    HighlightRegion string = new HighlightRegion(16, 23, MiniJavaColours.STRING_LITERAL_COLOUR);

    List<HighlightRegion> regions = List.of(comment, keywordPublic, keywordClass, string);

    // when
    List<HighlightRegion> result = highlighter.resolveConflicts(regions);

    // then
    assertEquals(2, result.size());
    assertEquals(comment, result.get(0));
    assertEquals(string, result.get(1));
  }

  private boolean containsRegion(
      List<HighlightRegion> regions, String text, String expectedText, Color expectedColour) {
    int expectedStart = text.indexOf(expectedText);
    int expectedEnd = expectedStart + expectedText.length();

    return regions.stream()
        .anyMatch(
            region ->
                region.start() == expectedStart
                    && region.end() == expectedEnd
                    && region.colour().equals(expectedColour));
  }

  @Test
  void
      computeRegions_givenKeywordsInsideCommentAndString_whenComputing_thenRemovesKeywordConflicts() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "// public class\nString s = \"public\";";

    // when
    List<HighlightRegion> result = highlighter.computeRegions(text);

    // then
    assertTrue(
        containsRegion(result, text, "// public class", MiniJavaColours.LINE_COMMENT_COLOUR));
    assertTrue(containsRegion(result, text, "\"public\"", MiniJavaColours.STRING_LITERAL_COLOUR));

    long keywordCount =
        result.stream()
            .filter(region -> region.colour().equals(MiniJavaColours.KEYWORD_COLOUR))
            .count();

    assertEquals(0, keywordCount);
  }
}
