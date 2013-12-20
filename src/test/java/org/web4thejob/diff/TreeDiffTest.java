package org.web4thejob.diff;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class TreeDiffTest {

    @Test
    public void mainTest() throws IOException {
        File current = new File("C:\\Documents and " +
                "Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test\\resources\\set1" +
                "\\current.html");

        File previous = new File("C:\\Documents and " +
                "Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test\\resources\\set1" +
                "\\previous.html");

        TreeDiffResult result = TreeDiff.buildDiffPage(previous, current);

        StringBuilder out = new StringBuilder();
        out.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        out.append("<!DOCTYPE html PUBLIC>");
        out.append("<html>");
        out.append("<head>");
        out.append("<meta charset=\"utf-8\"/>");
        out.append("</head>");
        out.append("<body>");
        out.append(diff_prettyHtml(result));
        out.append("</body>");
        out.append("</html>");

        FileUtils.writeStringToFile(new File("C:\\Documents and Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test" +
                "\\resources\\set1\\result.html"), out.toString(), Charset.forName("UTF-8"));
    }

    public String diff_prettyHtml(TreeDiffResult result) {
        Map<String, TreeDiff.ElementWrapper> wrappers = result.elementWrappers;
        LinkedList<DiffMatchPatch.Diff> diffs = result.diffs;
        StringBuilder html = new StringBuilder();
        for (DiffMatchPatch.Diff aDiff : diffs) {
            String text = aDiff.text;
            for (String line : text.split("\n")) {
                Element e = wrappers.get(line).element;
                String actualText = e.ownText();
                switch (aDiff.operation) {
                    case INSERT:
                        html.append("<div style=\"background:#e6ffe6;\">").append(e.outerHtml()).append("</div>");
                        break;
                    case DELETE:
                        html.append("<div style=\"background:#ffe6e6;\">").append(e.outerHtml()).append("</div>");
                        break;
                    case EQUAL:
                        html.append(e.outerHtml());
                        break;
                }
            }
        }
        return html.toString();
    }

    @Test
    public void renderTest() {

        Document doc = Jsoup.parseBodyFragment("");

        Element parent = doc.body();

        populate(parent, "p_0>span_0");
        populate(parent, "p_0>span_1");
        populate(parent, "p_0>span_0>span_0");
        populate(parent, "p_1>span_0");


        System.out.println(doc.outerHtml());

    }

    private void populate(Element parent, String path) {

        StringTokenizer tokens = new StringTokenizer(path, "_>");
        while (tokens.hasMoreTokens()) {
            String tag = tokens.nextToken();
            int index = Integer.valueOf(tokens.nextToken());

            Element child = null;
            if (parent.children().size() > index) {
                child = parent.children().get(index);
            }

            if (child == null) {
                child = parent.appendElement(tag);
            }
            parent = child;

        }

    }


    @Test
    public void doHtmlDiff() throws IOException {
        HtmlDiff htmlDiff = new HtmlDiff();

        File current = new File("C:\\Documents and Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test\\resources" +
                "\\set1\\current.html");

        File previous = new File("C:\\Documents and Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test\\resources" +
                "\\set1\\previous.html");


        FileUtils.writeStringToFile(new File("C:\\Documents and Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test" +
                "\\resources\\set1\\result.html"), htmlDiff.buildDiff(previous, current).outerHtml(),
                Charset.forName("UTF-8"));

    }

}
