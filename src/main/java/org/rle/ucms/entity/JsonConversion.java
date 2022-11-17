package org.rle.ucms.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.rle.ucms.entity.dto.UserActionHistoryDto;

public class JsonConversion {

  private JsonConversion() {}

  public static String toJson(UserActionHistory userActionHistory)
    throws IOException {
    UserActionHistoryDto uahDto = new UserActionHistoryDto(userActionHistory);
    ObjectMapper om = new ObjectMapper();
    return om.writeValueAsString(uahDto);
  }

  public static UserActionHistory toUserActionHistory(String jsonData)
    throws IOException {
    ObjectMapper om = new ObjectMapper();
    UserActionHistoryDto uahDto = om.readValue(
      jsonData,
      UserActionHistoryDto.class
    );
    return new UserActionHistory(uahDto);
  }
}
