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
public class SSIPRelation implements RelationshipType {

    private String type;
    private Map<String, Object> attributes;

    public SSIPRelation(String type, Map<String, Object> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public SSIPRelation(String type) {
        this.type = type;
        this.attributes = new HashMap<String, Object>();
    }

    public SSIPRelation(String type, int weight) {
        this.type = type;
        this.attributes = new HashMap<String, Object>();
        attributes.put("weight", weight);
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
        } else {
            addAttribute(key, value);
        }

    }
}
