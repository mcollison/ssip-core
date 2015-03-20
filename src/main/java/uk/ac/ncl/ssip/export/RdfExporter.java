/*
 * Copyright(c) 2014 Matthew Collison. 
 */
package uk.ac.ncl.ssip.export;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPRelationType;

/**
 *
 * @author Joe Mullen
 *
 * Class exports a SSIP graph to RDF using the Apache JENA library.
 *
 * <TODO> Update to use the rdf-util package from Goksel and Curtis (now added
 * to project pom)
 *
 */
public class RdfExporter {
    
    private Model rdfModel;
    private URI baseUri;
    private final static Logger LOGGER = Logger.getLogger(RdfExporter.class.getName());
    private Map<String, MetaDataInterface> jbeanMap;
    private String filename;
    
    public static void main(String[] args) {
        //test
        Neo4J graph = new Neo4J("test");
//        graph.initialiseDatabaseConnection();
//        graph.createIndex();
//        /**
//         * Toy Graph
//         */
//        SSIPNode one = new SSIPNode("test", "hello");
//        SSIPNode two = new SSIPNode("test", "world");
//        SSIPRelationType rel = new SSIPRelationType("testrel");
//        one.addRelation(two, rel);
//        graph.addNode(one);
//        graph.addNode(two);
        /**
         * Finished toy graph
         */
//        graph.commitTransaction();
//        graph.finaliseDatabaseConnection();
        /**
         * RDF exporter
         */        
        graph.initialiseDatabaseConnection();
        
        RdfExporter rdf = null;
        try {
            rdf = new RdfExporter(graph.returnAllNodesMap(), "output.xml", new URI("http://DReSMin/test"));
            rdf.export();
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        graph.finaliseDatabaseConnectionNoUPDATES();
        
    }

    /**
     * Takes a SSIP graph.
     *
     * @param handler
     */
    public RdfExporter(Map<String, MetaDataInterface> nodeMap, String filename, URI baseURI) {
        this.jbeanMap = nodeMap;
        this.rdfModel = ModelFactory.createDefaultModel();
        this.baseUri = baseURI;
        this.filename = filename;
        
    }
    
    public void export() {
        //set the base URI
        setBaseURI(baseUri);
        //get all nodes
        //variables used to create the nodes
        MetaDataInterface ssipNode = null;
        Map<String, Object> ssipNodeAttributes = null;
        Resource rdfNode = null;
        Property prop = null;

        //for each node you must create the resource
        //and add all properties
        for (String node : jbeanMap.keySet()) {
            ssipNode = jbeanMap.get(node);
            ssipNodeAttributes = ssipNode.getAttributes();
            //create the resource in the rdf model
            if (!ssipNode.getId().equals("ref node")) {
                
                rdfNode = rdfModel.createResource(createURI(getBaseURI(), ssipNode.getId()).toString());
                //add all the attributes to the rdfNode           
                for (String attName : ssipNodeAttributes.keySet()) {
                    prop = rdfModel.createProperty(createURI(getBaseURI(), attName).toString());
                    addProperty(rdfNode, prop, (String) ssipNodeAttributes.get(attName));
                }
                //add all the relations
                Map<MetaDataInterface, SSIPRelationType> relations = ssipNode.getRelations();
                for (MetaDataInterface met : relations.keySet()) {
                    //need a check to see if the node has been created
                    Resource typeResource = rdfModel.getResource(createURI(getBaseURI(), met.getId()).toString());
                    prop = rdfModel.createProperty(createURI(getBaseURI(), relations.get(met).getType()).toString());            
                    addProperty(rdfNode, prop, typeResource);
                }
            }
        }
        try {
            save(rdfModel, filename);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        LOGGER.log(Level.INFO, "Exported SSIP graph to RDF ...{0}", filename);

    }

    //ALL METHODS BELOW TAKEN/ MODIFIED FROM GOKSEL AND CURTIS's RDF-UTIL
    /**
     * Create a typed resource.
     *
     * @param resourceUri
     * @param type
     * @return
     */
    public Resource createResource(URI resourceUri, URI type) {
        Resource resource = this.rdfModel.createResource(resourceUri.toString());
        Resource typeResource = rdfModel.createResource(type.toString());
        addProperty(resource, RDF.type, typeResource);
        return resource;
    }
    
    public void addProperty(Resource resource, URI propertyURI,
            String propertyValue) {
        Property property = this.rdfModel.createProperty(propertyURI.toString());
        addProperty(resource, property, propertyValue);
    }
    
    public void addProperty(Resource resource, URI propertyURI,
            Resource propertyValue) {
        Property property = this.rdfModel.createProperty(propertyURI.toString());
        addProperty(resource, property, propertyValue);
    }
    
    public void addProperty(Resource resource, Property property,
            Resource propertyValue) {
        resource.addProperty(property, propertyValue);
    }
    
    public void addProperty(Resource resource, URI property,
            URI value) {
        Resource resourceValue = this.rdfModel.createResource(value.toString());
        addProperty(resource, property, resourceValue);
    }
    
    public void addProperty(Resource resource, Property property,
            String propertyValue) {
        resource.addProperty(property, propertyValue);
    }
    
    public void addComment(Resource resource, String value) {
        Literal literal = this.rdfModel.createLiteral(value);
        resource.addProperty(RDFS.comment, literal);
    }
    
    private void setBaseURI(URI uri) {
        this.baseUri = uri;
        if (uri != null && uri.toString().length() > 0) {
            rdfModel.setNsPrefix("", uri.toString());
        }
    }
    
    public URI getBaseURI() {
        try {
            return this.baseUri;
        } catch (Exception exception) {
            return null;
        }
    }
    
    public void addNameSpace(String nameSpacePrefix, URI nameSpace) {
        this.rdfModel.setNsPrefix(nameSpacePrefix, nameSpace.toString());
        
    }
    
    public static void save(Model rdfModel, String filePath) throws IOException, FileNotFoundException {
        save(rdfModel, filePath, getDefaultFormat());
    }
    
    public static void save(Model rdfModel, String filePath, String format) throws IOException, FileNotFoundException {
        if (format == null || format.length() == 0) {
            format = getDefaultFormat();
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(filePath));
            rdfModel.write(stream, format);
        } finally {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        }
    }
    
    public URI createURI(URI baseURI, String itemName) {
        itemName = itemName.replace(" ", "_");
        try {
            return new URI(baseURI.toString() + "#" + itemName);
        } catch (URISyntaxException exception) {
            return null;
        }
    }
    
    public static String getDefaultFormat() {
        return "RDF/XML-ABBREV";
    }

    //ALL METHODS ABOVE TAKEN/ MODIFIED FROM GOKSEL AND CURTIS's RDF-UTIL
    public void readModel(String file) {
        rdfModel.read(file);
    }
    
    public void writeModeltoConsole() {
        rdfModel.write(System.out);
    }
    
    public void printOut() {
        // list the statements in the Model
        StmtIterator iter = rdfModel.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();  // get next statement
            Resource subject = stmt.getSubject();     // get the subject
            Property predicate = stmt.getPredicate();   // get the predicate
            RDFNode object = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }
            
            System.out.println(" .");
        }
        
    }
}
