/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.dataaccesslayer;

import uk.ac.ncl.ssip.queryframework.StepDescription;
import java.util.List;
import java.util.Map;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;

/**
 *
 * @author Matt2
 */
public interface BackendInterface {

    /*
    * create database connection 
    */
    public void initialiseDatabaseConnection();

    /* 
    * add node to opperations list. Does not need commiting to the database 
    * as batch commit is more efficient, depending on commit size. 
    */
    public void addNode(MetaDataInterface node);

    /*
    * commits current update list to database. 
    */
    public void commitTransaction();

    /* 
    * Commits final update to database connection and close connection cleanly. 
    */
    public void finaliseDatabaseConnection();

    /*
    * deletes database in its entirety. This is to be used before regenerating a 
    * database to ensure no concurrency problems. 
    */
    public void clearDatabase(String dbname);

    /* 
    * This method should exhaustively search and return all entities in a 
    * database. The return structure should be a map with a keyset containing 
    * all types. This maps to another map with a keyset containing all ids for 
    * that type. The ids will map to an object containing information about the 
    * entity. 
    */
    public Map<String, MetaDataInterface> returnAllNodesMap();    

    /*
    Method will start from start node identified by type-id pair and return nodes 
    in the neighborhood to depth as defined in constructor with 
    * Map<type, Map<id, properties>> as in itterateall mathod. 
    */
    public Map<String, MetaDataInterface> traversal(MetaDataInterface startNode, StepDescription... steps);
    public Map<String, MetaDataInterface> traversal(StepDescription... steps);

    /*
    * Simply return the name of the database. 
    */
    public String getDatabaseName();
    
    
}
