package org.rle.ucms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rle.ucms.entity.UserAction;

public class UserActionDto {

  @JsonProperty("actionId")
  private int actionId;

  @JsonProperty("affectedNodeId")
  private String affectedNodeId;

  @JsonProperty("changedActionKey")
  private String changedActionKey;

  @JsonProperty("newValue")
  private int newValue;

  public UserActionDto() {}

  public UserActionDto(UserAction userAction) {
    this.actionId = userAction.getActionId();
    this.affectedNodeId = userAction.getAffectedNodeId();
    this.changedActionKey = userAction.getChangedActionKey();
    this.newValue = userAction.getNewValue();
  }

  public int getActionId() {
    return this.actionId;
  }

  public void setActionId(int actionId) {
    this.actionId = actionId;
  }

  public String getAffectedNodeId() {
    return this.affectedNodeId;
  }

  public void setAffectedNodeId(String affectedNodeId) {
    this.affectedNodeId = affectedNodeId;
  }

  public String getChangedActionKey() {
    return this.changedActionKey;
  }

  public void setChangedActionKey(String changedActionKey) {
    this.changedActionKey = changedActionKey;
  }

  public int getNewValue() {
    return this.newValue;
  }

  public void setNewValue(int newValue) {
    this.newValue = newValue;
  }
}
