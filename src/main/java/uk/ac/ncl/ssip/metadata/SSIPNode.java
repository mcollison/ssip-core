/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Matt2
 */
public class SSIPNode implements MetaDataInterface {

    //<TODO> think: how do we assert equality?? can we have a separate String<Set> accessions
    //and use this as equalirt assertion as opposed to looking thorugh all the 
    //properties??
    private String type;
    private String id;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<MetaDataInterface, SSIPRelationType> relations = new HashMap<MetaDataInterface, SSIPRelationType>();

    public SSIPNode(String type, String id, Map<MetaDataInterface, SSIPRelationType> relations, Map<String, Object> attributes) {
        this.type = type;
        this.id = id;
        this.relations = relations;
        this.attributes = attributes;
    }

    public SSIPNode(String type, String id) {
        this.type = type;
        this.id = id;
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
    public Map<MetaDataInterface, SSIPRelationType> getRelations() {
        return relations;
    }

    @Override
    public void addRelation(MetaDataInterface nodeObj, SSIPRelationType tr) {
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
    public SSIPRelationType getRelation(MetaDataInterface nodeObject) {

        for (MetaDataInterface met : relations.keySet()) {
            if (nodeObject.getId().equals(met.getId()) && nodeObject.getType().equals(met.getType())) {
                return relations.get(met);
            }
        }

        return null;
    }
}
