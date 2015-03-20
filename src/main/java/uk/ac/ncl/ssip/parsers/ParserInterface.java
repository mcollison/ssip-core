/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ncl.ssip.parsers;

import uk.ac.ncl.ssip.dataaccesslayer.BackendInterface;

/**
 *
 * @author Matt2
 */
public interface ParserInterface {
    
    public void parseFile();
    public void setHandler(BackendInterface handler);
    public void setFilepath(String handler);
}
