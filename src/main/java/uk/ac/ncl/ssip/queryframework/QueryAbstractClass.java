/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ncl.ssip.queryframework;

import java.io.File;
import java.util.Map;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.export.GephiExporter;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;

/**
 *
 * @author Matt2
 */
public abstract class QueryAbstractClass {
    protected String version = null;//default should be latest version but can be updated 
    protected String seedID;
    protected String seedType;
    protected StepDescription[] steps;


    public Map<String, MetaDataInterface> query() {
        Map<String, MetaDataInterface> subgraph2=null;
        try {
            String fileRoot = new File(".").getCanonicalPath();

            String graphFile = fileRoot + "/target/webapp/results/" + this.getClass().getSimpleName()+ ".gexf";

            System.out.println(graphFile);
            if (version == null) {
                version = "BioSSIPv0.1." + new File(fileRoot + "/neo4j-databases/").list().length;
            }
            
            System.out.println(version);
            
            Neo4J backend = new Neo4J(fileRoot + "/neo4j-databases/" + version, fileRoot + "/target/webapp/results/Qperformance-"
                    + this.getClass().getSimpleName() + version + ".txt");
            backend.initialiseDatabaseConnection();

            backend.syncIndexes(10);

            subgraph2 = backend.traversal(
                    new SSIPNode(seedType, seedID), steps);

            GephiExporter gexfExportSubgraph2 = new GephiExporter();

            gexfExportSubgraph2.export(subgraph2, graphFile);

            backend.finaliseDatabaseConnection();
        } catch (Exception ex) {
            System.err.println("Error retrieving canonical path!");
        }
        return subgraph2;

    }

    public String getSeedID() {
        return seedID;
    }

    public String getSeedType() {
        return seedType;
    }

    public StepDescription[] getSteps() {
        return steps;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSeedID(String seedID) {
        this.seedID = seedID;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public void setSteps(StepDescription[] steps) {
        this.steps = steps;
    }
    
}
