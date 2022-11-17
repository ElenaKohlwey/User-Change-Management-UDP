# User Change Management Study

This compilation of user-defined procedures implements several different ways on how to model user change in a growing knowledge graph application.
This project is used for determining which variant is the best option for such applications.

## Getting started

Clone the project. Clean and Build. Put the jar file into a Neo4j database plugin folder.
Opening the Neo4j Browser in the same database you can now call the procedures:
```
CALL org.rle.ucms.generateGraph(100)
CALL org.rle.ucms.createUserActionNode(10)
CALL org.rle.ucms.applyUserActionHistory()
```
The above calls will 
1. produce a connected graph with 100 Activity nodes.
2. produce one UserActionNode with 10 random User Actions.
3. Apply the User Actions from the UserActionNode to the Activity nodes.
