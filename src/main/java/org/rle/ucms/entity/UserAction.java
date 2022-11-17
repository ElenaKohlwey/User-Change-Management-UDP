package org.rle.ucms.entity;

import org.rle.ucms.entity.dto.UserActionDto;

public class UserAction {

  private final int actionId;
  private final String affectedNodeId;
  private final String changedActionKey;
  private final int newValue;

  public UserAction(
    int actionId,
    String affectedNodeId,
    String changedActionKey,
    int newValue
  ) {
    this.actionId = actionId;
    this.affectedNodeId = affectedNodeId;
    this.changedActionKey = changedActionKey;
    this.newValue = newValue;
  }

  public UserAction(UserActionDto userActionDto) {
    this.actionId = userActionDto.getActionId();
    this.affectedNodeId = userActionDto.getAffectedNodeId();
    this.changedActionKey = userActionDto.getChangedActionKey();
    this.newValue = userActionDto.getNewValue();
  }

  public int getActionId() {
    return this.actionId;
  }

  public String getAffectedNodeId() {
    return this.affectedNodeId;
  }

  public String getChangedActionKey() {
    return this.changedActionKey;
  }

  public int getNewValue() {
    return this.newValue;
  }

  public UserActionDto getUserActionDto() {
    return new UserActionDto(this);
  }

  public String print2Str() {
    return String.format(
      "id: %d, node: %s, key: %s, newVal: %d",
      actionId,
      affectedNodeId,
      changedActionKey,
      newValue
    );
  }
}
