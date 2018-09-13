package org.qzhu.mutationObserver;

import java.util.LinkedList;
import java.util.List;

public class Node<T> implements Cloneable{
    private T data = null;
    private List<Node<T>> children = new LinkedList<>();
    private Node<T> parent = null;

    public Node(T data) {
        this.data = data;
    }

    public Node(Node node) {
        this.data = (T) node.getData();
        this.children = node.getChildren();
        this.parent = node.getParent();
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
        treeString = treeString + "(" + this.data;
        //System.out.print("("+node.getData());
        for(Node child:children){
            treeString = child.toString(treeString);
        }
        treeString = treeString + ")" ;
        //System.out.print(")");
        return treeString;
    }

    public int compareTo(Node<T> node){
        int count = 0;
        if(node.getData().equals(this.data)){
            
        }

        return count;
    }
}
