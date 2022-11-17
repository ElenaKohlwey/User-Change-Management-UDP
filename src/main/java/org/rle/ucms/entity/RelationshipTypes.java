package org.rle.ucms.entity;

import org.neo4j.graphdb.RelationshipType;

public class RelationshipTypes {

  public enum MyRelationshipTypes implements RelationshipType {
    PRECEDES,
  }
}
