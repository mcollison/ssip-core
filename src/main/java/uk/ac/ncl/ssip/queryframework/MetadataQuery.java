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
public class MetadataQuery extends QueryAbstractClass{
        
    @Override 
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

            subgraph2 = backend.getMetadata();

            GephiExporter gexfExportSubgraph2 = new GephiExporter();

            gexfExportSubgraph2.export(subgraph2, graphFile);

            backend.finaliseDatabaseConnection();
        } catch (Exception ex) {
            System.err.println("Error retrieving canonical path!");
        }
        return subgraph2;

    }
}
