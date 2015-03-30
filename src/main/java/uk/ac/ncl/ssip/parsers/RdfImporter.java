/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.parsers;

import uk.ac.ncl.ssip.export.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.riot.RDFDataMgr;
import uk.ac.ncl.ssip.dataaccesslayer.BackendInterface;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 *
 * Class takes an RDF graph and uses this to create a bioSSIP representation of
 * the graph.
 *
 * <TODO> Update to use the rdf-util package from Goksel and Curtis (now added
 * to project pom)
 */
public class RdfImporter implements ParserInterface{

    private String file;
    private Model rdfModel;
    private BackendInterface backend;
    private final static Logger LOGGER = Logger.getLogger(RdfImporter.class.getName());

    public RdfImporter(){
    }
    
    public RdfImporter(String file, BackendInterface backend) {
        this.file = file;
        this.rdfModel = RDFDataMgr.loadModel(file);
        this.backend = backend;
    }
    

    public void parseFile() {

        LOGGER.log(Level.INFO, "Pulling graph into SSIP from ...{0}", file);

        backend.initialiseDatabaseConnection();
        backend.createIndex();
        // list the statements in the Model
        StmtIterator iter = rdfModel.listStatements();


        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();

            if (subject instanceof Resource) {
                System.out.print("NODE sub: " + subject.toString());
            } else {
                // object is a literal
                System.out.print("VALUE sub:" + " \"" + subject.toString() + "\"");
            }

            System.out.println(" " + predicate.toString() + " ");
            
            if (object instanceof Resource) {
                System.out.print("NODE object: " + object.toString());
            } else {
                // object is a literal
                System.out.print("VALUE object:" + " \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }

        backend.commitTransaction();
        backend.finaliseDatabaseConnection();

        LOGGER.log(Level.INFO, "Finished parsing graph ...");

    }

    @Override
    public void setFilepath(String filepath) {
        this.file=filepath;
    }

    @Override
    public void setHandler(BackendInterface handler) {
        this.backend=handler;
    }
}
