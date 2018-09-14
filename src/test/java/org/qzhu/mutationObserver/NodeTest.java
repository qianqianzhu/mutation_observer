package org.qzhu.mutationObserver;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NodeTest {

    private static Node<String> createTree() {
        Node<String> root = new Node<>("root");

        Node<String> node1 = root.addChild(new Node<String>("node1"));

        Node<String> node11 = node1.addChild(new Node<String>("node11"));
        Node<String> node13 = node1.addChild(new Node<String>("root"));

        Node<String> node111 = node11.addChild(new Node<String>("node111"));
        Node<String> node112 = node11.addChild(new Node<String>("node112"));
        Node<String> node113 = node11.addChild(new Node<String>("root"));


        Node<String> node12 = node1.addChild(new Node<String>("node12"));

        Node<String> node2 = root.addChild(new Node<String>("node2"));

        Node<String> node21 = node2.addChild(new Node<String>("node21"));
        Node<String> node22 = node2.addChild(new Node<String>("node22"));
        return root;
    }

    private static <T> String tree2string(Node<T> node,String treeString) {
        treeString = treeString + "(" + node.getData();
        //System.out.print("("+node.getData());
        List<Node<T>> childNodes = node.getChildren();
        for(Node<T> child:childNodes){
            treeString = tree2string(child,treeString);
        }
        treeString = treeString + ")" ;
        //System.out.print(")");
        return treeString;
    }

    @Test
    public void testTree2String() {
        Node<String> root = createTree();
        String treeString = "";
        treeString = root.toString(treeString);
        //treeString= tree2string(root,treeString);
        assertEquals(treeString,"(root(node1(node11(node111)(node112))(node12))(node2(node21)(node22)))");
    }

    @Test
    public void testMaxDepth(){
        Node<String> root = createTree();
        int maxDepth = root.maxDepth();
        assertEquals(maxDepth,4);
        Node<String> oneDepthNode = new Node<>("root");
        int maxDepth2 = oneDepthNode.maxDepth();
        assertEquals(maxDepth2,1);
    }

    @Test
    public void testCompareTo(){
        Node<String> root = createTree();
        String treeString = "";
        treeString = root.toString(treeString);
        System.out.println(treeString);
        Node<String> searchPattern = new Node<>("root");
        searchPattern.addChild(new Node<>("root"));
        String treeString2 = "";
        treeString2 = searchPattern.toString(treeString2);
        System.out.println(treeString2);
        System.out.println(root.compareTo(searchPattern));

    }
}