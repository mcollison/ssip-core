/*
 * Copyright(c) 2014 Matthew Collison. 
 */
package uk.ac.ncl.ssip.metadata;

import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Matthew Collison
 * http://www.ncl.ac.uk/computing/people/student/m.g.collison
 *
 */
public class SSIPRelationType implements RelationshipType {

    private String type;
    private Map<String, Object> attributes;

    public SSIPRelationType(String type, Map attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public SSIPRelationType(String type) {
        this.type = type;
        this.attributes = new HashMap<String, Object>();
    }

    public SSIPRelationType(String type, int weight) {
        this.type = type;
        this.attributes = new HashMap<String, Object>();
        attributes.put("weight", weight);
    }

    /**
     * TO DO need to think about how we populate the types. Maybe when creating
     * a node of type the constructor calls a method to query an 'ontology' and
     * populate an Enum with all possible relation types from said node type.
     */
    public enum RelTypes implements RelationshipType {

        ORIGINATED_FROM, IS_A, KNOWS, BINDS_TO, INTERACTS_WITH, IS_ENCODED_BY, HAS_GO_TERM, PART_OF_PATHWAY, PART_OF_TISSUE, INVOLVED_IN, INTERACTS_WITH_PROTEIN, LOCATED_IN_CELLULAR_COMPONENT, HAS_MOLECULAR_FUNCTION, PART_OF_BIOLOGICAL_PROCESS, IS_A_RARE_DISEASE, IS_A_DISEASE, PART_OF_RARE_DISEASE, MAY_TREAT, MAY_PREVENT, HAS_SIDE_EFFECT, TREATS
    }

    @Override
    public String name() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type.split(",")[0].replace("RelationshipTypeToken[name:", "").trim();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean checkAttribute(String key) {
        if (attributes.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Update an already existing key, value pair.
     */
    public void appendAttributeValue(String key, Object value) {
        //if the key already exist append
        if (attributes.containsKey(key)) {
            Object val = attributes.get(key);


        } //else add to the attributes map
        else {
        }


    }
    //TODO: append value to existing relation attribute
}
