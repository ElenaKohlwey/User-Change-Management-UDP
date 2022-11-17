package org.rle.ucms.entity.nodeDescriptor;

public class ActivityNode {

  private static final String[] TO_BE_ACTIONED_ON_KEYS = {
    "duration",
    "startDelay",
  };
  private static final String ID_PREFIX = "A";
  private static final String DEFAULT_ID = transformToNodeId(0);
  private static final String NAME_PREFIX = "Activity";
  private static final String ID_KEY = "id";
  private static final String NAME_KEY = "name";

  private ActivityNode() {}

  public static String[] getToBeActionedOnKeys() {
    return TO_BE_ACTIONED_ON_KEYS;
  }

  public static String getDefaultId() {
    return DEFAULT_ID;
  }

  public static String transformToNodeId(int id) {
    return ID_PREFIX + id;
  }

  public static String transformToNodeName(int id) {
    return NAME_PREFIX + id;
  }

  public static String getIdKey() {
    return ID_KEY;
  }

  public static String getNameKey() {
    return NAME_KEY;
  }
}
