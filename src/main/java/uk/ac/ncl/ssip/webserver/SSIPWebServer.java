/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.webserver;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import uk.ac.ncl.ssip.parsers.ParserInterface;
import uk.ac.ncl.ssip.queryframework.QueryAbstractClass;
/**
 *
 * @author Matt2
 */
public class SSIPWebServer implements Runnable, FrontendInterface {

    public static String fileBase;
    private List<ParserInterface> parsers;
    private List<QueryAbstractClass> queries;

    public SSIPWebServer(){
    }
    
    public SSIPWebServer(List<ParserInterface> parsers, List<QueryAbstractClass> queries) {
        this.parsers=parsers;
        this.queries=queries;
    }

    public void run() {
        try{
            fileBase = new File("./target/webapp/").getCanonicalPath();
        }catch(IOException ex){
            System.out.println("Web server not working due to bad filepath.");
            ex.printStackTrace();
        }
        Server server = new Server(80);
        System.out.println(fileBase);
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase(fileBase);

        //initialise servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        //add servlets with holders to context handler
        //NOT SURE WHAT "data/tmp" argument does
        ServletHolder fileUploadServletHolder = new ServletHolder(new StartDBServlet(queries));
        fileUploadServletHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement("data/tmp"));
        context.addServlet(fileUploadServletHolder, "/build");

        ServletHolder queryServletHolder = new ServletHolder(new QueryDBServlet(queries));
        queryServletHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement("data/tmp"));
        context.addServlet(queryServletHolder, "/query");

        ServletHolder holder1 = new ServletHolder(new IndexServlet(parsers));
        context.addServlet(holder1, "/BioSSIPv0.1");
        
        ServletHolder exportallGexf = new ServletHolder(new GexfExportAllServlet());
        context.addServlet(exportallGexf, "/exportall.gexf");

        ServletHolder exportallRdf = new ServletHolder(new RdfExportAllServlet());
        context.addServlet(exportallRdf, "/exportall.rdf");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context, new DefaultHandler()});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
