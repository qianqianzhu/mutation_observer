package org.qzhu.mutationObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Qianqian Zhu
 * Class copied with modifications from https://www.javagists.com/java-tree-data-structure
 */
public class Node<T> implements Cloneable{
    private T data = null;
    private List<Node<T>> children = new LinkedList<>();
    private Node<T> parent = null;

    public Node(T data) {
        this.data = data;
    }

    public Node(Node node) {
        if (node!=null) {
            this.data = (T) node.getData();
            this.children = node.getChildren();
            this.parent = node.getParent();
        }
    }

    public Node clone(){
        return new Node(this);
    }

    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        this.children.add(child);
        return child;
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public Node<T> getParent() {
        return parent;
    }

    public Node getRoot() {
        if(parent == null){
            return this;
        }
        return parent.getRoot();
    }

    public String toString(String treeString) {
        treeString = treeString + "(" + data;
        //System.out.print("("+node.getData());
        for(Node child:children){
            treeString = child.toString(treeString);
        }
        treeString = treeString + ")" ;
        //System.out.print(")");
        return treeString;
    }

    public int maxDepth(){
        if(this==null)
            return 0;

        int bigger = 0;
        for(Node<T> child: children){
            int childDepth = child.maxDepth();
            bigger = Math.max(bigger,childDepth);
        }
        return bigger+1;
    }

    public Node<T> deleteRootNode() {
        if (parent != null) {
            throw new IllegalStateException("deleteRootNode not called on root");
        }

        Node<T> newParent = null;
        if (!getChildren().isEmpty()) {
            newParent = getChildren().get(0);
            newParent.setParent(null);
            getChildren().remove(0);
            for (Node<T> each : getChildren()) {
                each.setParent(newParent);
            }
            newParent.getChildren().addAll(getChildren());
        }
        this.getChildren().clear();
        return newParent;
    }

//    public List<Node<T>> getNextLayer(){
//        List<Node<T>> nextLayer = new LinkedList<Node<T>>();
//        if(parent==null){
//            return children;
//        }else{
//            for(Node thisLayerNode: parent.getChildren()){
//                nextLayer.addAll(thisLayerNode.getChildren());
//            }
//            return nextLayer;
//        }
//    }

    public List<Node<T>> matchFirst(Node<T> node){
        if(this==null)
            return null;

        List<Node<T>> matchNodes = new LinkedList<Node<T>>();
        if (data.equals(node.getData())){
            matchNodes.add(this);
        }

        for (Node<T> child:children){
            matchNodes.addAll(child.matchFirst(node));
        }
        return matchNodes;
    }

    public int matchCount(Node<T> node){
        int count = 0;
        List<Node<T>> firstMatchs = matchFirst(node);
        if (node.maxDepth()==1){
            count = firstMatchs.size();

        }
        if(node.maxDepth()==2){
            Node nextMatch = node.getChildren().get(0);

            for (Node<T> first : firstMatchs) {
                for (Node<T> firstChild : first.getChildren()) {
                    if (firstChild.getData() == nextMatch.getData())
                        count++;
                }
            }
        }

        return count;
    }
}
