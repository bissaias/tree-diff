package org.web4thejob.diff;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.io.File;
import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class TreeDiff2 {

    public void buildDiffPage(File currentFile, File againstFile) throws IOException {


        Document currentDoc = Jsoup.parseBodyFragment(FileUtils.readFileToString(currentFile));
        Document againstDoc = Jsoup.parseBodyFragment(FileUtils.readFileToString(againstFile));

        Element currentBody = currentDoc.body();
        Element againstBody = againstDoc.body();
        Element finalBody = new Element(Tag.valueOf("body"), "");

        traverseElements(currentBody, againstBody, finalBody);

        System.out.println(finalBody.html());
    }

    private void traverseElements(Element currentElement, Element againstElement, Element finalElement) {

        for (int i = 0; i < java.lang.Math.max(currentElement.children().size(), againstElement.children().size());
             i++) {

            Element currentChild;
            Element againstChild;

            if (i < currentElement.children().size()) {
                currentChild = currentElement.children().get(i);
            } else {
                currentChild = null;
            }

            if (i < againstElement.children().size()) {
                againstChild = againstElement.children().get(i);
            } else {
                againstChild = null;
            }

            if (currentChild == null) {
                // child has been deleted
                Element del = new Element(Tag.valueOf("span"), "");
                del.addClass("deleted");
                del.appendChild(againstChild);
                finalElement.appendChild(del);
            } else if (againstChild == null) {
                // child has been inserted
                Element ins = new Element(Tag.valueOf("span"), "");
                ins.addClass("inserted");
                ins.appendChild(currentChild);
                finalElement.appendChild(ins);
            } else {

                if (currentChild.tagName().equals(againstChild.tagName())) {
                    if (currentChild.hasText() || againstChild.hasText()) {

                        if (currentChild.ownText().equals(againstChild.ownText())) {

                        } else {

                        }
                    } else {

                    }
                } else {
                    Element del = new Element(Tag.valueOf("span"), "");
                    del.addClass("deleted");
                    del.appendChild(againstChild);
                    finalElement.appendChild(del);

                    Element ins = new Element(Tag.valueOf("span"), "");
                    ins.addClass("inserted");
                    ins.appendChild(currentChild);
                    finalElement.appendChild(ins);
                }

            }

        }

    }
}
