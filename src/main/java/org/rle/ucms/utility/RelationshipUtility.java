package org.rle.ucms.utility;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.rle.ucms.entity.RelationshipTypes.MyRelationshipTypes;

public class RelationshipUtility {

  private RelationshipUtility() {}

  // checks if an outgoing PRECEDES relationship exists from n1 to n2
  public static boolean existsRelationship(Node n1, Node n2) {
    for (Relationship rel : n1.getRelationships(
      Direction.OUTGOING,
      MyRelationshipTypes.PRECEDES
    )) {
      if (rel.getOtherNode(n1).equals(n2)) return true;
    }
    return false;
  }
}
