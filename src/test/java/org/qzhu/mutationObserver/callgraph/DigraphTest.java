package org.qzhu.mutationObserver.callgraph;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class DigraphTest {

    @Test
    public void testDigrah(){
        Digraph<Integer> graph = new Digraph<>();

        graph.add(0, 1);
        graph.add(1, 2);
        graph.add(1, 4);
        graph.add(2, 3);
        graph.add(3, 0);
        graph.add(1, 3);
        graph.add(2, 1);
        graph.add(2, 1);

        assertEquals(5,graph.getNumberOfVertex());
        assertEquals(7,graph.getNumberOfEdges());
        assertEquals(1,graph.inDegree(0));
        assertEquals(1, graph.outDegree(0));
        assertEquals(2,graph.inDegree(3));
        assertEquals(1,graph.outDegree(3));

        ArrayList<Integer> expectedOutBound = new ArrayList<>();
        expectedOutBound.add(2);
        expectedOutBound.add(3);
        expectedOutBound.add(4);

        assertEquals(expectedOutBound,graph.outboundNeighbors(1));
        ArrayList<Integer> expectedInBound = new ArrayList<>();
        expectedInBound.add(0);
        expectedInBound.add(2);
        assertEquals(expectedInBound,graph.inboundNeighbors(1));
        assertFalse(graph.isEdge(0, 2));
        assertTrue(graph.isEdge(1, 3));

    }

    @Test
    public void testShortestDistanceBFS(){

        Digraph<Integer> graph = new Digraph<>();
        graph.add(0, 1);
        graph.add(1, 2);
        graph.add(1, 4);
        graph.add(2, 3);
        graph.add(3, 0);
        graph.add(2, 1);
        graph.add(2, 1);

        // case 1
        HashMap<Integer,Integer> expected = new HashMap<>();
        expected.put(0,-1);
        expected.put(1,0);
        expected.put(2,1);
        expected.put(4,1);
        expected.put(3,2);

        HashMap<Integer,Integer> shortestDistance =  graph.shortestDistanceBFS(0);
        assertEquals(expected,shortestDistance);

        // case 2
        HashMap<Integer,Integer> expected2 = new HashMap<>();
        expected2.put(1,-1);
        expected2.put(2,0);
        expected2.put(4,0);
        expected2.put(3,1);
        expected2.put(0,2);

        HashMap<Integer,Integer> shortestDistance2 =  graph.shortestDistanceBFS(1);
        assertEquals(expected2,shortestDistance2);


    }
}
