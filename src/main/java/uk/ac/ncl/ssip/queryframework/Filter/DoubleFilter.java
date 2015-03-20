/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.Filter;

/**
 *
 * @author Joe Mullen http://homepages.cs.ncl.ac.uk/j.mullen/
 *
 * Allows for the comparison of doubles.
 */
public class DoubleFilter implements Filter {

    private filterType comparator;
    private String key;
    private double value;

    /**
     * Constructor- define the type of comparison and provide a key and value to
     * be compared too.
     *
     * @param comparator
     * @param key
     * @param value
     */
    public DoubleFilter(String key, filterType comparator, double value) {
        this.comparator = comparator;
        this.key = key;
        this.value = value;
    }

    /**
     * Actual comparison method.
     *
     * @param checkkey
     * @param checkvalue
     * @return the boolean value returns reflects whether or not a node should
     * be removed during a search i.e. a true means that it will be removed as
     * there was no match and vice versa.
     *
     */
    public boolean Compare(String checkkey, String checkvalue) {
        if (isDouble(checkvalue)) {
            double check = Double.parseDouble(checkvalue);
            if (comparator.equals(filterType.GREATER_THAN)) {
                if (key.toLowerCase().equals(checkkey.toLowerCase())) {
                    if (check > value) {
                        return false;
                    }
                } else {
                    return true;
                }
            } else if (comparator.equals(filterType.LESS_THAN)) {
                if (key.toLowerCase().equals(checkkey.toLowerCase())) {
                    if (check < value) {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * Checks to see if the value we are comparing is actually a double.
     *
     * @param value
     * @return
     */
    public boolean isDouble(String value) {
        try {
            java.lang.Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getPropKey() {
        return key;
    }
}
