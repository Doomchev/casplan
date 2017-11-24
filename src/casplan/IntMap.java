package casplan;

public class IntMap<E> {
  Node<E> root = new Node<>();
  
  class Node<E> {
    int key;
    E value = null;
    Node<E>[] nodes = null;
  }
  
  public void put(int key, E value) {
    Node node = root;
    while(true) {
      if(node.nodes == null) node.nodes = (Node<E>[])new Node[16];
      int index = key & 15;
      if(node.nodes[index] == null) { 
        Node<E> newNode = new Node<>();
        node.nodes[index] = newNode;
        newNode.key = key;
        newNode.value = value;
        return;
      } else {
        node = node.nodes[index];
        key = key >> 4;
      }
    }
  }
  
  public E get(int key) {
    Node<E> node = root;
    while(node.value != null) {
      if(node.key == key) return node.value;
      int index = key & 15;
      if(node.nodes[index] == null) {
        return null;
      } else {
        node = node.nodes[index];
        key = key >> 4;
      }
    }
    return null;
  }
}
