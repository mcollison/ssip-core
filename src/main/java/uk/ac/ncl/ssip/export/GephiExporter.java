/*
 * Copyright(c) 2014 Matthew Collison. 
 */
package uk.ac.ncl.ssip.export;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.ColorImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.PositionImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.viz.Color;
import it.uniroma1.dis.wsngroup.gexf4j.core.viz.Position;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import uk.ac.ncl.ssip.metadata.MetaDataInterface;
import uk.ac.ncl.ssip.metadata.SSIPNode;

/**
 *
 * @author Matthew Collison and Joe Mullen
 * http://www.ncl.ac.uk/computing/people/student/m.g.collison
 *
 */
public class GephiExporter {

    //set visualisation important: adds xmlns:viz = "http://www.gephi.org/gexf/viz" to op
    private Gexf gexf = new GexfImpl().setVisualization(true);
    private Graph graph = gexf.getGraph();
    //map for uid to java bean nodes 
    private Map<String, MetaDataInterface> jbeanMap = new HashMap<String, MetaDataInterface>();
    //map for uid to geph nodes 
    private Map<String, Node> gephiNodeMap = new HashMap<String, Node>();
    //create GEXF attribute list for nodes the graph
    private AttributeList nodeAttrList;
    //create GEXF attribute list for the edges of graph
    private AttributeList edgeAttrList;
    //testing coloured nodes this will need to be put in a seperate class
    private Map<String, Color> typeColorMap = new HashMap<String, Color>();   
    private Map<String, Color> dreninColorNodes = new HashMap<String, Color>() {
        {
            //yellow
            put("Bio_Tech", new ColorImpl(255, 255, 0));
            //green
            put("Small_Molecule", new ColorImpl(0, 255, 0));
            //blue
            put("Protein", new ColorImpl(0, 0, 255));
            //red
            put("Gene", new ColorImpl(255, 0, 0));
            //purple
            put("GO", new ColorImpl(153, 0, 153));
            //grey
            put("Disease", new ColorImpl(255, 255, 40));

            put("Pathway", new ColorImpl(80, 160, 80));
            put("ProteinFamily", new ColorImpl(10, 16, 80));
            put("CellLine", new ColorImpl(8, 16, 8));
            put("ProteinComplex", new ColorImpl(120, 120, 80));
            put("Tissue", new ColorImpl(80, 120, 120));
            put("Organism", new ColorImpl(80, 160, 255));
            put("Rare_Disease", new ColorImpl(20, 255, 20));
        }

    };

    public GephiExporter() {
    }

    public void export(Map<String, MetaDataInterface> nodeMap, String filename) {

        this.jbeanMap = nodeMap;
        this.nodeAttrList = new AttributeListImpl(AttributeClass.NODE);
        this.edgeAttrList = new AttributeListImpl(AttributeClass.EDGE);
        itterateObjects();
        writeGexf(filename);

    }

    public void exportMetadata(String filename) {
    }

    private void itterateObjects() {

        //just a quick patch-need to sort properly
        Set<String> relationsAdded = new HashSet<String>();

        //add the attribute lists to the graph
        graph.getAttributeLists().add(nodeAttrList);

        int x =0;
        int y=0;
        int z=1;
        //iterate through java bean list to create gephi nodes 
        for (String key : jbeanMap.keySet()) {
            String type = jbeanMap.get(key).getType();
//            System.out.println(type);

            Node n = graph.createNode(jbeanMap.get(key).getType() + jbeanMap.get(key).getId());

            n.setLabel(jbeanMap.get(key).getId());
            n.setPosition(new PositionImpl(x,y,z));
            x++;
            if (x>100){
                y++;
                x=0;
            }

            //get attributes from the SSIPNodes
            Map<String, Object> SSIPattributes = jbeanMap.get(key).getAttributes();
            n = addGexAttributes(SSIPattributes, n);
            gephiNodeMap.put(jbeanMap.get(key).getType() + jbeanMap.get(key).getId(), n);
        }
        //iterate through java bean list again to create gephi node relations 
        for (String fromNodeString : jbeanMap.keySet()) {
            MetaDataInterface fromNode = jbeanMap.get(fromNodeString);
            //get each java bean object relations as set of destination java bean objects
            Set<MetaDataInterface> rels = fromNode.getRelations().keySet();
            //if the relations set is not empty iterate through it
            if (!rels.isEmpty()) {
                for (MetaDataInterface toNode : rels) {
                    //needed to check that the gephiNodeMap contains the keys we wish to extracts
                    if (gephiNodeMap.containsKey(fromNode.getType() + fromNode.getId()) && gephiNodeMap.containsKey(toNode.getType() + toNode.getId())) {
                        //only if the edge hasn't already been created
                        if (!relationsAdded.contains(gephiNodeMap.get(fromNode.getType() + fromNode.getId()).toString() + gephiNodeMap.get(toNode.getType() + toNode.getId()).toString())) {
                            //create the edge

                            Node from = gephiNodeMap.get(fromNode.getType() + fromNode.getId());
                            Node to = gephiNodeMap.get(toNode.getType() + toNode.getId());
                            Edge edge = from.connectTo(to);
                            //how do we add propoerties to edges????
                            Map<String, Object> edgeAttrs = toNode.getAttributes();
                            addGexAttributes(edgeAttrs, edge);

                            //add the relation to the duplication check (String)
                            relationsAdded.add(gephiNodeMap.get(fromNode.getType() + fromNode.getId()).toString() + gephiNodeMap.get(toNode.getType() + toNode.getId()).toString());
                        }
                    }
                }
            }
        }

    }

    /**
     * Not working
     *
     * @param SSIPattributes
     * @param n
     * @return
     */
    private Edge addGexAttributes(Map<String, Object> SSIPattributes, Edge n) {

        //iterate through the SSIP attributes and add them to the gex4f  node
        for (String attKeys : SSIPattributes.keySet()) {
            System.out.println("Edge: " + attKeys);
            Attribute at = null;
            //iterate through all attributes allready created
            Iterator itr = edgeAttrList.iterator();
            while (itr.hasNext()) {
                //if we have already created it use it
                Attribute local = (Attribute) itr.next();
                if (local.getId().equals(attKeys)) {

                    at = local;
                }
            }
            //if not then we have to create the attribute
            if (at == null) {
                at = edgeAttrList.createAttribute(attKeys, AttributeType.STRING, attKeys);
            }
            //add the attribute to the node
            n.getAttributeValues().addValue(at, (String) SSIPattributes.get(attKeys));
        }
        return n;

    }

    private Node addGexAttributes(Map<String, Object> SSIPattributes, Node n) {

        //iterate through the SSIP attributes and add them to the gex4f  node
        for (String attKeys : SSIPattributes.keySet()) {
            Attribute at = null;
            //iterate through all attributes allready created
            Iterator itr = nodeAttrList.iterator();
            while (itr.hasNext()) {
                //if we have already created it use it
                Attribute local = (Attribute) itr.next();
                if (local.getId().equals(attKeys)) {
                    at = local;
                }
            }
            //if not then we have to create the attribute
            if (at == null) {
                at = nodeAttrList.createAttribute(attKeys, AttributeType.STRING, attKeys);
            }

            //if we have the type value we can change the colour of the node
            if (at.getId().equals("type")) {
//                if (dreninColorNodes.containsKey((String) SSIPattributes.get(attKeys))) {
//                    n.setColor(dreninColorNodes.get((String) SSIPattributes.get(attKeys)));
//                }
                if (typeColorMap.containsKey((String) SSIPattributes.get(attKeys))) {
                    n.setColor(typeColorMap.get((String) SSIPattributes.get(attKeys)));
                }else{
                    Random randomGenerator = new Random();
                    int red = randomGenerator.nextInt(255);
                    int green = randomGenerator.nextInt(255);
                    int blue = randomGenerator.nextInt(255);
                    n.setColor(new ColorImpl(red,green,blue));
                    typeColorMap.put((String) SSIPattributes.get(attKeys), new ColorImpl(red,green,blue));
                }
            }

            //add the attribute to the node
            n.getAttributeValues().addValue(at, (String) SSIPattributes.get(attKeys));
        }
        return n;

    }

    private void writeGexf(String filename) {
        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File(filename);
        Writer out;
        try {
            out = new FileWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            System.out.println(f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
