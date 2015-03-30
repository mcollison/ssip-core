/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.webserver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.ncl.ssip.queryframework.StepDescription;
import uk.ac.ncl.ssip.queryframework.CustomQuery;
import uk.ac.ncl.ssip.queryframework.QueryAbstractClass;

/**
 *
 * @author matt
 */
public class QueryDBServlet extends HttpServlet {
    protected List<QueryAbstractClass> queries;
    public QueryDBServlet(List<QueryAbstractClass> queries){
        this.queries= queries;
    }

    private CustomQuery custQ = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        resultsWebPage(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //setup custom query based on web form details 
        String seedID = request.getParameter("id");
        String seedType = request.getParameter("type");
        
        //NEED TO CHANGE THE FORM STRUCTURE TO ALLOW FOR MULTIPLE STEPS 
        int stepno = Integer.parseInt(request.getParameter("stepno"));//this variable isn't used yet
        int steps = Integer.parseInt(request.getParameter("steps"));
        boolean depth_first = Boolean.parseBoolean(request.getParameter("depth_first"));
        
        custQ = new CustomQuery(seedID,seedType
                ,new StepDescription[]{new StepDescription(steps,depth_first)});

        custQ.query();

        //generate results before rendering the page 
        resultsWebPage(response);
    }

    private void resultsWebPage(HttpServletResponse response) {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            Scanner sc = new Scanner(new File("./target/webapp/query.html"));
            while (sc.hasNextLine()) {
                String str = sc.nextLine();

                if (str.contains("<!-- insert canned queries here -->")) {

                    for (QueryAbstractClass query : queries) {

                        try{
                            response.getWriter().print(query.getClass().getSimpleName()
                                    + "\nSeed node type-id pair: \"" + query.getSeedID()
                                    + "\"\t \"" + query.getSeedType() + "\n"
                                    + "<table style=\"width:100%\">"
                                    + "<tr><td>Steps</td><td>Depth</td><td>Depth first?</td>\n"
                                    + "<td>Relationship type restrictions</td></tr>");
                            for (int j = 0; j < query.getSteps().length; j++) {
                                response.getWriter().print("<tr><td>" + j + "</td>"
                                        + "<td>" + query.getSteps()[j].getDepth() + "</td>"
                                        + "<td>" + query.getSteps()[j].isDepthFirst() + "</td>"
                                        + "<td>" + query.getSteps()[j].getRelations().toString() + "</td>"
                                        + "</tr>");
                            }
                            response.getWriter().print("</table><script>sigma.parsers.gexf("
                                    + "'./results/" + query.getClass().getSimpleName() + ".gexf',{"
                                    + "container: '" 
                                    + query.getClass().getSimpleName()
                                    + "sigma-container'},"
                                    + "function(s) {});"
                                    + "</script>\n"
                                    + "<div class=\"sigmacontainer\" id=\""
                                    + query.getClass().getSimpleName()
                                    + "sigma-container\""
                                    + "style = \"max-width: 800px; height: 400px; margin: auto;\""
                                    + "></div>");
                        }catch(IOException ioEx){
                            System.out.println("get writer failed in QueryDBServlet");
                            ioEx.printStackTrace();
                        }
                    }

                    //insert custom query results if they've been run
                    if (custQ != null) {
                        response.getWriter().print(custQ.getClass().getSimpleName()
                                + "\nSeed node type-id pair: \"" + custQ.getSeedID()
                                + "\"\t \"" + custQ.getSeedType() + "\n"
                                + "<table style=\"width:100%\">"
                                + "<tr><td>Steps</td><td>Depth</td><td>Depth first?</td>\n"
                                + "<td>Relationship type restrictions</td></tr>");
                        for (int j = 0; j < custQ.getSteps().length; j++) {
                            response.getWriter().print("<tr><td>" + j + "</td>"
                                    + "<td>" + custQ.getSteps()[j].getDepth() + "</td>"
                                    + "<td>" + custQ.getSteps()[j].isDepthFirst() + "</td>"
                                    + "<td>" + custQ.getSteps()[j].getRelations().toString() + "</td>"
                                    + "</tr>");
                        }
                        response.getWriter().print("</table><script>sigma.parsers.gexf("
                                + "'./results/" + custQ.getClass().getSimpleName() + ".gexf',{"
                                + "container: '"
                                + custQ.getClass().getSimpleName()
                                + "sigma-container'},"
                                + "function(s) {});"
                                + "</script>\n"
                                + "<div class=\"sigmacontainer\" id=\""
                                + custQ.getClass().getSimpleName()
                                + "sigma-container\" "
                                + "style = \"max-width: 800px; height: 400px; margin: auto;\""
                                + "></div>");
                    }
                }

                response.getWriter().println(str);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
