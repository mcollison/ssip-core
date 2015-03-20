/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.export.GephiExporter;
import uk.ac.ncl.ssip.export.RdfExporter;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;

/**
 *
 * @author Matt2
 */
public class ExportAllServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //query database
        System.out.println("export all code starts here...");
        String fileRoot = new File(".").getCanonicalPath();
        String graphFile = fileRoot + "/public_html/results/export_all.gexf";

        if (!new File(fileRoot + "/public_html/results/").list().toString().contains("export_all.gexf")) {

            File dbroot = new File(fileRoot + "/neo4j-databases/");
            int dbversion = 0;
            for (int i = 0; i < dbroot.list().length; i++) {
                dbversion++;
            }
            Neo4J backend = new Neo4J(fileRoot + "/neo4j-databases/BioSSIPv0.1." + dbversion, fileRoot + "/public_html/results/performance");
            backend.initialiseDatabaseConnection();
            backend.syncIndexes(10);

            Map<String, MetaDataInterface> allNodes = backend.returnAllNodesMap();

            GephiExporter gexfExportSubgraph = new GephiExporter();
            gexfExportSubgraph.export(allNodes, graphFile);

            /* THIS CODE NEEDS SOME RESTRUCTURING
             RdfExporter rdfExportSubgraph = new RdfExporter();
             String rdfFile = fileRoot + "/public_html/results/export_all.rdf";
             gexfExportSubgraph.export(allNodes, rdfFile);
             */
            //close database again 
            backend.finaliseDatabaseConnection();
        }
        FileInputStream fis = null;
        OutputStream out = null;
        try {

            fis = new FileInputStream(graphFile);
            response.setContentType("application/octet-stream");

            out = response.getOutputStream();
            IOUtils.copy(fis, out); // this is using apache-commons, 
            // make sure you provide required JARs

        } finally {

            IOUtils.closeQuietly(out);  // this is using apache-commons, 
            IOUtils.closeQuietly(fis);  // make sure you provide required JARs

        }
    }

}

