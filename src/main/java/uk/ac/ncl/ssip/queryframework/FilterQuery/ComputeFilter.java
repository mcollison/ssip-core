/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.FilterQuery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.ac.ncl.ssip.queryframework.StepDescription;
import uk.ac.ncl.ssip.queryframework.StepDescription.Dir;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;
import uk.ac.ncl.ssip.metadata.SSIPRelationType;
import uk.ac.ncl.ssip.queryframework.Filter.Filter;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 *
 * Class takes all nodes from a traversal and filters them based on the filters
 * that have been provided.
 *
 */
public class ComputeFilter {

    private SSIPNode previousNode;
    private Map<String, MetaDataInterface> potentialNextNodes;
    private StepDescription step;

    public ComputeFilter(StepDescription step, SSIPNode previous, Map<String, MetaDataInterface> potentialNextNodes) {

        this.previousNode = previous;
        this.step = step;
        this.potentialNextNodes = potentialNextNodes;
    }

    public ComputeFilter() {
    }

    public void filter(StepDescription step, SSIPNode previous, Map<String, MetaDataInterface> potentialNextNodes) {
        this.step = step;
        this.potentialNextNodes = potentialNextNodes;
        this.previousNode = previous;
        //first check the edge attributes
        checkEdgeAttributes();
        //then check node attributes
        checkNodeAttributes();
    }

    /**
     * Check that the node attributes are matched using the filters assigned.
     */
    public void checkNodeAttributes() {
        SSIPNode checkNode = null;
        if (step.getToNodeProperties() != null && step.getToNodeProperties().size() > 0) {
            for (String propName : step.getToNodeProperties().keySet()) {
                Filter check = (Filter) step.getToNodeProperties().get(propName);
                String key = check.getPropKey();
                Map<String, MetaDataInterface> duplicate = new HashMap<>();
                duplicate.putAll(potentialNextNodes);
                for (String id : duplicate.keySet()) {
                    checkNode = (SSIPNode) duplicate.get(id);
                    boolean remove = true;
                    if (checkNode.getAllPropertyNames().contains(key)) {
                        remove = check.Compare(key, (String) checkNode.getAttributes().get(key));
                    }
                    //not matched the property- remove the end node
                    if (remove) {
                        //may have allready been removed; check
                        if (potentialNextNodes.containsKey(id)) {
                            potentialNextNodes.remove(id);
                        }
                    }
                }
            }
        }

    }

    /**
     * Checks the edge attributes.
     */
    public void checkEdgeAttributes() {

        SSIPNode checkNode = null;
        SSIPRelationType checkRelation = null;
        Set<SSIPRelationType> relations = new HashSet<>();
        Map<String, MetaDataInterface> duplicate = new HashMap<>();
        duplicate.putAll(potentialNextNodes);
        if (step.getRelationProperties() != null && step.getRelationProperties().size() > 0) {
            for (String id : duplicate.keySet()) {
                boolean remove = true;
                checkNode = (SSIPNode) duplicate.get(id);
                if (step.getDirection().equals(Dir.INCOMING)) {
                    if (checkNode.containsRelation(previousNode)) {
                        checkRelation = checkNode.getRelation(previousNode);
                        for (String propName : step.getRelationProperties().keySet()) {
                            //if we have other comptypes need to check here
                            Filter check = (Filter) step.getRelationProperties().get(propName);
                            String key = check.getPropKey();
                            if (checkRelation.checkAttribute(key)) {
                                remove = check.Compare(key, (String) checkRelation.getAttribute(key));
                            }
                        }
                    }
                } else if (step.getDirection().equals(Dir.OUTGOING)) {
                    if (previousNode.containsRelation(checkNode)) {
                        checkRelation = previousNode.getRelation(checkNode);
                        for (String propName : step.getRelationProperties().keySet()) {
                            //if we have other comptypes need to check here
                            Filter check = (Filter) step.getRelationProperties().get(propName);
                            String key = check.getPropKey();
                            if (checkRelation.checkAttribute(key)) {
                                remove = check.Compare(key, (String) checkRelation.getAttribute(key));
                            }
                        }
                    }
                }
                if (remove) {
                    //may have allready been removed; check        
                    if (potentialNextNodes.containsKey(id)) {
                        potentialNextNodes.remove(id);
                    }
                }
            }
        }
    }

    public Map<String, MetaDataInterface> getSuccesfulNextNodes() {
        return potentialNextNodes;
    }
}
