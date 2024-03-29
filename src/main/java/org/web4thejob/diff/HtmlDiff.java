package org.web4thejob.diff;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;

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
    private static final String DIV_INSERTED = "<ins style=\"background:#e6ffe6;\"><ins>";
    private static final String DIV_DELETED = "<del style=\"background:#ffe6e6;\"><del>";
    private CombinedHtml combinedHtml = new CombinedHtml();


    public Document buildDiff(File oldFile, File newFile) throws IOException {
        Document doc;

        doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(oldFile));
        for (Node e : doc.body().childNodes()) {
            combinedHtml.addOldElement(e);
            traverseOldDocument(e);
        }

        doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(newFile));
        for (Element e : doc.body().children()) {
            combinedHtml.addNewElement(e);
            traverseNewDocument(e);
        }

        List<Node> inserted = new ArrayList<>();
        List<Node> deleted = new ArrayList<>();


        doc = Jsoup.parseBodyFragment("");
        doc.head().appendElement("meta").attr("charset", "utf-8");
        for (String key : combinedHtml.getKeys()) {
            Node finalElement = playBack(key, doc.body());
            CombinedHtml.Combination combination = combinedHtml.get(key);
            Node oldElement = combination.getOldElement();
            Node newElement = combination.getNewElement();

            if (newElement == null) {
                deleted.add(copyAll(oldElement, finalElement));//.wrap(DELETED_SPAN);
            } else if (oldElement == null) {
                inserted.add(copyAll(newElement, finalElement));//.wrap(INSERTED_SPAN);
            } else {
                if (combination.equals()) {
                    copyAll(newElement, finalElement);
                } else if (combination.equalTag() && !combination.equalText()) {
                    copyAttributes(newElement, finalElement);
                    String oldText = ((TextNode) oldElement).text();
                    String newText = ((TextNode) newElement).text();

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

                    Element parent = (Element) finalElement.parent();
                    finalElement.remove();
                    for (Node in : Parser.parseBodyFragment(html.toString(), "").body().childNodes()) {
                        parent.appendChild(in.clone());
                    }


                } else {
                    deleted.add(copyAll(oldElement, finalElement));//.wrap(DELETED_SPAN);
                    inserted.add(copyAll(newElement, finalElement));//.wrap(INSERTED_SPAN);
                }
            }

        }

        for (Node e : inserted) {
            e.wrap(DIV_INSERTED);
        }
        for (Node e : deleted) {
            e.wrap(DIV_DELETED);
        }

        return doc;
    }

    private Node playBack(String key, Node parent) {
        Node child = null;
        StringTokenizer tokens = new StringTokenizer(key, "_>");
        while (tokens.hasMoreTokens()) {
            String index = tokens.nextToken();
            String tag = tokens.nextToken();
            String id = index + "_" + tag;
            child = null;

            for (Node tmp : parent.childNodes()) {
                if (id.equals(tmp.attr("diffid"))) {
                    child = tmp;
                    break;
                }
            }

            if (child == null) {
                switch (tag) {
                    case "#text":
                        child = new TextNode("", "");
                        break;
                    default:
                        child = new Element(Tag.valueOf(tag), "");
                        break;
                }

                child.attr("diffid", id);
                ((Element) parent).appendChild(child);

            }
            parent = child;
        }

        return child;
    }

    private void traverseNewDocument(Node parent) {
        for (Node e : parent.childNodes()) {
            combinedHtml.addNewElement(e);
            traverseNewDocument(e);
        }
    }

    private void traverseOldDocument(Node parent) {
        for (Node e : parent.childNodes()) {
            combinedHtml.addOldElement(e);
            traverseOldDocument(e);
        }
    }

    private static Node copyAll(Node src, Node trg) {
        copyAttributes(src, trg);
        copyText(src, trg);
        return trg;
    }

    private static Node copyAttributes(Node src, Node trg) {
        for (Attribute a : src.attributes()) {
            trg.attr(a.getKey(), a.getValue());
        }
        return trg;
    }

    private static Node copyText(Node src, Node trg) {
        if (src instanceof TextNode) {
            ((TextNode) trg).text(((TextNode) src).text());
        }
        return trg;
    }

}
