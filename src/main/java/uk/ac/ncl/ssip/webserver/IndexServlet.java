/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ncl.ssip.webserver;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class IndexServlet extends HttpServlet
{
    String greeting = "Hello";

    public IndexServlet()
    {
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        Scanner sc = new Scanner(new File("./public_html/index.html"));
        while (sc.hasNextLine()) {
            String str = sc.nextLine();
            if (str.contains("//initialise options array with parsers")) {
                File classDir = new File("./target/classes/uk/ac/ncl/ssip/parsers/");
                File[] classArr = classDir.listFiles();
                response.getWriter().print("var month = [");
                for(int i=0;i<classArr.length-1;i++){
//                    System.out.println(classArr[i].getName().replace(".class", ""));
                    response.getWriter().print("\"" + classArr[i].getName().replace(".class", "") + "\",");
                }
                response.getWriter().print("\"" + classArr[classArr.length-1].getName().replace(".class", "") + "\"];");
            }
            response.getWriter().println(str);
        }

    }
}