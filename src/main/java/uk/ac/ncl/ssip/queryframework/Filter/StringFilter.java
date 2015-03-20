/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ncl.ssip.queryframework.Filter;

import java.util.Set;

/**
 *
 * @author joe
 */
public class StringFilter implements Filter {

    private filterType comparator;
    private String key;
    private String value;
    private Set<String> mulValue;


    /**
     * Constructor- define the type of comparison and provide a key and value to
     * be compared too.
     *
     * @param comparator
     * @param key
     * @param value
     */
    public StringFilter(String key, filterType comparator, String value) {
        this.comparator = comparator;
        this.key = key;
        this.value = value;
    }

    /**
     * Constructor- define the type of comparison and provide a key and value to
     * be compared too.
     *
     * @param comparator
     * @param key
     * @param value
     */
    public StringFilter(String key, filterType comparator, Set<String> value) {
        this.comparator = comparator;
        this.key = key;
        this.mulValue = value;
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

        if (comparator.equals(filterType.CONTAINS)) {
            if (checkvalue.toLowerCase().contains(value.toLowerCase())) {
                return false;
            } else {
                return true;
            }
        } else if (comparator.equals(filterType.EQUALS)) {
            if (checkvalue.toLowerCase().equals(value.toLowerCase())) {

                return false;
            } else {
                return true;
            }
        } else if (comparator.equals(filterType.EQUALS_EITHER_VALUE)) {

            for (String val : mulValue) {
                if (checkvalue.toLowerCase().equals(val.toLowerCase())) {
                    return false;
                }
            }
        }
        
        return true;
    }

    @Override
    public String getPropKey() {
        return key;
    }

}
