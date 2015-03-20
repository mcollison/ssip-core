/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.performance;

import java.util.List;

/**
 *
 * @author a6034850
 */
public interface PerformanceTasks {

    /**
     * Returns an array of completion times for the build process for 
     * graphs of size 10, 100, 1,000, 10,000, 100,000, 1,000,000, 10,000,000, 
     * 100,000,000 and 1,000,000,000 nodes. 
     *
     * @return build times in seconds 
     */
    public List<Double> buildGraph();

    /**
     * Returns an array of completion times for a given query process for 
     * graphs of size 10, 100, 1,000, 10,000, 100,000, 1,000,000, 10,000,000, 
     * 100,000,000 and 1,000,000,000 nodes. 
     *
     * @return query times in seconds 
     */
    public List<Double> queryGraph();

    /**
     * Draws charts for the build performance and query performance times. 
     */
    public void performanceCharts();
    
}
