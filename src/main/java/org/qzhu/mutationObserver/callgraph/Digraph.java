package org.qzhu.mutationObserver.callgraph;
import java.util.*;

/**
 * @author Qianqian Zhu
 * Class copied with modifications from Stack Overflow: https://stackoverflow.com/questions/19757371/directed-graph-in-java
 */
public class Digraph<V> {

    public static class Edge<V>{
        private V vertex;

        public Edge(V v){
            vertex = v;
        }

        public V getVertex() {
            return vertex;
        }

        @Override
        public String toString() {
            return String.valueOf(vertex) ;
        }

        @Override
        public boolean equals(Object obj){
            if(this == obj)
                return true;
            if(!(obj instanceof Edge))
                return false;
            Edge objEdge = (Edge) obj;
            return objEdge.vertex.equals(this.vertex);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + this.vertex.hashCode();
            return hash;
        }
    }

    /**
     * A Map is used to map each vertex to its list of adjacent vertices.
     */
    private Map<V, Set<Edge<V>>> neighbors = new HashMap<>();

    /**
     * String representation of graph.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (V v : neighbors.keySet())
            s.append("\n    " + v + " : " + neighbors.get(v));
        return s.toString();
    }

    /**
     * Add a vertex to the graph. Nothing happens if vertex is already in graph.
     */
    public void add(V vertex) {
        if (neighbors.containsKey(vertex))
            return;
        neighbors.put(vertex, new HashSet<>());
    }

    public List<V> getAllVertex(){
        ArrayList<V> vertex = new ArrayList<>();
        vertex.addAll(neighbors.keySet());
        return vertex;
    }

    public int getNumberOfVertex(){
        return neighbors.keySet().size();
    }

    public int getNumberOfEdges(){
        int sum = 0;
        for(Set<Edge<V>> outBounds : neighbors.values()){
            sum += outBounds.size();
        }
        return sum;
    }

    /**
     * True iff graph contains vertex.
     */
    public boolean contains(V vertex) {
        return neighbors.containsKey(vertex);
    }

    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    public void add(V from, V to) {
        this.add(from);
        this.add(to);
        neighbors.get(from).add(new Edge<V>(to));
    }

    public int outDegree(int vertex) {
        return neighbors.get(vertex).size();
    }

    public int inDegree(V vertex) {
        return inboundNeighbors(vertex).size();
    }

    public List<V> outboundNeighbors(V vertex) {
        List<V> list = new ArrayList<>();
        for(Edge<V> e: neighbors.get(vertex))
            list.add(e.vertex);
        return list;
    }

    public List<V> inboundNeighbors(V inboundVertex) {
        List<V> inList = new ArrayList<>();
        for (V to : neighbors.keySet()) {
            for (Edge e : neighbors.get(to))
                if (e.vertex.equals(inboundVertex))
                    inList.add(to);
        }
        return inList;
    }

    public boolean isEdge(V from, V to) {
        for(Edge<V> e :  neighbors.get(from)){
            if(e.vertex.equals(to))
                return true;
        }
        return false;
    }
}