/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework;

import java.util.ArrayList;
import java.util.List;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Matt2
 */
public class StepDescription {

    private int depth;
    private boolean depthFirst;
    //to do add ability to specify node type as well as relation type
    private List<RelTypes> relations;
    /**
     * These variables may be used to extend the traversal methods. If we provide
     * a list of node types we want to include and properties for which they must
     * contain then we can score during the traversal 
     */
    private List<String> nodes;
    
    
    public StepDescription() {
        this.relations = new ArrayList<RelTypes>();   
    }
    
    /*
     * 
     */
    public StepDescription(int depth, boolean depthFirst) {
        this.relations = new ArrayList<RelTypes>();
        this.depth = depth;
        this.depthFirst = depthFirst;
    }

    public void addRelation(RelTypes rel){
        relations.add(rel);
    }
    
    public List<RelTypes> getRelations(){
        return relations;
    }
    
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isDepthFirst() {
        return depthFirst;
    }

    public void setDepthFirst(boolean depthFirst) {
        this.depthFirst = depthFirst;
    }

    public enum RelTypes implements RelationshipType {
        KNOWS, BINDS_TO, INTERACTS_WITH, PART_OF_PATHWAY, HAS_GO_TERM, IS_ENCODED_BY, INTERACTS_WITH_PROTEIN, LOCATED_IN_CELLULAR_COMPONENT, HAS_MOLECULAR_FUNCTION, PART_OF_BIOLOGICAL_PROCESS, INVOLVED_IN_DISEASE
    }

}
