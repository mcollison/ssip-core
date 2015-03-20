/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import uk.ac.ncl.ssip.metadata.SSIPRelationType;

/**
 *
 * @author Matt2
 */
public interface MetaDataInterface {

    String type = null;
    String id = null;
    Map<MetaDataInterface, SSIPRelationType> relations = new HashMap<MetaDataInterface, SSIPRelationType>();
    Map<String, Object> attributes = new HashMap<String, Object>();
    
    public String getType();

    public String getId();

    public void setId(String id);
    
    public Map<MetaDataInterface, SSIPRelationType> getRelations();
    
    public SSIPRelationType getRelation(MetaDataInterface nodeObject);
    
    public void addRelation(MetaDataInterface nodeObj, SSIPRelationType tr); 

    public Set<String> getAllPropertyNames();
    
     public Map<String, Object> getAttributes();
     
}
