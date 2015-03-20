/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.webserver;

import java.net.URL;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.openide.util.Exceptions;
import java.awt.Desktop;
import javax.servlet.MultipartConfigElement;
import org.eclipse.jetty.server.Connector;

/**
 *
 * @author Matt2
 */
public class SSIPWebServer implements Runnable, FrontendInterface {

    private String fileBase;

    public SSIPWebServer(){
    }
    
    public SSIPWebServer(String fileBase) {
        this.fileBase = fileBase;
    }

    public static void main(String[] args) {
        //start web server in new thread
        try {
            String fileBase = "./../public_html/";//"D://mni_files/sampleData/";
            (new Thread(new SSIPWebServer(fileBase))).start();
            Desktop.getDesktop().browse(new URL("http://localhost:8080/BioSSIPv0.1").toURI());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        Server server = new Server(8080);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});
        resource_handler.setResourceBase("./public_html/");

        //initialise servlet context handler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        //add servlets with holders to context handler
        //NOT SURE WHAT "data/tmp" argument does
        ServletHolder fileUploadServletHolder = new ServletHolder(new StartDBServlet());
        fileUploadServletHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement("data/tmp"));
        context.addServlet(fileUploadServletHolder, "/build");

        ServletHolder queryServletHolder = new ServletHolder(new QueryDBServlet());
        queryServletHolder.getRegistration().setMultipartConfig(
                new MultipartConfigElement("data/tmp"));
        context.addServlet(queryServletHolder, "/query");

        ServletHolder holder1 = new ServletHolder(new IndexServlet());
        context.addServlet(holder1, "/BioSSIPv0.1");
        
        ServletHolder exportallGexf = new ServletHolder(new ExportAllServlet());
        context.addServlet(exportallGexf, "/exportall.gexf");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context, new DefaultHandler()});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
