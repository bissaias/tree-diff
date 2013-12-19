package org.web4thejob.diff;

import java.util.LinkedList;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class LineDiff extends DiffMatchPatch {

    public LinkedList<Diff> diffLinesOnly(String text1, String text2) {
        final LinesToCharsResult lines = diff_linesToChars(text1, text2);
        //lines.chars1 etc. are unaccessable from outside the dmp package :(
        final LinkedList<Diff> diffs = diff_main(lines.chars1, lines.chars2);

        //diff_cleanupSemantic(diffs);

        diff_charsToLines(diffs, lines.lineArray);

        return diffs;
    }
}
