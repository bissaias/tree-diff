package org.web4thejob.diff;

import org.jsoup.nodes.Element;

import java.util.*;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class CombinedHtml {
    private Map<String, Combination> combinedTags = new HashMap<>();

    public void addNewElement(Element element) {
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

    public void addOldElement(Element element) {
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

    private String buildKey(Element e) {
        String key = String.format("%05d", e.elementSiblingIndex()) + "_" + e.tagName();
        Element parent = e.parent();
        while (parent != null && !parent.tagName().equals("body")) {
            key = String.format("%05d", parent.elementSiblingIndex()) + "_" + parent.tagName() + ">" + key;
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
        private Element newElement;
        private Element oldElement;

        public Combination(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public Element getOldElement() {
            return oldElement;
        }

        public void setOldElement(Element oldElement) {
            this.oldElement = oldElement.clone();
            clearChildren(this.oldElement);
        }

        public Element getNewElement() {
            return newElement;
        }

        public void setNewElement(Element newElement) {
            this.newElement = newElement.clone();
            clearChildren(this.newElement);
        }

        public boolean equals() {
            return equalTag() && equalText();
        }

        public boolean equalText() {
            String oldText = "";
            if (oldElement != null && oldElement.hasText()) {
                oldText = oldElement.ownText();
            }

            String newText = "";
            if (newElement != null && newElement.hasText()) {
                newText = newElement.ownText();
            }
            return newText.equals(oldText);
        }

        public boolean equalTag() {
            String oldTag = "";
            if (oldElement != null) {
                oldTag = oldElement.tagName();
            }

            String newTag = "";
            if (newElement != null) {
                newTag = newElement.tagName();
            }
            return newTag.equals(oldTag);
        }

        private void clearChildren(Element e) {
            while (e.children().size() > 0) {
                e.child(0).remove();
            }
        }
    }

}
