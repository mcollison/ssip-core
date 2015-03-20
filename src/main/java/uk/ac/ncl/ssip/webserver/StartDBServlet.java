/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.webserver;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.export.GephiExporter;
import uk.ac.ncl.ssip.parsers.ParserInterface;
import uk.ac.ncl.ssip.queryframework.ExampleQuery;
import uk.ac.ncl.ssip.queryframework.QueryInterface;

@MultipartConfig
public class StartDBServlet extends HttpServlet {

    private final static Logger LOGGER
            = Logger.getLogger(StartDBServlet.class.getCanonicalName());
    Collection<Part> fileParts;
    int fileNum = 0;

    public StartDBServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        resultsWebPage(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        fileParts = request.getParts();

        //write uploaded files to uploads folder
        processFiles();

        //complete data handling 
        dataHandling(request);

        //generate results web page 
        resultsWebPage(response);

        //run canned queries
        runQueries();

    }

    private void processFiles() throws IOException {

        String path = new File(".").getCanonicalPath() + "/uploads/";
        System.out.println(path);

        OutputStream out = null;
        InputStream filecontent = null;

        try {
            for (Part filePart : fileParts) {
                if (filePart.getName().contains("file")) {
                    fileNum++;
                    if (new File(path).list().toString().contains(getFileName(filePart))) {
                        out = new FileOutputStream(new File(path + File.separator
                                + getFileName(filePart)));
                        filecontent = filePart.getInputStream();

                        int read = 0;
                        final byte[] bytes = new byte[1024];

                        while ((read = filecontent.read(bytes)) != -1) {
                            out.write(bytes, 0, read);
                        }
                        //APPEND TO LOG FILE NOT CONSOLE
                        System.out.println("New file " + getFileName(filePart) + " created at " + path);
                        LOGGER.log(Level.INFO, "File{0}being uploaded to {1}",
                                new Object[]{getFileName(filePart), path});

                    } else {
                        System.out.println("File already exists in uploads: " + getFileName(filePart));
                    }
                }
            }

        } catch (FileNotFoundException fne) {
            System.out.println("You either did not specify a file to upload or are "
                    + "trying to upload a file to a protected or nonexistent "
                    + "location.");
            System.out.println("<br/> ERROR: " + fne.getMessage());

            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}",
                    new Object[]{fne.getMessage()});
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }
    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private void dataHandling(HttpServletRequest request) throws ServletException, IOException {
        //configure embedded database 
        String fileRoot = new File(".").getCanonicalPath();
        File dbroot = new File(fileRoot + "/neo4j-databases/");
        int dbversion = 1;
        for (int i = 0; i < dbroot.list().length; i++) {
            dbversion++;
        }
        Neo4J backend = new Neo4J(fileRoot + "/neo4j-databases/BioSSIPv0.1." + dbversion, fileRoot + "/public_html/results/performance");
        backend.initialiseDatabaseConnection();
        backend.createIndex();

        //parse each file uploaded using parsers specified 
        for (int i = 0; i < fileNum; i++) {

            String fileName = getFileName(request.getPart("file" + i));
//            String fileNameIndex = fileName.substring(fileName.length() - 1);

            String inputParser = request.getParameter("parser" + i);
            System.out.println(inputParser);
            ParserInterface parserObj = null;
            try {
                parserObj = (ParserInterface) Class
                        .forName("uk.ac.ncl.ssip.parsers." + inputParser)
                        .newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            parserObj.setFilepath(fileRoot + "/uploads/" + fileName);
            parserObj.setHandler(backend);
            parserObj.parseFile();
        }

//        //query database REDUNDANT
//        System.out.println("export all code starts here...");
//        GephiExporter gexfExportSubgraph = new GephiExporter();
//
//        String graphFile = fileRoot + "/public_html/results/gephi_export_all.gexf";
//
//        gexfExportSubgraph.export(backend.returnAllNodesMap(), graphFile);
        //close database again 
        backend.finaliseDatabaseConnection();

    }

    private void resultsWebPage(HttpServletResponse response) {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            Scanner sc = new Scanner(new File("./public_html/results.html"));
            while (sc.hasNextLine()) {
                String str = sc.nextLine();

                if (str.contains("<!--insert pipeline information-->")) {
                    System.getProperties().list(response.getWriter());
                }

                if (str.contains("<!--insert performance json data-->")) {
                    Scanner sc2 = new Scanner(new File("./public_html/results/performance.json"));
                    while (sc2.hasNextLine()) {
                        response.getWriter().println(sc2.nextLine());
                    }
                }

                if (str.contains("Untitled.gexf")) {
                    //REDUNDANT 
                    //NEEDS REPLACING WITH METADATA GRAPH
                    str = str.replace("Untitled.gexf", "./results/export_all.gexf");
                }
                response.getWriter().println(str);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void runQueries() {
        QueryInterface queryClass=null;
        File classDir = new File("./target/classes/uk/ac/ncl/ssip/queryframework/");
        String[] classArr = classDir.list();
        for (int i = 0; i < classArr.length - 1; i++) {
            //remove example queries and interface
            if (!(classArr[i].contains("Interface")
                    || classArr[i].contains("CustomQuery")
                    || classArr[i].contains("ExampleQueryStrategy"))) {

                try{
                queryClass = (QueryInterface) Class
                        .forName("uk.ac.ncl.ssip.queryframework." + classArr[i].replace(".class", ""))
                        .newInstance();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                queryClass.query();
            }

        }
    }
}
