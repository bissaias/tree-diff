package org.web4thejob.diff;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */
public class TreeDiffTest2 {

    @Test
    public void test() throws IOException {
        File current = new File("C:\\Documents and " +
                "Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test\\resources\\set1" +
                "\\current.html");

        File previous = new File("C:\\Documents and " +
                "Settings\\e36132\\IdeaProjects\\tree-diff\\src\\test\\resources\\set1" +
                "\\previous.html");

        TreeDiff2 tf2 = new TreeDiff2();
        tf2.buildDiffPage(current, previous);
    }
}
