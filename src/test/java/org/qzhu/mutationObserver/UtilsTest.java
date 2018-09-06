package org.qzhu.mutationObserver;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Qianqian Zhu
 */
public class UtilsTest {

    @Test
    public void testGetAllFilesFromDir() throws IOException {

        String testDir ="./src/main/resources/";
        List<String> fileNames = new ArrayList<>();
        fileNames = Utils.getAllJavaFilesFromDir(fileNames,testDir);
        assertEquals(fileNames.size(),2);
        assertTrue(fileNames.contains("./src/main/resources/helloworld.java"));
        assertTrue(fileNames.contains("./src/main/resources/ClassPathUtils.java"));
    }


    @Test
    public void testLcsCase1() {
        String[] sq1 = "AGGTAB".split("");
        String[] sq2 = "GXTXAYB".split("");

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(Utils.lcs(a,b),4);

    }

    @Test
    public void testLcsCase2() {
        String[] sq1 = {"if","{","}"};
        String[] sq2 = {"if","{","}","else","{","}"};

        ArrayList<String> a = new ArrayList<>();
        ArrayList<String> b = new ArrayList<>();

        a.addAll(Arrays.asList(sq1));
        b.addAll(Arrays.asList(sq2));

        assertEquals(Utils.lcs(a,b),3);

    }
}