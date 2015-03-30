/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework;

import uk.ac.ncl.ssip.queryframework.StepDescription;
import uk.ac.ncl.ssip.queryframework.QueryAbstractClass;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 */
public class ConvertedSPARQL extends QueryAbstractClass {
    
    private String seedID;
    private String setSeedType;
    private StepDescription[] stepDescription ;
       
    public ConvertedSPARQL(){
    }
    
     public void setSetSeedType(String setSeedType) {
        this.setSeedType = setSeedType;
    }

    public void setStepDescription(StepDescription[] stepDescription) {
        this.stepDescription = stepDescription;
    }
    
    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }
}
