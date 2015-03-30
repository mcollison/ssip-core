/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.main;

import uk.ac.ncl.ssip.dataaccesslayer.BackendInterface;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.metadata.SSIPNode;
import uk.ac.ncl.ssip.metadata.SSIPRelation;
import java.util.Random;
import uk.ac.ncl.ssip.export.GephiExporter;

/**
 *
 * @author Matt2
 */
public class PerformanceMain {

    static String fileRoot = "D://BioSSIP/performanceGraphs/";
    static String version = "0.1";

    public static void main(String[] args) {

        Neo4J gh;
        int graphSize = 1000;

        PerformanceMain pm = new PerformanceMain();

        gh = new Neo4J(
                fileRoot + "neo4j-databases/" + version + "_linear_" + graphSize,
                fileRoot + "results/performance-" + version + "_linear_" + graphSize + ".txt");
        pm.createLinearGraph(gh, graphSize);

        gh = new Neo4J(
                fileRoot + "neo4j-databases/" + version + "_conected_" + graphSize,
                fileRoot + "results/performance-" + version + "_conected_" + graphSize + ".txt");
        pm.createConnectedGraph(gh, graphSize);

        gh = new Neo4J(
                fileRoot + "neo4j-databases/" + version + "_scalefree_" + graphSize,
                fileRoot + "results/performance-" + version + "_scalefree_" + graphSize + ".txt");
        pm.createScaleFreeGraph(gh, graphSize);

    }

    public void createLinearGraph(Neo4J handler, int graphSize) {

        handler.initialiseDatabaseConnection();
        handler.createIndex();
        
        SSIPNode node0 = new SSIPNode("type", "no0");
        handler.addNode(node0);

        for (int i = 1; i < graphSize; i++) {
            SSIPNode node = new SSIPNode("type", "no" + i);
            node.addRelation(new SSIPNode("type", "no" + (i - 1)), new SSIPRelation("KNOWS"));
            handler.addNode(node);
        }
        handler.finaliseDatabaseConnection();

        handler.initialiseDatabaseConnection();
        
        GephiExporter gexfExportSubgraph = new GephiExporter();
        gexfExportSubgraph.export(handler.returnAllNodesMap(),
                fileRoot + "results/" + version + "_" + graphSize + "_linear.gexf");

        handler.finaliseDatabaseConnection();

    }

    public void createConnectedGraph(Neo4J handler, int graphSize) {

        handler.initialiseDatabaseConnection();
        handler.createIndex();

        SSIPNode node0 = new SSIPNode("type", "no0");
        handler.addNode(node0);

        for (int i = 1; i < graphSize; i++) {
            SSIPNode node = new SSIPNode("type", "no" + i);
            for (int j = 0; j < i; j++) {
                node.addRelation(new SSIPNode("type", "no" + j), new SSIPRelation("KNOWS"));
            }
            handler.addNode(node);
        }
        
        handler.finaliseDatabaseConnection();
        
        handler.initialiseDatabaseConnection();
        
        GephiExporter gexfExportSubgraph = new GephiExporter();
        gexfExportSubgraph.export(handler.returnAllNodesMap(),
                fileRoot + "results/" + version + "_" + graphSize + "_connected.gexf");

        handler.finaliseDatabaseConnection();

    }

    public void createScaleFreeGraph(Neo4J handler, int graphSize) {

        handler.initialiseDatabaseConnection();
        handler.createIndex();

        SSIPNode node0 = new SSIPNode("type", "no0");
        handler.addNode(node0);
        Random rand = new Random();

        for (int i = 1; i < graphSize; i++) {
            SSIPNode node = new SSIPNode("type", "no" + i);
            node.addRelation(new SSIPNode("type", "no" + rand.nextInt(i)), new SSIPRelation("KNOWS"));
            handler.addNode(node);
        }
        
        handler.finaliseDatabaseConnection();

        handler.initialiseDatabaseConnection();
        
        GephiExporter gexfExportSubgraph = new GephiExporter();
        gexfExportSubgraph.export(handler.returnAllNodesMap(),
                fileRoot + "results/" + version + "_" + graphSize + "_scalefree.gexf");

        handler.finaliseDatabaseConnection();

    }
}
