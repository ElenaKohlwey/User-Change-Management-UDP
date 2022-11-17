package org.rle.ucms.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.rle.ucms.entity.UserActionHistory;

public class UserActionHistoryDto {

  @JsonProperty("userActions")
  private UserActionDto[] userActionDtos;

  public UserActionHistoryDto() {}

  public UserActionHistoryDto(UserActionHistory userActionHistory) {
    userActionDtos =
      userActionHistory
        .getAll()
        .map(UserActionDto::new)
        .toArray(UserActionDto[]::new);
  }

  public UserActionDto[] getUserActionDtos() {
    return this.userActionDtos;
  }

  public void setUserActionDtos(UserActionDto[] userActionDtos) {
    this.userActionDtos = userActionDtos;
  }
  // im dto hashcode und equals überschreiben (in den neuen Deskriptoren von Jensanschauen). Hascode: wird genullt im Setter und im Getter berechnet und gespeichert
  // EntityIdentifier + subclasses gucken

}
