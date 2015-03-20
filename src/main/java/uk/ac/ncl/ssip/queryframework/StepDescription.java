/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework;

import uk.ac.ncl.ssip.queryframework.StepDescription;
import java.util.Map;
import java.util.Set;
import uk.ac.ncl.ssip.metadata.SSIPNode;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 */
public class StepDescription {

    private SSIPNode from;
    private Set<String> relType;
    private SSIPNode to;
    private boolean extend;
    private Map<String, Object> toNodeProperties;
    private Map<String, Object> relationProperties;
    private Dir direction;
    private int depth;
    private boolean depthFirst;
    private StepDescription[] step;

    public enum Dir {

        OUTGOING, INCOMING
    }

    public Map<String, Object> getToNodeProperties() {
        return toNodeProperties;
    }

    public void setToNodeProperties(Map<String, Object> props) {
        this.toNodeProperties = props;
    }

    public Map<String, Object> getRelationProperties() {
        return relationProperties;
    }

    public void setRelationProperties(Map<String, Object> props) {
        this.relationProperties = props;
    }

    public StepDescription(int depth, boolean depthFirst) {
        this.depth = depth;
        this.depthFirst = depthFirst;
    }

    public StepDescription(SSIPNode from, SSIPNode to, Set<String> type, boolean extend) {
        this.extend = extend;
        this.from = from;
        this.relType = type;
        this.to = to;
        this.direction = Dir.OUTGOING;
        this.depth = 1;
    }

    public StepDescription(SSIPNode from, SSIPNode to, Set<String> type, Dir traverseDirection, boolean extend) {
        this.extend = extend;
        this.from = from;
        this.relType = type;
        this.to = to;
        this.direction = traverseDirection;
        this.depth = 1;
    }

    public StepDescription(Set<String> type, Dir traverseDirection, boolean extend, Map<String, Object> toNodeProps, Map<String, Object> relProps) {
        this.extend = extend;
        this.relType = type;
        this.toNodeProperties = toNodeProps;
        this.relationProperties = relProps;
        this.direction = traverseDirection;
        this.depth = 1;
    }

    public StepDescription(SSIPNode from, SSIPNode to, Set<String> type, Dir traverseDirection, Map<String, Object> toNodeProps, Map<String, Object> relProps) {
        this.from = from;
        this.relType = type;
        this.to = to;
        this.toNodeProperties = toNodeProps;
        this.relationProperties = relProps;
        this.direction = traverseDirection;
        this.depth = 1;
    }

    public Dir getDirection() {
        return direction;
    }

    public boolean getExtend() {
        return extend;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }

    public SSIPNode getFrom() {
        return from;
    }

    public Set<String> getType() {
        return relType;
    }

    public SSIPNode getTo() {
        return to;
    }

    public Set<String> getReltypes() {
        return relType;
    }

    public void setFrom(SSIPNode from) {
        this.from = from;
    }

    public void setType(Set<String> type) {
        this.relType = type;
    }

    public void setTo(SSIPNode to) {
        this.to = to;
    }

    public void setDirection(Dir direction) {
        this.direction = direction;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public StepDescription[] getStep() {
        step = new StepDescription[depth];
        step[0] = new StepDescription(depth, depthFirst);
        return step;
    }

    public void printOutQuery() {
        System.out.println("FROM: " + from.getId());
        System.out.println("TO: " + to.getId());
        System.out.println("Types: " + relType.toString());
    }
}
