package org.rle.ucms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

/**
 * @author ekohlwey
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateGraphTest {

  private static final Config driverConfig = Config
    .builder()
    .withoutEncryption()
    .build();
  private Driver driver;
  private Neo4j embeddedDatabaseServer;

  @BeforeAll
  void initializeNeo4j() throws IOException {
    this.embeddedDatabaseServer =
      Neo4jBuilders
        .newInProcessBuilder()
        .withDisabledServer()
        .withProcedure(Procedures.class)
        .build();

    driver =
      GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
    try (Session session = driver.session()) {
      session.run("CALL org.rle.ucms.generateGraph(50)");
    }
    System.out.println("Database initialized.");
  }

  @Test
  public void testPrintRels() {
    Result result;
    List<Record> recordList;

    try (Session session = driver.session()) {
      result =
        session.run(
          "MATCH (a)-[:PRECEDES]->(b) RETURN a.name + ' PRECEDES ' + b.name AS rel ORDER BY rel ASC"
        );
      recordList = result.list();

      for (Record record : recordList) {
        System.out.println(record.get("rel").asString());
      }

      assertTrue(!recordList.isEmpty());
    }
  }

  @Test
  public void testNoUnconnectedNodes() {
    int countUnconnectedNodes;

    try (Session session = driver.session()) {
      countUnconnectedNodes =
        session
          .run(
            "MATCH (a) WHERE NOT EXISTS ((a)-[:PRECEDES]->()) RETURN count(a) AS count"
          )
          .single()
          .get("count", 5);

      assertEquals(0, countUnconnectedNodes);
    }
  }

  @Test
  public void testNumberNodesCorrect() {
    int countNumberNodes;

    try (Session session = driver.session()) {
      countNumberNodes =
        session
          .run("MATCH (a) RETURN count(a) AS count")
          .single()
          .get("count", 0);

      assertEquals(50, countNumberNodes);
    }
  }

  @Test
  public void testNumberOutgoingEdgesCorrect() {
    int countNumberOutgoingEdges;

    try (Session session = driver.session()) {
      countNumberOutgoingEdges =
        session
          .run(
            "MATCH (a)-[s:PRECEDES]->(b) WITH a,count(s) AS countS RETURN max(countS) AS count"
          )
          .single()
          .get("count", 0);

      assertTrue(5 >= countNumberOutgoingEdges);
    }
  }

  @Test
  public void testNumberStartingNodesCorrect() {
    int countNumberStartingNodes;

    try (Session session = driver.session()) {
      countNumberStartingNodes =
        session
          .run(
            "MATCH (a) WHERE NOT EXISTS (()-[:PRECEDES]->(a)) RETURN count(a) AS count"
          )
          .single()
          .get("count", 0);

      assertTrue(10 >= countNumberStartingNodes);
    }
  }
}
