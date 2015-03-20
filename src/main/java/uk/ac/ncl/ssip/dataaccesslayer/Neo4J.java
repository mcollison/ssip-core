/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.dataaccesslayer;

import uk.ac.ncl.ssip.queryframework.StepDescription;
import uk.ac.ncl.ssip.performance.Performance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;
import uk.ac.ncl.ssip.metadata.SSIPRelationType;

/**
 *
 * @author Matt and Joe
 */
public class Neo4J implements BackendInterface {

    public static int commitSize = 1000;
    private String DB_PATH;
    private GraphDatabaseService graphDb;
    private boolean ref_created = true;
    private final String label_string = "label";
    private final String type_string = "type";
    private final String UID = "UID";
    private final String refnode_id = "ref node";
    private List<MetaDataInterface> nodeBuffer = new ArrayList<MetaDataInterface>();
    private static int transactions = 1;
    private Performance performance;

    public Neo4J(){
    }
    
    public Neo4J(String DB_PATH) {
        this.DB_PATH = DB_PATH;
        performance = new Performance();
    }

    public Neo4J(String DB_PATH, String performanceReportPath) {
        this.DB_PATH = DB_PATH;
        performance = new Performance(performanceReportPath);
    }

    @Override
    public void initialiseDatabaseConnection() {
        performance.setStartTime();
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        registerShutdownHook(graphDb);
        performance.setConnectionTime();

//return all live indexes and create UID index if not already there 
//        boolean uidIndex = true;
//        for (IndexDefinition index : graphDb.schema().getIndexes()) {
//            System.out.print(index.getLabel());
//            if (index.getPropertyKeys().iterator().next().equals(UID)){
//                uidIndex=false;
//            }
//        }
//        if (uidIndex){
//            createIndex();
//        }
        System.out.println("Sucesfully connected to graph");
        System.out.println("Graph location: " + getDatabaseName());
    }

    public void createIndex(String property) {
        IndexDefinition indexDefinition;
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = graphDb.schema();
            indexDefinition = schema.indexFor(DynamicLabel.label(label_string))
                    .on(property)
                    .create();
            tx.success();
        }
    }

    public void createIndex() {
        IndexDefinition indexDefinition;
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = graphDb.schema();
            indexDefinition = schema.indexFor(DynamicLabel.label(label_string))
                    .on(UID)
                    .create();
            tx.success();
        }
    }

    public void setCommitSize(int size) {
        this.commitSize = size;
    }

    public int getCommitSize() {
        return commitSize;
    }

    public void syncIndexes(long seconds) {
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = graphDb.schema();
            schema.awaitIndexesOnline(seconds, TimeUnit.SECONDS);
//                schema.awaitIndexOnline( indexDefinition, 10, TimeUnit.SECONDS );

            System.out.println("Live indexes in database...");
            for (IndexDefinition index : graphDb.schema().getIndexes()) {
                System.out.println(index.getLabel());
                System.out.println(index.getPropertyKeys().iterator().next());
            }
            tx.success();
        }
    }

    //need to add relations and check uniqueness
    @Override
    public void commitTransaction() {
        try (Transaction tx = graphDb.beginTx()) {
            // Create some users
            Node node = null;
            for (int id = 0; id < nodeBuffer.size(); id++) {
//                node = checkNode(UID, nodeBuffer.get(id).getId());
                node = checkNode(nodeBuffer.get(id).getId(), true);

                //add type property (do not index this)
                node.setProperty(type_string, nodeBuffer.get(id).getType());

                //add attributes
                for (String attribute : nodeBuffer.get(id).getAttributes().keySet()) {

                    //create equivalent property on neo4j node
//                    System.out.println(attribute + "\t" + nodeBuffer.get(id).getAttributes().get(attribute));
                    node.setProperty(attribute, nodeBuffer.get(id).getAttributes().get(attribute));

                }

                //add relations to neo4j node
                for (MetaDataInterface toNodeObject : nodeBuffer.get(id).getRelations().keySet()) {

                    //create/return equivalent neo4j node
//                    Node toNeoNode = checkNode(UID, toNodeObject.getId());
                    Node toNeoNode = checkNode(toNodeObject.getId(), true);

                    //return relationship connecting nodes
                    SSIPRelationType ssipRel = nodeBuffer.get(id).getRelations().get(toNodeObject);

                    Relationship rel = node.createRelationshipTo(toNeoNode, ssipRel);
                    for (String attribute : ssipRel.getAttributes().keySet()) {
                        rel.setProperty(attribute, ssipRel.getAttribute(attribute));
                    }

                }

            }
            if (ref_created && nodeBuffer.size() > 0) {
                Node refNode = graphDb.createNode(DynamicLabel.label(label_string));
                refNode.setProperty(UID, getRefSSIPnode().getId());
                refNode.createRelationshipTo(node, SSIPRelationType.RelTypes.KNOWS);
                ref_created = false;
            }
            tx.success();
            System.out.println("Node transaction commited [" + transactions + "]");
            performance.setTransactionTime();
            transactions++;
        }
    }

    @Override
    public void addNode(MetaDataInterface nodeObject) {
        nodeBuffer.add(nodeObject);
        if (nodeBuffer.size() > commitSize) {
            commitTransaction();
            nodeBuffer.clear();
        }
    }

    public Node checkNode(String value, boolean altId) {
        //warning: property should not be used 
        Node node = null;
        if (altId) {
            List<Node> nodes = new ArrayList<Node>();

            for (IndexDefinition index : graphDb.schema().getIndexes(DynamicLabel.label(label_string))) {

//                System.out.println(index.getLabel().toString());
//                System.out.println(index.getPropertyKeys().iterator().next());
                for (Node n : graphDb.findNodesByLabelAndProperty(
                        index.getLabel(),
                        index.getPropertyKeys().iterator().next(),
                        value)) {

//                    System.out.println(n);
                    if (n != null) {
                        nodes.add(n);
                    }
                }
            }
            if (nodes.isEmpty()) {
                node = graphDb.createNode(DynamicLabel.label(label_string));
                node.setProperty(UID, value);
                return node;
            } else {
                return nodes.get(0);
            }

        } else {
            return checkNode(UID, value);
        }
    }

    public Node checkNode(String property, String value) {
        Node node = null;
        //change this for a different node

        for (Node n : graphDb.findNodesByLabelAndProperty(
                DynamicLabel.label(label_string), property, value)) {
            node = n;
        }
        if (node == null) {
            node = graphDb.createNode(DynamicLabel.label(label_string));
            node.setProperty(property, value);
            return node;
        } else {
            return node;
        }
    }

    @Override
    public void finaliseDatabaseConnection() {
        commitTransaction();
        nodeBuffer.clear();
        System.out.println("Transaction closed");
        graphDb.shutdown();
        System.out.println("Database connection shutdown");
        performance.setEndTime();
    }

    public void finaliseDatabaseConnectionNoUPDATES() {
        syncIndexes(10);
        System.out.println("Transaction closed");
        graphDb.shutdown();
        System.out.println("Database connection shutdown");
        performance.setEndTime();
    }

    /*
     * only the UID and id properties of the start node have to be identical to seed the traversal
     * AIM: return all sub networks from starting seed nodes that follow the rules defined in the step descriptions 
     */
    public Map<String, MetaDataInterface> traversal(MetaDataInterface seedNode, StepDescription... steps) {

        Map<String, MetaDataInterface> returnNodes = new HashMap<String, MetaDataInterface>();

        List<Node> returnNeoNodes = new ArrayList<Node>();

        List<Node> seedNodes = new ArrayList<Node>();
        seedNodes.add(findNode(this.UID, seedNode.getId()));

        try (Transaction tx = graphDb.beginTx()) {
            for (int i = 0; i < steps.length; i++) {

                List<StepDescription.RelTypes> rels = steps[i].getRelations();

                //define the traversal rules for each step
                TraversalDescription td = graphDb.traversalDescription()
                        .breadthFirst()
                        .evaluator(
                        Evaluators.toDepth(steps[i].getDepth()));

                //add relationship rules to traversal if we have any
                if (!rels.isEmpty()) {
                    for (StepDescription.RelTypes rel : rels) {
                        td = td.relationships(rel);
                    }
                }

                List<Node> newSeedNodes = new ArrayList<Node>();
                for (Node seed : seedNodes) {
                    seed.getId();

                    //meet Trevor the travelling traverser 
                    Traverser trevor = td.traverse(seed);

                    //seed next step 
                    for (Node n : trevor.nodes()) {
                        returnNeoNodes.add(n);
                    }
                    //can be optimised as traversal includes paths to intermediate nodes in longer paths 
                    for (Path p : trevor) {
                        newSeedNodes.add(p.endNode());
//                        System.out.println("new seed node type and id pair: " + p.endNode().getProperty(UID));
                    }
                }
                seedNodes.clear();//not sure if this line can be deleted
                seedNodes.addAll(newSeedNodes);
                newSeedNodes.clear();

            }
            returnNodes = convertNeoNodesToSSIPNodes(returnNeoNodes);
        }

        return returnNodes;

    }

    public Map<String, MetaDataInterface> convertNeoNodesToSSIPNodes(Iterable<Node> neoNodes) {

        Map<String, MetaDataInterface> returnNodes = new HashMap<String, MetaDataInterface>();

        for (Node neonode : neoNodes) {

//            System.out.println("Neo4J node: " + neonode);
            //convert step result into SSIP subgraph to seed next step and to return
            SSIPNode nodeObject = new SSIPNode(UID, (String) neonode.getProperty(UID));

            //Add attributes code here 
            for (String attribute : neonode.getPropertyKeys()) {
                nodeObject.addProperty(attribute, neonode.getProperty(attribute));
            }

            //add relations 
            for (Relationship rel : neonode.getRelationships(Direction.OUTGOING)) {
                SSIPRelationType ssipRel = new SSIPRelationType(rel.getType().toString());
                for (String attribute : rel.getPropertyKeys()) {
                    ssipRel.addAttribute(attribute, rel.getProperty(attribute));
                }
                nodeObject.addRelation(
                        new SSIPNode(UID, (String) rel.getEndNode()
                        .getProperty(UID)),
                        ssipRel);
            }
            returnNodes.put(nodeObject.getType() + nodeObject.getId(), nodeObject);
        }
        return returnNodes;
    }


    /*
     This method starts the traversal at the reference node 
     */
    public Map<String, MetaDataInterface> traversal(StepDescription... steps) {
        return traversal(getRefSSIPnode(), steps);
    }

    @Override
    public String getDatabaseName() {
        return DB_PATH;
    }

    public Node findNode(String labelproperty, String value) {
        Node node = null;
        try (Transaction findNodeTx = graphDb.beginTx()) {
            try (ResourceIterator<Node> users = graphDb.findNodesByLabelAndProperty(DynamicLabel.label(label_string), labelproperty, value).iterator()) {
                ArrayList<Node> userNodes = new ArrayList<>();
                while (users.hasNext()) {
                    userNodes.add(users.next());
                }

                for (Node nodie : userNodes) {
                    node = nodie;
                }
            }
            findNodeTx.success();
        }
        if (node == null) {
            System.out.println("Unable to find node");
        }
        return node;
    }

    void shutDown() {
        System.out.println();
        System.out.println("Shutting down database ...");
        graphDb.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    @Override
    public Map<String, MetaDataInterface> returnAllNodesMap() {

        return returnAllNodesMap(getRefSSIPnode());

    }

    /*
     WARNING: THIS METHOD ONLY RETURNS NODES WITH AT LEAST ONE TRANSITIVE RELATION TO THE START NODE
     */
    public Map<String, MetaDataInterface> returnAllNodesMap(MetaDataInterface startNode) {

        Map<String, MetaDataInterface> returnNodes = new HashMap<String, MetaDataInterface>();

        Traverser trevor;
        try (Transaction tx = graphDb.beginTx()) {
            //neo4j traversal object
            TraversalDescription td = graphDb.traversalDescription()
                    .breadthFirst()
                    .evaluator(
                    Evaluators.all());

            trevor = td.traverse(findNode(UID, startNode.getId()));
            returnNodes = convertNeoNodesToSSIPNodes(trevor.nodes());

            tx.success();
        }

        return returnNodes;

    }

    public SSIPNode getSSIPNode(SSIPNode seedNode) {

        try (Transaction tx = graphDb.beginTx()) {
            Node found = null;
            found = findNode(this.UID, seedNode.getId());
            if (found == null) {
                //error
            } else {
                return convertNeoNodesToSSIPNodes(found);
            }
            return null;
        }
    }

    public SSIPNode convertNeoNodesToSSIPNodes(Node neoNode) {

        SSIPNode nodeObject = new SSIPNode(UID, (String) neoNode.getProperty(UID));

        //Add attributes code here 
        for (String attribute : neoNode.getPropertyKeys()) {
            nodeObject.addProperty(attribute, neoNode.getProperty(attribute));
        }
        //add relations 
        for (Relationship rel : neoNode.getRelationships(Direction.OUTGOING)) {
            SSIPRelationType ssipRel = new SSIPRelationType(rel.getType().toString());
            for (String attribute : rel.getPropertyKeys()) {
                ssipRel.addAttribute(attribute, rel.getProperty(attribute));
            }
            nodeObject.addRelation(
                    new SSIPNode(UID, (String) rel.getEndNode()
                    .getProperty(UID)),
                    ssipRel);
        }
        return nodeObject;

    }

    private MetaDataInterface getRefSSIPnode() {
        return new SSIPNode(UID, refnode_id);

    }

    /*
     * Check if relation exists between fromNode and toNode of UID relType
     */
    @Deprecated
    public boolean checkRelationshipExists(Node fromNode, Node toNode, RelationshipType relType) {

        //for all relations of relationship UID check if destination nodes match 
        for (Relationship rel : fromNode.getRelationships(relType, Direction.OUTGOING)) {
//        for (Relationship rel : fromNode.getRelationships(relType, Direction.OUTGOING)) {
            //    System.out.println(rel.getEndNode().getProperty(label));
            //   System.out.println(toNode.getProperty(label));
            //maybe this should be fromnode instead of toNode
            if (rel.getEndNode().getProperty(UID).equals(toNode.getProperty(UID))) {
                System.out.println("relation duplication check point");
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearDatabase(String dbname) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
