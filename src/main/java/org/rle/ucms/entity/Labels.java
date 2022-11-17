package org.rle.ucms.entity;

import org.neo4j.graphdb.Label;

public class Labels {

  public enum MyLabels implements Label {
    Activity,
    UserActionHistory,
  }
}
