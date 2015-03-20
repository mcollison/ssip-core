/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.Filter;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 *
 * Provides an interface for query filters.
 *
 */
public interface Filter {

    filterType compare = null;
    String key = null;

    enum filterType {
        CONTAINS,
        EQUALS_EITHER_VALUE,
        EQUALS,
        GREATER_THAN,
        LESS_THAN;
    }

    /**
     * Comparison.
     *
     * @param key Property key being compared
     * @param value Property value being compared
     * @return
     */
    public boolean Compare(String key, String value);

    public String getPropKey();
}
