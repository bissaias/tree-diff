package org.web4thejob.diff;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class TreeDiff {

    public static TreeDiffResult buildDiffPage(File thisFile, File thatFile) throws IOException {
        List<ElementWrapper> thisNodes;
        List<ElementWrapper> thatNodes;


        thisNodes = new ArrayList<>();
        String thisHtml = FileUtils.readFileToString(thisFile);
        Document thisDoc = Jsoup.parseBodyFragment(thisHtml);

        for (Element e : thisDoc.body().children()) {
            thisNodes.add(new ElementWrapper(e));

            traverseDocument(e, thisNodes);
        }

        thatNodes = new ArrayList<>();
        String thatHtml = FileUtils.readFileToString(thatFile);
        Document thatDoc = Jsoup.parseBodyFragment(thatHtml);

        for (Element e : thatDoc.body().children()) {
            thatNodes.add(new ElementWrapper(e));

            traverseDocument(e, thatNodes);
        }

/*
        List<ElementWrapper> finalNodes = new ArrayList<>();
        for (int i = 0; i < java.lang.Math.max(thisNodes.size(), thatNodes.size()); i++) {
            ElementWrapper thisNode = null;
            ElementWrapper thatNode = null;

            if (i < thisNodes.size()) {
                thisNode = thisNodes.get(i);
            } else {
                thisNodes = null;
            }

            if (i < thatNodes.size()) {
                thatNode = thatNodes.get(i);
            } else {
                thatNode = null;
            }


            if (thatNode == null) {
                thisNode.status = ElementStatus.INSERTED;
                finalNodes.add(thisNode);
            } else if (thisNode == null) {
                thatNode.status = ElementStatus.DELETED;
                finalNodes.add(thatNode);
            } else {
                if (thisNode.Id.equals(thatNode.Id)) {
                    thisNode.status = ElementStatus.MATCHED;
                    thisNode.against = thatNode.element;
                    finalNodes.add(thisNode);
                } else {
                    thatNode.status = ElementStatus.DELETED;
                    finalNodes.add(thatNode);
                    thisNode.status = ElementStatus.INSERTED;
                    finalNodes.add(thisNode);
                }
            }
        }


        for (ElementWrapper ew : finalNodes) {
            System.out.println(ew.status + " " + ew.Id);
        }
*/
        Map<String, ElementWrapper> elementWrappers = new HashMap<>();

        StringBuilder thisString = new StringBuilder();
        for (ElementWrapper ew : thisNodes) {
            thisString.append(ew.Id + "\n");
            elementWrappers.put(ew.Id, ew);
        }

        StringBuilder thatString = new StringBuilder();
        for (ElementWrapper ew : thatNodes) {
            thatString.append(ew.Id + "\n");
            elementWrappers.put(ew.Id, ew);
        }

        LineDiff lineDiffs = new LineDiff();
        LinkedList<DiffMatchPatch.Diff> diffs = lineDiffs.diffLinesOnly(thisString.toString(), thatString.toString());
        //lineDiffs.diff_cleanupSemantic(diffs);

        return new TreeDiffResult(elementWrappers, diffs);
    }


    private static void traverseDocument(Element parent, List<ElementWrapper> nodes) {
        for (Element e : parent.children()) {
            nodes.add(new ElementWrapper(e));
            traverseDocument(e, nodes);
        }
    }


    private static String buildTagId(Element e) {
        String tagId = e.tagName(); // + "_" + e.elementSiblingIndex();
        Element parent = e.parent();
        while (parent != null) {
            tagId = parent.tagName() + /*"_" + parent.elementSiblingIndex() +*/ ">" + tagId;
            parent = parent.parent();
        }

        String s = tagId; // + ":" + e.ownText();
        return s + ">" + Base64.encodeBase64String(DigestUtils.getMd5Digest().digest(e.ownText().getBytes(Charset
                .forName("UTF-8"))));
    }

    public static class ElementWrapper {
        public final Element element;
        public final String Id;

        public ElementWrapper(Element e) {
            this.element = e;
            this.Id = buildTagId(e);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(Id).toHashCode();

        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ElementWrapper) {
                return ((ElementWrapper) obj).Id.equals(Id);
            }
            return false;
        }
    }


}
