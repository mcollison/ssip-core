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
import uk.ac.ncl.ssip.parsers.ParserInterface;

public class IndexServlet extends HttpServlet
{

    List<ParserInterface> parsers;
    public IndexServlet(List<ParserInterface> parsers){
        this.parsers=parsers;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        Scanner sc = new Scanner(new File(SSIPWebServer.fileBase + "/index.html"));
        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.contains("//initialise options array with parsers")) {
                response.getWriter().print("var month = [");
                for(ParserInterface parser : parsers){
                    response.getWriter().print("\"" + parser.getClass().getSimpleName() + "\",");
                }
                response.getWriter().print("];");
            }
            response.getWriter().println(str);
        }

    }
}