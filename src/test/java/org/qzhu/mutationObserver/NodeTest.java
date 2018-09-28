package org.qzhu.mutationObserver;

import org.junit.Test;
import org.qzhu.mutationObserver.source.Node;
import java.util.List;
import static org.junit.Assert.*;

/**
 * @author Qianqian Zhu
 */
public class NodeTest {

    private static Node<String> createTree() {
        Node<String> root = new Node<>("root");

        Node<String> node1 = root.addChild(new Node<String>("node1"));

        Node<String> node11 = node1.addChild(new Node<String>("node11"));
        Node<String> node13 = node1.addChild(new Node<String>("noise"));
        Node<String> node14 = node1.addChild(new Node<String>("noise"));

        Node<String> node111 = node11.addChild(new Node<String>("node111"));
        Node<String> node112 = node11.addChild(new Node<String>("node112"));
        Node<String> node113 = node11.addChild(new Node<String>("noise"));


        Node<String> node12 = node1.addChild(new Node<String>("node12"));

        Node<String> node2 = root.addChild(new Node<String>("node2"));

        Node<String> node21 = node2.addChild(new Node<String>("node21"));
        Node<String> node22 = node2.addChild(new Node<String>("node22"));
        return root;
    }

//    private static <T> String tree2string(Node<T> node,String treeString) {
//        treeString = treeString + "(" + node.getData();
//        //System.out.print("("+node.getData());
//        List<Node<T>> childNodes = node.getChildren();
//        for(Node<T> child:childNodes){
//            treeString = tree2string(child,treeString);
//        }
//        treeString = treeString + ")" ;
//        //System.out.print(")");
//        return treeString;
//    }

    @Test
    public void testTree2String() {
        Node<String> root = createTree();
        String treeString="";
        treeString = root.toString(treeString);
        //treeString= tree2string(root,treeString);
        assertEquals(treeString,"(root(node1(node11(node111)(node112)(noise))(noise)(noise)(node12))(node2(node21)(node22)))");
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
    public void testMatchOne(){
        Node<String> root = createTree();
        Node<String> searchPattern = new Node<>("noise");
        String[] expectedData = {"noise","noise","noise"};
        List<Node<String>> matchNodes = root.matchFirst(searchPattern);
        assertEquals(matchNodes.size(),3);
        for(int i=0;i<matchNodes.size();i++){
            assertEquals(matchNodes.get(i).getData(),expectedData[i]);
        }
    }

//    @Test
//    public void testGetNextLayer(){
//        Node<String> root = createTree();
//
//        // root node case
//        List<Node<String>> nextLayer = root.getNextLayer();
//        String[] expectedNextLayerData = {"node1","node2"};
//        for(int i = 0; i<nextLayer.size();i++){
//            assertEquals(nextLayer.get(i).getData(),expectedNextLayerData[i]);
//        }
//        // non-root node case
//        List<Node<String>> nextLayer2 = root.getNextLayer().get(0).getNextLayer();
//        String[] expectedNextLayerData2 = {"node11","noise","node12","node21","node22"};
//        for(int i = 0; i<nextLayer2.size();i++){
//            assertEquals(nextLayer2.get(i).getData(),expectedNextLayerData2[i]);
//        }
//    }

    @Test
    public void testMatchCount(){
        Node<String> root = createTree();
        Node<String> searchPattern = new Node<>("node1");
        searchPattern.addChild(new Node<>("noise"));
        assertEquals(root.matchCount(searchPattern),2);

        Node<String> searchPattern2 = new Node<>("noise");
        assertEquals(root.matchCount(searchPattern2),3);

    }

}