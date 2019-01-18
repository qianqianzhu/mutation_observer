package org.qzhu.mutationObserver;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class MainTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testMissingArgs(){
        String[] args = null;
        Main.main(args);
        int returnInt = Main.returnValue ;
        assertEquals(-1,returnInt);

        args = new String[]{"test"};
        returnInt = Main.returnValue ;
        assertEquals(-1,returnInt);

        args = new String[]{"testProject", "test_resources"};
        returnInt = Main.returnValue ;
        assertEquals(-1, returnInt);
    }
    @Test
    public void testIOException(){
        String[] args = new String[]{"testProject", "test_resources","./src/test/test_resources/testPitest/jfreechart-1.5.0_mutations.csv"};
        Main.main(args);
        int returnInt = Main.returnValue ;
        assertEquals(-2, returnInt);
    }

    @Test
    public void testNormalExecution(){
        String[] args = new String[]{"testProject", "./src/test/test_resources/",
                "./src/test/test_resources/testProject/target/pit-reports/201901170312/mutations.csv"};
        Main.main(args);
        int returnInt = Main.returnValue ;
        assertEquals(0, returnInt);
    }

}