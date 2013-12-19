package org.web4thejob.diff;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class HtmlDiff {
    private static final String DIV_INSERTED = "<div style=\"background:#e6ffe6;\"><ins></ins></div>";
    private static final String DIV_DELETED = "<div style=\"background:#ffe6e6;\"><del></del></div>";
    private CombinedHtml combinedHtml = new CombinedHtml();

    private static Element copyAll(Element src, Element trg) {
        trg.tagName(src.tagName());
        copyAttributes(src, trg);
        copyText(src, trg);
        return trg;
    }

    private static Element copyAttributes(Element src, Element trg) {
        for (Attribute a : src.attributes()) {
            trg.attr(a.getKey(), a.getValue());
        }
        return trg;
    }

    private static Element copyText(Element src, Element trg) {
        trg.text(src.ownText());
        return trg;
    }

    public Document buildDiff(File oldFile, File newFile) throws IOException {
        Document doc;

        doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(oldFile));
        for (Element e : doc.body().children()) {
            combinedHtml.addNewElement(e);
            traverseDocument(e);
        }

        doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(newFile));
        for (Element e : doc.body().children()) {
            combinedHtml.addOldElement(e);
            traverseDocument(e);
        }

        List<Element> inserted = new ArrayList<>();
        List<Element> deleted = new ArrayList<>();


        doc = Jsoup.parseBodyFragment("");
        doc.head().appendElement("meta").attr("charset", "utf-8");
        for (String key : combinedHtml.getKeys()) {
            Element finalElement = playBack(key, doc.body());
            CombinedHtml.Combination combination = combinedHtml.get(key);
            Element oldElement = combination.getOldElement();
            Element newElement = combination.getNewElement();

            if (newElement == null) {
                deleted.add(copyAll(oldElement, finalElement));//.wrap(DELETED_SPAN);
            } else if (oldElement == null) {
                inserted.add(copyAll(newElement, finalElement));//.wrap(INSERTED_SPAN);
            } else {
                if (combination.equals()) {
                    copyAll(newElement, finalElement);
                } else if (combination.equalTag() && !combination.equalText()) {
                    copyAttributes(newElement, finalElement);
                    String oldText = oldElement.ownText();
                    String newText = newElement.ownText();

                    DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
                    LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diff_main(oldText, newText);
                    diffMatchPatch.diff_cleanupSemantic(diffs);

                    StringBuilder html = new StringBuilder();
                    for (DiffMatchPatch.Diff diff : diffs) {
                        String text = diff.text;
                        switch (diff.operation) {
                            case INSERT:
                                html.append("<ins style=\"background:#e6ffe6;\">").append(text)
                                        .append("</ins>");
                                break;
                            case DELETE:
                                html.append("<del style=\"background:#ffe6e6;\">").append(text)
                                        .append("</del>");
                                break;
                            case EQUAL:
                                html.append(text);
                                break;
                        }
                    }

                    finalElement.append(html.toString());

                } else {
                    deleted.add(copyAll(oldElement, finalElement));//.wrap(DELETED_SPAN);
                    inserted.add(copyAll(newElement, finalElement));//.wrap(INSERTED_SPAN);
                }
            }

        }

        for (Element e : inserted) {
            e.wrap(DIV_INSERTED);
        }
        for (Element e : deleted) {
            e.wrap(DIV_DELETED);
        }

        return doc;
    }

    private Element playBack(String key, Element parent) {
        Element child = null;
        StringTokenizer tokens = new StringTokenizer(key, "_>");
        while (tokens.hasMoreTokens()) {
            String index = tokens.nextToken();
            String tag = tokens.nextToken();
            String id = index + "_" + tag;
            child = null;

            child = parent.getElementsByAttributeValue("diffid", id).first();
            if (child != null && !child.parent().equals(parent)) {
                child = null;
            }

            if (child == null) {
                child = parent.appendElement(tag);
                child.attr("diffid", id);
            }
            parent = child;
        }

        return child;
    }

    private void traverseDocument(Element parent) {
        for (Element e : parent.children()) {
            combinedHtml.addNewElement(e);
            traverseDocument(e);
        }
    }

}
