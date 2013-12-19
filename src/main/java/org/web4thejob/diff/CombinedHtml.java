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
        Combination combination = combinedTags.get(key);
        if (combination == null) {
            combination = combinedTags.put(key, new Combination(key));
        }
        combination.setNewElement(element);
    }

    public void addOldElement(Element element) {
        String key = buildKey(element);
        Combination combination = combinedTags.get(key);
        if (combination == null) {
            combination = combinedTags.put(key, new Combination(key));
        }
        combination.setOldElement(element);
    }

    private String buildKey(Element e) {
        String key = String.format("%05d", e.elementSiblingIndex()) + "_" + e.tagName();
        Element parent = e.parent();
        while (parent != null) {
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
            this.oldElement.children().clear();
        }

        public Element getNewElement() {
            return newElement;
        }

        public void setNewElement(Element newElement) {
            this.newElement = newElement.clone();
            this.newElement.children().clear();
        }


    }
}
