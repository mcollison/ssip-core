/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.FilterQuery;

import uk.ac.ncl.ssip.queryframework.QueryInterface;
import java.util.HashMap;
import java.util.Map;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.queryframework.StepDescription;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 *
 * Class takes a filter query and a graph runs step description before filtering
 * (ComputeFilter) results; this is done iteratively until search is complete.
 *
 */
public class RunFilterQuery {

    private QueryInterface query;
    private Neo4J handler;
    private Map<String, MetaDataInterface> returnNodes;
    private SSIPNode seedNode;
    private ComputeFilter filter;

    public RunFilterQuery(QueryInterface query, Neo4J handler) {
        this.query = query;
        this.handler = handler;
        this.returnNodes = new HashMap<>();
        this.seedNode = handler.getSSIPNode(new SSIPNode(query.getSeedType(), query.getSeedID()));
        returnNodes.put(seedNode.getId(), seedNode);
        this.filter = new ComputeFilter();
    }

    public void runQuery() {

        Map<String, MetaDataInterface> nextNodes = new HashMap<>();

        for (int i = 0; i < query.getSteps().length; i++) {
            StepDescription[] steps = query.getSteps()[i].getStep();
            if (i == 0) {
                //step one is the traversal
                Map<String, MetaDataInterface> tempNextNodes = handler.traversal(seedNode, steps[0]);
                //step two is the filtering
                filter.filter(query.getSteps()[i], seedNode, tempNextNodes);
                returnNodes.putAll(filter.getSuccesfulNextNodes());
                nextNodes = filter.getSuccesfulNextNodes();
            } else {
                Map<String, MetaDataInterface> tempNextNodesState = new HashMap<>();
                //for each of the previous nodes do the same thing
                for (String id : nextNodes.keySet()) {
                    //step one is the traversal
                    Map<String, MetaDataInterface> tempNextNodes = handler.traversal(nextNodes.get(id), steps[0]);
                    //step two is the filtering
                    filter.filter(query.getSteps()[i], (SSIPNode) nextNodes.get(id), tempNextNodes);
                    returnNodes.putAll(filter.getSuccesfulNextNodes());
                    tempNextNodesState.putAll(filter.getSuccesfulNextNodes());
                }
                nextNodes = tempNextNodesState;
            }
        }
    }

    public QueryInterface getQuery() {
        return query;
    }

    public Neo4J getHandler() {
        return handler;
    }

    public Map<String, MetaDataInterface> getReturnNodes() {
        return returnNodes;
    }

    public SSIPNode getSeedNode() {
        return seedNode;
    }

    public ComputeFilter getFilter() {
        return filter;
    }
}
