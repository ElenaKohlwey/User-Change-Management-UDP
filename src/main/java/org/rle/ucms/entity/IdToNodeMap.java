package org.rle.ucms.entity;

import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.rle.ucms.entity.nodeDescriptor.ActivityNode;

public class IdToNodeMap {

  private final HashMap<String, Node> nodes;

  // Id to node map --> kein Graph
  public IdToNodeMap(ResourceIterable<Node> nodeIt) {
    ResourceIterator<Node> nodeIterator = nodeIt.iterator();
    this.nodes = new HashMap<>();
    while (nodeIterator.hasNext()) {
      Node nextNode = nodeIterator.next();
      this.nodes.put(nextNode.getProperty("id", "A0").toString(), nextNode);
    }
  }

  public static IdToNodeMap fetchGraph(Transaction tx) {
    ResourceIterable<Node> nodeIt = tx.getAllNodes();
    return new IdToNodeMap(nodeIt);
  }

  public Node getNode(String key) {
    return nodes.get(key);
  }

  public int getSize() {
    return this.nodes.size();
  }

  public void removeUserActions() {
    String[] keys = ActivityNode.getToBeActionedOnKeys();
    for (Map.Entry<String, Node> set : this.nodes.entrySet()) {
      Node currentNode = set.getValue();
      for (String key : keys) {
        currentNode.removeProperty(key);
      }
    }
  }

  public String print2Str() {
    String[] s = new String[this.getSize()];

    int counter = 0;
    for (Map.Entry<String, Node> set : this.nodes.entrySet()) {
      Node currentNode = set.getValue();
      s[counter] =
        currentNode.getProperty("id", "A0") +
        ", " +
        currentNode.getProperty("name", "");
      counter++;
    }

    return String.join(System.lineSeparator(), s);
  }
}
