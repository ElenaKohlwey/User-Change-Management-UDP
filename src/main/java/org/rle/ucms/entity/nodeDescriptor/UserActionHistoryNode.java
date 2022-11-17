package org.rle.ucms.entity.nodeDescriptor;

public class UserActionHistoryNode {

  private static final String ID_KEY = "id";
  private static final String NAME_KEY = "name";
  private static final String CHANGE_LOG_KEY = "changeLog";
  private static final String USER_ACTION_NODE_KEY = "userActionNode";
  private static final String DEFAULT_ID = "UA";
  private static final String DEFAULT_NAME = "UserActionNode";

  private UserActionHistoryNode() {}

  public static String getIdKey() {
    return ID_KEY;
  }

  public static String getNameKey() {
    return NAME_KEY;
  }

  public static String getChangeLogKey() {
    return CHANGE_LOG_KEY;
  }

  public static String getUserActionNodeKey() {
    return USER_ACTION_NODE_KEY;
  }

  public static String getDefaultId() {
    return DEFAULT_ID;
  }

  public static String getDefaultName() {
    return DEFAULT_NAME;
  }
}
