 /*
 * Copyright(c) 2014 Matthew Collison. 
 */
package uk.ac.ncl.ssip.parsers;

import java.io.File;
import java.util.Scanner;
import uk.ac.ncl.ssip.dataaccesslayer.BackendInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;
import uk.ac.ncl.ssip.metadata.SSIPRelation;


/**
 *
 * @author Matthew Collison
 * http://www.ncl.ac.uk/computing/people/student/m.g.collison
 *
 */
public class KeggOrthologyParser extends Object implements ParserInterface{

    private String filepath;
    private Scanner sc;
    private BackendInterface handler;
    private String rel = "knows";

    public KeggOrthologyParser(String filepath, BackendInterface handler) {
        this.filepath = filepath;
        this.handler = handler;
    }

    public KeggOrthologyParser(){
        
    }
    /*
     potential extension to constructor that allows ontology mapping to be done 
     outside of the parser. 
     */

    public void parseFile() {

        try {
            //load file into scanner 
            sc = new Scanner(new File(filepath));

            //read each line (use for loop while testing)
            SSIPNode a = null;
            SSIPNode b = null;
            SSIPNode c = null;
            SSIPNode d = null;
            while (sc.hasNextLine()) {
//            for (int i = 0; i < 150; i++) {
                String line = sc.nextLine();
                if (line.startsWith("A")) {
                    String[] splits = line.split(">");
//                    System.out.println(splits[1].substring(0, splits[1].length() - 3));
                    String name = splits[1].substring(0, splits[1].length() - 3);
                    a = new SSIPNode("KOlevel1", name);
//                    a.addRelation(ref, SSIPNode.RelTypes.KNOWS);
                    handler.addNode(a);
                }
                if (line.startsWith("B")) {
                    String[] splits = line.split(">");
                    if (splits.length != 1) {
//                        System.out.println(splits[1].substring(0, splits[1].length() - 3));
                        String name = splits[1].substring(0, splits[1].length() - 3);
                        b = new SSIPNode("KOlevel2", name);
                        b.addRelation(a,  new SSIPRelation(rel, 1));
                        handler.addNode(b);

                    }
                }
                if (line.startsWith("C")) {
                    String[] splits = line.split(" +");
                    String description = "";
                    for (int j = 2; j < splits.length; j++) {
                        description = description + splits[j] + " ";
                    }
//                    System.out.println(splits[1] + "\t" + description);
                    String name = splits[1];
                    c = new SSIPNode("KOlevel3", name);
                    c.addRelation(b, new SSIPRelation(rel, 2));
                    handler.addNode(c);
                }
                if (line.startsWith("D")) {
                    String[] splits = line.split(" +");
                    String description = "";
                    for (int j = 2; j < splits.length; j++) {
                        description = description + splits[j];
                    }
                    String name = splits[1];
                    d = new SSIPNode("KOlevel4", name);
//                    System.out.println(name);
                    d.addRelation(c,  new SSIPRelation(rel, 3));
                    handler.addNode(d);
                }
            }
            handler.commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public String toString(){
        return "String to show ClassLoader is working";
    }

    @Override
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    
    @Override
    public void setHandler(BackendInterface handler) {
        this.handler = handler;
    }    
    
}
