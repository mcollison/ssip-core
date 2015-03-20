package uk.ac.ncl.ssip.parsers;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import uk.ac.ncl.ssip.dataaccesslayer.BackendInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;
import uk.ac.ncl.ssip.metadata.SSIPRelationType;

public class UniProtSwissProt {

    private String filepath;
    private Scanner sc;
    private BackendInterface handler;

    public UniProtSwissProt(String filepath, BackendInterface handler) {
        this.filepath = filepath;
        this.handler = handler;
    }

    public void parseFile() {

        try {
            //load file into scanner 
            sc = new Scanner(new File(filepath));

            SSIPNode uniProtNode = null;
            SSIPNode uniProtRoot = new SSIPNode("UniProt", "UniProt_root");
            // reads to the end of the stream 
//            while (sc.hasNextLine()) {

            for (int i = 0; i < 20000; i++) {

                String line = sc.nextLine();

                if (line.startsWith("AC")) {
                    String acc = line.substring(5, 11);
                    uniProtNode = new SSIPNode("UniProt", acc);
                    uniProtNode.addRelation(uniProtRoot, new SSIPRelationType("Source"));
                }

                if (line.startsWith("//")) {
                    handler.addNode(uniProtNode);
//                    System.out.println(uniProtNode.getId() + "added to buffer");
                    uniProtNode = null;
                }

                if (line.startsWith("DR")) {
//                    if (line.contains("KO; ")){
//                        System.out.println(line);
//                    }
                    if (line.substring(5, 9).equals("KO; ")) {
                        String KOterm = line.split("KO; ")[1].substring(0, 6);
                        uniProtNode.addRelation(new SSIPNode("KOlevel4", KOterm), new SSIPRelationType("KNOWS"));
//                        System.out.println(KOterm);
                    }
                }

            }
            handler.commitTransaction();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
