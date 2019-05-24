package com.allendowney.thinkdast;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     *
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     *
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        //String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        String source = "https://en.wikipedia.org/wiki/Science";

        testConjecture(destination, source, 10);
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    public static void testConjecture(String destination, String source, int limit) throws IOException {
        if (source.equals(destination)) {
            System.out.println("たどり着いた！ " + source);
            return;
        }

        if (limit <= 0) {
            System.out.println("回数上限");
            return;
        }

        if (visited.contains(source)) {
            System.out.println("もう見た " + source);
            return;
        }

        visited.add(source);
        System.out.println(source);

        Elements paragraphs = wf.fetchWikipedia(source);

        // 動作確認
        //for (Element p : paragraphs) System.out.println(p);

        String validLink = getValidLinkFromParagraphs(source, paragraphs);

        if (validLink == null) {
            System.out.println("リンクなし");
            return;
        }

        testConjecture(destination, validLink, limit - 1);
    }

    private static String getValidLinkFromParagraphs(String source, Elements paragraphs) {
        URI sourceUri = URI.create(source);
        int parenthesisDepth = 0;

        for (Element p : paragraphs) {
            for (Node node : new WikiNodeIterable(p)) {
                if (node instanceof TextNode) {
                    // 括弧をチェックしていく
                    String nodeText = ((TextNode)node).text();
                    for (int i = 0; i < nodeText.length(); i++) {
                        char c = nodeText.charAt(i);
                        if (c == '(' || c == '[') parenthesisDepth++;
                        else if (c == ')' || c == ']') parenthesisDepth--;
                    }
                    assert parenthesisDepth >= 0;
                } else if (parenthesisDepth == 0 && node instanceof Element) {
                    Element element = (Element)node;
                    // a タグであり、親や子孫に斜体表示を持たないことを確認
                    if (element.tagName().equalsIgnoreCase("a") // a タグ
                        && !element.classNames().contains("new") // red link でない
                        && !hasItalicParent(element)) // 斜体でない
                    {
                        String href = element.attr("href");
                        URI linkUri = sourceUri.resolve(href);
                        if (sourceUri.getHost().equals(linkUri.getHost()) // 同一ホスト(en.wikipedia.org)
                            && !sourceUri.getPath().equals(linkUri.getPath())) // 違うページ
                        {
                            return linkUri.toString();
                        }
                    }
                }
            }
        }

        return null; // 見つからなかった
    }

    private static boolean hasItalicParent(Element element) {
        for (Element parent : element.parents()) {
            if (isItalicElement(element))
                return true;
        }
        return false;
    }

    private static boolean isItalicElement(Element element) {
        String tagName = element.tagName();
        return tagName.equalsIgnoreCase("em") || tagName.equalsIgnoreCase("i");
    }
}
