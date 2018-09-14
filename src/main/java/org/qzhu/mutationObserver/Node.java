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

    public int maxDepth(){
        if(this==null)
            return 0;

        int bigger = 0;
        for(Node child: children){
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

    public int compareTo(Node<T> node){
        System.out.println(this.data);
        if(this==null)
            return 0;

//        if(this.maxDepth()<node.maxDepth())
//            return 0;

        int count = 0;
        if(data.equals(node.getData())){
            for(Node child:children){
                if(child.getData().equals(node.getChildren().get(0).getData())){
                    count++;
                }
                List<Node> subChildren = child.getChildren();
                for (Node subChild:subChildren){
                    count = count+ subChild.compareTo(node.getChildren().get(0));
                }
            }
        }

//        for(Node child:children){
//            count = count + child.compareTo(node);
//        }
        return count;
    }
}
