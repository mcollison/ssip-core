/*
 * Copyright(c) 2014 Matthew Collison. 
 */
package uk.ac.ncl.ssip.dataaccesslayer;

import uk.ac.ncl.ssip.queryframework.StepDescription;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;

/**
 *
 * @author Matthew Collison
 * http://www.ncl.ac.uk/computing/people/student/m.g.collison
 *
 */
public class JVM implements BackendInterface {

    List<MetaDataInterface> data = new ArrayList<MetaDataInterface>();
    Map<String, MetaDataInterface> dataMap = new HashMap<String, MetaDataInterface>();

    public void initialiseDatabaseConnection() {
        System.out.println("initalisation not needed with JVM back end");
    }

    public void addNode(MetaDataInterface node) {
        dataMap.put(node.getType() + node.getId(), node);
        data.add(node);
    }

    public void addEdge(MetaDataInterface nodeFrom) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void commitTransaction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void finaliseDatabaseConnection() {
        System.out.println("initalisation not needed with JVM back end");
    }

    public void clearDatabase(String dbname) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Map<String, MetaDataInterface>> itterateAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, List<String>> itterateAllTypeIdPairs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDatabaseName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Map<String, MetaDataInterface>> iterateAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<MetaDataInterface> returnAllNodes() {

        return data;

    }

    public Map<String, MetaDataInterface> returnAllNodesMap() {

        return dataMap;

    }

    @Override
    public Map<String, MetaDataInterface> traversal(MetaDataInterface startNode, StepDescription... steps) {
        return dataMap;
    }

    @Override
    public Map<String, MetaDataInterface> traversal(StepDescription... steps) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
