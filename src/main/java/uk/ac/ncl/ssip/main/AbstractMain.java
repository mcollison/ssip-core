/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.main;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ncl.ssip.dataaccesslayer.BackendInterface;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.parsers.KeggOrthologyParser;
import uk.ac.ncl.ssip.parsers.ParserInterface;
import uk.ac.ncl.ssip.parsers.RdfImporter;
import uk.ac.ncl.ssip.queryframework.CustomQuery;
import uk.ac.ncl.ssip.queryframework.QueryAbstractClass;
import uk.ac.ncl.ssip.webserver.FrontendInterface;
import uk.ac.ncl.ssip.webserver.SSIPWebServer;

/**
 *
 * @author matt
 */
public abstract class AbstractMain extends Object {

    protected BackendInterface DAO;
    protected FrontendInterface webserver;
    protected static List<ParserInterface> parsers = new ArrayList<ParserInterface>() {
        {
            add(new KeggOrthologyParser());
            add(new RdfImporter());
        }
    };
    protected static List<QueryAbstractClass> queries = new ArrayList<QueryAbstractClass>();

    public static void main(String[] args) {
        try {
//            BackendInterface db = new Neo4J();
            (new Thread(new SSIPWebServer(parsers, queries))).start();
            Desktop.getDesktop().browse(new URL("http://localhost/BioSSIPv0.1").toURI());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
