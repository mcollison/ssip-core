/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.performance;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;

/**
 *
 * @author Matt2
 */
public class Performance {

    private long startTime;
    private long connectionTime;
    private long transactionTime;
    private long totalTransactionTime=0;
    private long endTime;
    private int size = 0;
    private PrintWriter writer = null;
    private PrintWriter jsonwriter = null;

    public Performance() {

        //log commit size, dependency versions, operating system, computer specification etc
    }

    public Performance(String performanceReportPath) {
        try {
            writer = new PrintWriter(performanceReportPath + ".txt", "UTF-8");
            jsonwriter = new PrintWriter(performanceReportPath + ".json", "UTF-8");
            jsonwriter.print("var data = {\n"
                    + "\t\"xScale\": \"linear\",\n"
                    + "\t\"yScale\": \"linear\",\n"
                    + "\t\"type\": \"line\",\n"
                    + "\t\"main\": [\n"
                    + "\t{\n"
                    + "\t\t\"className\": \".build\",\n"
                    + "\t\t\"data\": [\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            writer = null;
        }
    }

    public void generatePerformanceReport() {
    }

    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public void setConnectionTime() {
        this.connectionTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
//        output(connectionTime);
    }

    public void setTransactionTime() {
        this.transactionTime = System.currentTimeMillis() - startTime ;
        startTime = System.currentTimeMillis();
        output(transactionTime);
    }

    public void setEndTime() {
        this.endTime = System.currentTimeMillis() - startTime;
//        output(endTime);
        if (writer != null) {
            writer.close();
            jsonwriter.print(
                    "\t\t]\n"
                    + "\t}\n"
                    + "\t]\n"
                    + "}");
            jsonwriter.close();

        }
    }

    public void output(long time) {
        if (writer == null) {
            System.out.println(time);
        } else {
            System.out.println(time);
            writer.println(time);
            jsonwriter.print(
                    "\t\t{\n"
                    + "\t\t\t\"x\":" + size + ",\n"
                    + "\t\t\t\"y\":" + time + ",\n"
                    + "\t\t},\n");
            size = size + Neo4J.commitSize;

        }
    }
}
