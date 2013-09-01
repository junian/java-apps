package net.junian.mathnotationconv;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Uchiha Junichi
 */
public class JStack {
    private Node top;

    public boolean isEmpty(){
        return (top==null);
    }

    public String peek(){
        if(!isEmpty()){
            return top.data;
        }
        return null;
    }

    public void push(String data){
        Node new_node = new Node(data);
        new_node.next = top;
        top = new_node;
    }

    public String pop(){
        if(!isEmpty()){
            String result = peek();
            top = top.next;
            return result;
        }
        return null;
    }
}

class Node{
    String data;
    Node next;

    public Node(String new_data){
        data = new_data;
        next = null;
    }
}