/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework;

import uk.ac.ncl.ssip.queryframework.StepDescription;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 */

public class QueryInterface {
    
    protected String version = null;//default should be latest version but can be updated 
    protected String seedID;
    protected String seedType;
    protected StepDescription[] steps;

    public String getVersion() {
        return version;
    }

    public String getSeedID() {
        return seedID;
    }

    public String getSeedType() {
        return seedType;
    }

    public StepDescription[] getSteps() {
        return steps;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSeedID(String seedID) {
        this.seedID = seedID;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public void setSteps(StepDescription[] steps) {
        this.steps = steps;
    }
    
}
