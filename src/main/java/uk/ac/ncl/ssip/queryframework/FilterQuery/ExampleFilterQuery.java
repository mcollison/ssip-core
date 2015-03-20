/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.FilterQuery;

import uk.ac.ncl.ssip.queryframework.QueryInterface;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.ac.ncl.ssip.queryframework.StepDescription;
import java.util.Arrays;
import java.util.HashMap;
import uk.ac.ncl.ssip.queryframework.StepDescription.Dir;
import uk.ac.ncl.ssip.queryframework.Filter.DoubleFilter;
import uk.ac.ncl.ssip.queryframework.Filter.Filter.filterType;
import uk.ac.ncl.ssip.queryframework.Filter.StringFilter;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 */
public class ExampleFilterQuery extends QueryInterface {

    public ExampleFilterQuery() {
        this.setSeedID("Sildenafil");
        this.setSeedType("Small_Molecule");
            
        //define the filter steps with attributes
        Set<String> types = new HashSet<>(Arrays.asList("BINDS_TO"));
        Map<String, Object> relAtts = new HashMap<>();
        relAtts.put("Att2", new DoubleFilter("Ki", filterType.GREATER_THAN, 100.0));
        Map<String, Object> nodeAtts = new HashMap<>();
        nodeAtts.put("Att1", new StringFilter("Name", filterType.CONTAINS, "A2a"));
        StepDescription one = new StepDescription(types, Dir.OUTGOING, false, nodeAtts, relAtts);
        
        Set<String> typestwo = new HashSet<>(Arrays.asList("IS_ENCODED_BY"));
        Map<String, Object> relAttstwo = new HashMap<>();
        Map<String, Object> nodeAttstwo = new HashMap<>();
        StepDescription two = new StepDescription(typestwo, Dir.OUTGOING, false, nodeAttstwo, relAttstwo);
           
        this.setSteps(new StepDescription[]{one, two});
    }
} 
