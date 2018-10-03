package org.qzhu.mutationObserver.callgraph;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DigraphTest {

    @Test
    public void testDigrah(){
        Digraph<Integer> graph = new Digraph<Integer>();

        graph.add(0, 1);
        graph.add(1, 2);
        graph.add(2, 3);
        graph.add(3, 0);
        graph.add(1, 3);
        graph.add(2, 1);
        graph.add(2, 1);

        System.out.println(graph);

        assertEquals(4,graph.getNumberOfVertex());
        assertEquals(6,graph.getNumberOfEdges());
        assertEquals(1,graph.inDegree(0));
        assertEquals(1, graph.outDegree(0));
        assertEquals(2,graph.inDegree(3));
        assertEquals(1,graph.outDegree(3));

        ArrayList<Integer> expectedOutBound = new ArrayList<>();
        expectedOutBound.add(2);
        expectedOutBound.add(3);

        assertEquals(expectedOutBound,graph.outboundNeighbors(1));
        ArrayList<Integer> expectedInBound = new ArrayList<>();
        expectedInBound.add(0);
        expectedInBound.add(2);
        assertEquals(expectedInBound,graph.inboundNeighbors(1));
        assertFalse(graph.isEdge(0, 2));
        assertTrue(graph.isEdge(1, 3));

    }
}
