package org.web4thejob.diff;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class TreeDiffResult {
    public final LinkedList<DiffMatchPatch.Diff> diffs;
    public final Map<String, TreeDiff.ElementWrapper> elementWrappers;

    public TreeDiffResult(Map<String, TreeDiff.ElementWrapper> elementWrappers, LinkedList<DiffMatchPatch.Diff> diffs) {
        this.elementWrappers = elementWrappers;
        this.diffs = diffs;
    }

}
