package org.rle.ucms.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.neo4j.graphdb.Node;
import org.rle.ucms.entity.dto.UserActionDto;
import org.rle.ucms.entity.dto.UserActionHistoryDto;
import org.rle.ucms.entity.nodeDescriptor.ActivityNode;
import org.rle.ucms.utility.RandomNumbers;

public class UserActionHistory {

  private final List<UserAction> userActions;

  public UserActionHistory() {
    this.userActions = new ArrayList<>();
  }

  public UserActionHistory(UserActionHistoryDto userActionHistoryDto) {
    UserActionDto[] userActionDtos = userActionHistoryDto.getUserActionDtos();
    this.userActions = new ArrayList<>();
    for (int i = 0; i < userActionDtos.length; i++) {
      this.userActions.add(new UserAction(userActionDtos[i]));
    }
  }

  // statische Methode mit Seed-Objekt dabei. --> weiter oben Random mit seed erzeugen und dann das Random-Pbjekt weitergeben
  public static UserActionHistory createRandom(
    IdToNodeMap graph,
    long numberUserActions
  ) {
    String[] keys = ActivityNode.getToBeActionedOnKeys();
    UserActionHistory uah = new UserActionHistory();
    int graphSize = graph.getSize();

    for (int i = 1; i <= numberUserActions; i++) {
      uah.add(
        new UserAction(
          i,
          graph
            .getNode(
              ActivityNode.transformToNodeId(
                RandomNumbers.randomNumber(1, graphSize)
              )
            )
            .getProperty("id", "A0")
            .toString(),
          keys[RandomNumbers.randomNumber(0, keys.length - 1)],
          RandomNumbers.randomNumber(1, 100)
        )
      );
    }

    return uah;
  }

  public void add(UserAction userAction) {
    this.userActions.add(userAction);
  }

  public void addAll(Collection<UserAction> userActions) {
    this.userActions.addAll(userActions);
  }

  public Stream<UserAction> getAll() {
    return this.userActions.stream();
  }

  public boolean hasUserAction(UserAction userAction) {
    if (this.userActions.contains(userAction)) {
      return true;
    } else {
      return false;
    }
  }

  public String print2Str(String indent) {
    String[] s = userActions
      .stream()
      .map(o -> indent + o.print2Str())
      .toArray(String[]::new);
    return String.join(System.lineSeparator(), s);
  }

  public String print2Str() {
    String[] s = userActions
      .stream()
      .map(UserAction::print2Str)
      .toArray(String[]::new);
    return String.join(System.lineSeparator(), s);
  }

  public void applyToGraph(IdToNodeMap graph) {
    Node affectedNode;
    for (UserAction ua : this.userActions) {
      affectedNode = graph.getNode(ua.getAffectedNodeId());
      affectedNode.setProperty(ua.getChangedActionKey(), ua.getNewValue());
    }
  }
}
