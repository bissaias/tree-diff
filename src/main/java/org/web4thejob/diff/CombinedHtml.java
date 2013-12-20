package org.web4thejob.diff;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class CombinedHtml {
    private Map<String, Combination> combinedTags = new HashMap<>();

    public void addNewElement(Node element) {
        String key = buildKey(element);
        String id = getDiffId(key);
        element.attr("diffid", id);
        Combination combination = combinedTags.get(key);
        if (combination == null) {
            combination = new Combination(key);
            combinedTags.put(key, combination);
        }
        combination.setNewElement(element);
    }

    public void addOldElement(Node element) {
        String key = buildKey(element);
        String id = getDiffId(key);
        element.attr("diffid", id);
        Combination combination = combinedTags.get(key);
        if (combination == null) {
            combination = new Combination(key);
            combinedTags.put(key, combination);
        }
        combination.setOldElement(element);
    }

    private String getDiffId(String key) {
        String[] tokens = key.split(">");
        return tokens[tokens.length - 1];
    }

    private String buildKey(Node e) {
        String key = String.format("%05d", e.siblingIndex()) + "_" + e.nodeName();
        Node parent = e.parent();
        while (parent != null && !parent.nodeName().equals("body")) {
            key = String.format("%05d", parent.siblingIndex()) + "_" + parent.nodeName() + ">" + key;
            parent = parent.parent();
        }

        return key;
    }

    public Combination get(String key) {
        return combinedTags.get(key);
    }

    public Collection<String> getKeys() {
        List<String> keys = new ArrayList<>(combinedTags.keySet());
        Collections.sort(keys);
        return keys;
    }

    public class Combination {
        private String key;
        private Node newElement;
        private Node oldElement;

        public Combination(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public Node getOldElement() {
            return oldElement;
        }

        public void setOldElement(Node oldElement) {
            this.oldElement = oldElement.clone();
            clearChildren(this.oldElement);
        }

        public Node getNewElement() {
            return newElement;
        }

        public void setNewElement(Node newElement) {
            this.newElement = newElement.clone();
            clearChildren(this.newElement);
        }

        public boolean equals() {
            return equalTag() && equalText();
        }

        public boolean equalText() {
            String oldText = "";
            if (oldElement instanceof TextNode) {
                oldText = ((TextNode) oldElement).text();
            }

            String newText = "";
            if (newElement instanceof TextNode) {
                newText = ((TextNode) newElement).text();
            }
            return newText.equals(oldText);
        }

        public boolean equalTag() {
            String oldTag = "";
            if (oldElement != null) {
                oldTag = oldElement.nodeName();
            }

            String newTag = "";
            if (newElement != null) {
                newTag = newElement.nodeName();
            }
            return newTag.equals(oldTag);
        }

        private void clearChildren(Node e) {
            while (e.childNodeSize() > 0) {
                e.childNode(0).remove();
            }
        }
    }

}
