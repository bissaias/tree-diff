package org.web4thejob.diff;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class HtmlDiff {
    private static final String INSERTED_SPAN = "<span style=\"background:#e6ffe6;\"></span>";
    private static final String DELETED_SPAN = "<span style=\"background:#ffe6e6;\"></span>";
    private CombinedHtml combinedHtml = new CombinedHtml();


    public Document buildDiff(File thisFile, File thatFile) throws IOException {
        Document doc;

        doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(thisFile));
        for (Element e : doc.body().children()) {
            combinedHtml.addNewElement(e);
            traverseDocument(e);
        }

        doc = Jsoup.parseBodyFragment(FileUtils.readFileToString(thisFile));
        for (Element e : doc.body().children()) {
            combinedHtml.addOldElement(e);
            traverseDocument(e);
        }

        doc = Jsoup.parseBodyFragment("");
        doc.head().appendElement("meta").attr("charset", "utf-8");
        for (String key : combinedHtml.getKeys()) {
            Element finalElement = playBack(key, doc.body());
            Element oldElement = combinedHtml.get(key).getOldElement();
            Element newElement = combinedHtml.get(key).getNewElement();

            if (newElement == null) {
                finalElement.appendChild(oldElement.wrap(DELETED_SPAN));
            } else if (oldElement == null) {
                finalElement.appendChild(newElement.wrap(INSERTED_SPAN));
            } else {
                if (oldElement.tagName().equals(newElement.tagName())) {
                    Element tag = finalElement.appendElement(newElement.tagName());


                } else {
                    finalElement.appendChild(oldElement.wrap(DELETED_SPAN));
                    finalElement.appendChild(newElement.wrap(INSERTED_SPAN));
                }
            }

        }

        return doc;
    }

    private Element playBack(String key, Element parent) {
        Element child = null;
        StringTokenizer tokens = new StringTokenizer(key, "_>");
        while (tokens.hasMoreTokens()) {
            int index = Integer.valueOf(tokens.nextToken());
            String tag = tokens.nextToken();

            if (parent.children().size() > index) {
                child = parent.children().get(index);
            }

            if (child == null) {
                child = parent.appendElement(tag);
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
