/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Matt2
 */
public class SSIPNode implements MetaDataInterface {

    private String type;
    private String id;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<MetaDataInterface, SSIPRelation> relations = new HashMap<MetaDataInterface, SSIPRelation>();

    public SSIPNode(String type, String id, Map<MetaDataInterface, SSIPRelation> relations, Map<String, Object> attributes) {
        this.type = type;
        this.id = id;
        this.relations = relations;
        this.attributes = attributes;
        if (!type.equals("metadata")) {
            this.addRelation(new SSIPNode("metadata", type), new SSIPRelation("instance"));
        }
    }

    public SSIPNode(String type, String id) {
        this.type = type;
        this.id = id;
        if (!type.equals("metadata")) {
            this.addRelation(new SSIPNode("metadata", type), new SSIPRelation("instance"));
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Map<MetaDataInterface, SSIPRelation> getRelations() {
        return relations;
    }

    @Override
    public void addRelation(MetaDataInterface nodeObj, SSIPRelation tr) {
        relations.put(nodeObj, tr);
    }

    public void addProperty(String str, Object obj) {
        this.attributes.put(str, obj);
    }

    public Map<String, Object> getPropertyMap() {
        return attributes;
    }

    public Object getProperty(String name) {
        return attributes.get(name);
    }

    public Set<String> getAllPropertyNames() {
        return attributes.keySet();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public boolean containsRelation(MetaDataInterface nodeObject) {
        for (MetaDataInterface met : relations.keySet()) {
            if (nodeObject.getId().equals(met.getId()) && nodeObject.getType().equals(met.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SSIPRelation getRelation(MetaDataInterface nodeObject) {

        for (MetaDataInterface met : relations.keySet()) {
            if (nodeObject.getId().equals(met.getId()) && nodeObject.getType().equals(met.getType())) {
                return relations.get(met);
            }
        }

        return null;
    }
}
