package org.rle.ucms;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.*;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.rle.ucms.entity.IdToNodeMap;
import org.rle.ucms.entity.JsonConversion;
import org.rle.ucms.entity.Labels.MyLabels;
import org.rle.ucms.entity.RelationshipTypes.MyRelationshipTypes;
import org.rle.ucms.entity.UserActionHistory;
import org.rle.ucms.entity.nodeDescriptor.ActivityNode;
import org.rle.ucms.entity.nodeDescriptor.UserActionHistoryNode;
import org.rle.ucms.utility.RandomNumbers;
import org.rle.ucms.utility.RelationshipUtility;

public class Procedures {

  @Context
  public Transaction tx;

  private static Logger logger = Logger.getLogger(Procedures.class.getName());

  private static final String GENERATE_GRAPH_NAME =
    "org.rle.ucms.generateGraph";
  private static final String CREATE_USER_ACTION_NODE =
    "org.rle.ucms.createUserActionNode";
  private static final String APPLY_USER_ACTION_HISTORY =
    "org.rle.ucms.applyUserActionHistory";
  private static final String APPLY_USER_ACTION_HISTORY_WITH_NODE =
    "org.rle.ucms.applyUserActionHistoryWithNode";
  private static final String REVERT_GRAPH = "org.rle.ucms.revertGraph";

  /* This procedure creates an Activity graph of numberNodes nodes. 
  The graph is connected (there are no unconnected nodes) and does not contain circles. */
  @Procedure(mode = Mode.WRITE, name = GENERATE_GRAPH_NAME)
  @Description("Create a graph")
  public void generateGraph(@Name("numberNodes") long numberNodes) {
    final int MAXIMUM_NUMBER_STARTING_NODES = 10;
    final int MAXIMUM_NUMBER_OUTGOING_RELS = 5;

    ArrayDeque<Node> currentNodes = new ArrayDeque<>();
    ArrayDeque<Node> unvisitedNodes = new ArrayDeque<>();
    HashSet<Node> visitedNodes = new HashSet<>();

    int numberNodesInt = (int) numberNodes;

    // create activity nodes
    HashMap<String, Node> nodes = createActivityNodes(numberNodesInt);

    // add all activity nodes to unvisited nodes
    for (Map.Entry<String, Node> set : nodes.entrySet()) {
      unvisitedNodes.add(set.getValue());
    }

    // Find starting nodes
    int numberStartingNodes = RandomNumbers.randomNumber(
      1,
      MAXIMUM_NUMBER_STARTING_NODES
    );
    for (int i = 1; i <= numberStartingNodes; i++) {
      currentNodes.add(nodes.get(ActivityNode.transformToNodeId(i)));
    }

    // all starting nodes are visited by default
    visitedNodes.addAll(currentNodes);
    unvisitedNodes.removeAll(currentNodes);

    // create relationships
    while (!(currentNodes.isEmpty() && unvisitedNodes.isEmpty())) {
      if (currentNodes.isEmpty()) {
        currentNodes.add(unvisitedNodes.poll());
      }

      // take the next node from the currentNodes list to attach outgoing relationships to it
      Node currentNode = currentNodes.poll();

      // get a random number between 1 and the maximum number of outoing relationships to attach to currentNode
      int numberOutgoingRels = RandomNumbers.randomNumber(
        1,
        MAXIMUM_NUMBER_OUTGOING_RELS
      );
      for (int i = 1; i <= numberOutgoingRels; i++) {
        Node toNode = nodes.get(
          ActivityNode.transformToNodeId(
            RandomNumbers.randomNumberExcludingOne(
              numberStartingNodes + 1,
              numberNodesInt,
              Integer.parseInt(
                (
                  currentNode
                    .getProperty(
                      ActivityNode.getIdKey(),
                      ActivityNode.getDefaultId()
                    )
                    .toString()
                ).substring(1)
              )
            )
          )
        );

        // if no relationship between currentNode and toNode exists then create it
        if (!RelationshipUtility.existsRelationship(currentNode, toNode)) {
          currentNode.createRelationshipTo(
            toNode,
            MyRelationshipTypes.PRECEDES
          );
          unvisitedNodes.remove(toNode);
        }

        // if the toNode has not been dealt with (has not received outgoing rels yet) add it to the currentNodes list
        if (!visitedNodes.contains(toNode) && !currentNodes.contains(toNode)) {
          currentNodes.add(toNode);
        }
      }
      // after adding outgoing relationships to currentNode put it into the visitedNodes list so that it is not dealt with again
      if (currentNode.hasRelationship(Direction.OUTGOING)) {
        visitedNodes.add(currentNode);
      } else {
        currentNodes.add(currentNode);
      }
    }

    IdToNodeMap graph = IdToNodeMap.fetchGraph(tx);

    logger.log(Level.INFO, "Graph: \n {0}", graph.print2Str());
  }

  /* This procedure creates a UserActionHistory node that contains numberUserActions many random User Actions.*/
  @Procedure(mode = Mode.WRITE, name = CREATE_USER_ACTION_NODE)
  @Description("create one node with all user activities")
  public void createUANode(@Name("numberUserActions") long numberUserActions) {
    // Fetch graph from DB
    IdToNodeMap graph = IdToNodeMap.fetchGraph(tx);
    logger.log(Level.INFO, "Graph: \n {0} ", graph.print2Str());

    // create a random UserActionHistory
    UserActionHistory uah = UserActionHistory.createRandom(
      graph,
      numberUserActions
    );

    logger.log(
      Level.INFO,
      "Object before Serialization: \n {0}",
      uah.print2Str()
    );

    // serialize UserActionHistory object into a json string
    String json = "";
    try {
      json = JsonConversion.toJson(uah);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // create UserActionHistory node in db and set the json string as the changeLog property
    Node userActionNode = tx.createNode();
    userActionNode.setProperty(UserActionHistoryNode.getChangeLogKey(), json);
    userActionNode.addLabel(MyLabels.UserActionHistory);
    userActionNode.setProperty(
      UserActionHistoryNode.getUserActionNodeKey(),
      true
    );
    userActionNode.setProperty(
      UserActionHistoryNode.getIdKey(),
      UserActionHistoryNode.getDefaultId()
    );
    userActionNode.setProperty(
      UserActionHistoryNode.getNameKey(),
      UserActionHistoryNode.getDefaultName()
    );
  }

  /* This procedure applies all changes that are recorded in the 
  UserActionHistory node which the procedure itself fetches to the graph.*/
  @Procedure(mode = Mode.WRITE, name = APPLY_USER_ACTION_HISTORY)
  @Description("apply all User Actions to the graph")
  public void applyUserActionHistoryToGraph() {
    Node userActionNode = tx.findNode(
      MyLabels.UserActionHistory,
      UserActionHistoryNode.getUserActionNodeKey(),
      true
    );
    UserActionHistory uah = new UserActionHistory();

    try {
      uah =
        JsonConversion.toUserActionHistory(
          userActionNode
            .getProperty(UserActionHistoryNode.getChangeLogKey(), "")
            .toString()
        );
      logger.log(
        Level.INFO,
        "UAH in application method: \n {0}",
        uah.print2Str()
      );
    } catch (Exception e) {
      e.printStackTrace();
    }

    IdToNodeMap graph = IdToNodeMap.fetchGraph(tx);
    logger.log(
      Level.INFO,
      "Graph in application method: \n {0}",
      graph.print2Str()
    );

    uah.applyToGraph(graph);
  }

  /* This procedure applies all changes that are recorded in the 
  UserActionHistory node which is provided to the procedure to the graph.*/
  @Procedure(mode = Mode.WRITE, name = APPLY_USER_ACTION_HISTORY_WITH_NODE)
  @Description("apply all User Actions to the graph")
  public void applyUserActionHistoryToGraph(
    @Name("UserActionHistoryNode") Node userActionHistoryNode
  ) {
    // create new UserActionHistory object
    UserActionHistory uah = new UserActionHistory();

    // transform the json String of the userActionHistory node into the UserActionHistory object
    try {
      uah =
        JsonConversion.toUserActionHistory(
          userActionHistoryNode
            .getProperty(UserActionHistoryNode.getChangeLogKey(), "")
            .toString()
        );
    } catch (Exception e) {
      e.printStackTrace();
    }

    // fetch all Activity nodes from the graph database and put them into a Hashmap
    IdToNodeMap graph = IdToNodeMap.fetchGraph(tx);

    // apply all User Actions from the User Action History to the graph
    uah.applyToGraph(graph);
  }

  /* This procedure reverts the graph back to the state where there were 
  no user actions yet. This means that the properties that are changed on 
  Activity Nodes by the application of User Actions are removed and the 
  UserActionHistory Node is also removed.*/
  @Procedure(mode = Mode.WRITE, name = REVERT_GRAPH)
  @Description("reverts graph")
  public void revertGraph() {
    // remove user actions from Activity nodes
    IdToNodeMap graph = IdToNodeMap.fetchGraph(tx);
    graph.removeUserActions();

    // remove User Action Node
    Node userActionNode = tx.findNode(
      MyLabels.UserActionHistory,
      UserActionHistoryNode.getUserActionNodeKey(),
      true
    );
    userActionNode.delete();
  }

  /* This private function generates ActivityNodes of the provided 
  number and return a Hashmap containning them all */
  private HashMap<String, Node> createActivityNodes(int numberNodes) {
    HashMap<String, Node> nodes = new HashMap<>();
    String nodeName;
    String nodeId;
    // Create the provided number of nodes
    for (int i = 1; i <= numberNodes; i++) {
      Node newNode = tx.createNode();
      nodeName = ActivityNode.transformToNodeName(i);
      nodeId = ActivityNode.transformToNodeId(i);
      newNode.setProperty(ActivityNode.getNameKey(), nodeName);
      newNode.setProperty(ActivityNode.getIdKey(), nodeId);
      newNode.addLabel(MyLabels.Activity);
      nodes.put(nodeId, newNode);
    }

    return nodes;
  }
}
