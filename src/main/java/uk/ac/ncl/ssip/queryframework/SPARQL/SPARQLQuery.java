/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.SPARQL;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ncl.ssip.queryframework.CustomQuery;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 *
 * Class takes a sparql query and converts it to a SSIP query.
 *
 */
public class SPARQLQuery {

    public Query sparqlQuery;
    public CustomQuery convertedSPARQL;
    private final static Logger LOGGER = Logger.getLogger(SPARQLQuery.class.getName());

    public SPARQLQuery(Query q) {
        this.sparqlQuery = q;
        this.convertedSPARQL = new CustomQuery(null, null, null);
    }

    public static void main(String[] args) {
        Query query = QueryFactory.create("PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
                + "SELECT ?name\n"
                + "WHERE {\n"
                + "    ?person foaf:name ?name .\n"
                + "}", Syntax.syntaxSPARQL);
        SPARQLQuery convert = new SPARQLQuery(query);
        convert.convertQuery();
        
    }

    public void convertQuery() {
        LOGGER.log(Level.INFO, "Converting following SPARQL query \n ------- \n {0} \n -------", sparqlQuery.toString(Syntax.syntaxSPARQL));
        
    }

    public Query getSparqlQuery() {
        return sparqlQuery;
    }

    public CustomQuery getConvertedSPARQL() {
        return convertedSPARQL;
    }

    public void readQuery(String queryString) {

        Query query = QueryFactory.create(queryString);

    }
}
