package org.web4thejob.diff;

/**
 * @author Veniamin Isaias
 * @since 1.0.0
 */

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.IOException;

public class DecodeBase64 {

    @Test
    public void base64ToFile() throws IOException {
        File in = new File("c:\\Documents and Settings\\e36132\\My Documents\\jsoup.txt");
        File out = new File("c:\\Documents and Settings\\e36132\\My Documents\\jsoup.zip");
        String base = FileUtils.readFileToString(in);

        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = decoder.decodeBuffer(base);

        FileUtils.writeByteArrayToFile(out, decodedBytes);

    }
}
