/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework;

import java.io.File;
import java.util.Map;
import uk.ac.ncl.ssip.dataaccesslayer.Neo4J;
import uk.ac.ncl.ssip.export.GephiExporter;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;

/**
 *
 * @author Matt2
 */
public class CustomQuery extends QueryInterface {


    public CustomQuery(String seedID, String seedType, StepDescription[] steps) {
        this.seedID=seedID;
        this.seedType=seedType;
        this.steps=steps;
    }

}
