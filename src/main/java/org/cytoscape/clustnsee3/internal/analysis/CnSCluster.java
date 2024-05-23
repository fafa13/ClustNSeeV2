/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date Nov 12, 2018
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.cytoscape.clustnsee3.internal.analysis.annotation.CnSClusterAnnotation;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;

/**
 * @param <T>
 * 
 */
public class CnSCluster implements Comparable<CnSCluster> {
	public static final int COMPARE_NO = 0;
	public static final int COMPARE_NB_NODES_CRE = 1;
	public static final int COMPARE_NB_NODES_DEC = 2;
	public static final int COMPARE_RATIO_CRE = 3;
	public static final int COMPARE_RATIO_DEC = 4;
	public static final String[] COMPARE_NAME = new String[5];
		
	private static int compare_type;
	
	private int ID, totalDegree, inDegree, outDegree;
	private double modularity;
	private ImageIcon snapshot = null;
	private String name;
	private CySubNetwork network;
	
	private Vector<CnSNode> alNodes;
	private Vector<CnSEdge> alEdges;
	private Vector<CnSEdge> extEdges;
	
	private CyNode cyNode;
	
	private ArrayList<CnSClusterAnnotation> annotations;
	
	private HashMap<String, Object> attributes;
	private HashMap<String, Class<?>> attributeTypes;
	
	static {
		COMPARE_NAME[COMPARE_NO] = "----------";
		COMPARE_NAME[COMPARE_NB_NODES_CRE] = "size (ascending)";
		COMPARE_NAME[COMPARE_NB_NODES_DEC] = "size (descending)";
		COMPARE_NAME[COMPARE_RATIO_CRE] = "int/ext edges ratio (ascending)";
		COMPARE_NAME[COMPARE_RATIO_DEC] = "int/ext edges ratio (descending)";
		
		compare_type = COMPARE_NB_NODES_DEC;
	}
	public void print() {
		System.out.println("---------------------------------------------");
		System.out.println("Cluster name : " + name);
		System.out.println("Total degree : " + totalDegree);
		System.out.println("In degree : " + inDegree);
		System.out.println("Out degree : " + outDegree);
		System.out.println("Modularity : " + modularity);
		System.out.println("Snapshot : " + snapshot);
		System.out.println("Nodes : " + alNodes.size());
		System.out.println("Edges : " + alEdges.size());
		System.out.println("Ext edges : " + extEdges.size());
		System.out.println("CyNode : " + cyNode);
		System.out.println("Annotations : " + annotations);
		System.out.println("Attributes : " + attributes);
		System.out.println("Attribute types : " + attributeTypes);
		System.out.println("---------------------------------------------");
	}
	public CnSCluster() {
		super();
		alNodes = null;
		alEdges = null;
		extEdges = null;
		annotations = new ArrayList<CnSClusterAnnotation>();
		attributes = new HashMap<String, Object>();
		attributeTypes = new HashMap<String, Class<?>>();
		name = "all";
		ID = -1;
	}
	
	public static void setCompareType(String choice) {
		int t = 0;
		for (String s : COMPARE_NAME)
			if (choice.equals(s)) {
				compare_type = t;
				break;
			}
			else
				t++;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CnSCluster o) {
		switch (compare_type) {
			case COMPARE_NB_NODES_CRE :
				return alNodes.size() - o.getNbNodes();

			case COMPARE_NB_NODES_DEC :
				return o.getNbNodes() - alNodes.size();

			case COMPARE_RATIO_CRE :
				if (o.getExtEdges().size() > 0 && extEdges.size() > 0)
					return inDegree / extEdges.size() - o.getInDegree() / o.getExtEdges().size();
				else if (o.getExtEdges().size() > 0)
					return -1000;
				else return 1000;
				
			case COMPARE_RATIO_DEC :
				if (o.getExtEdges().size() > 0 && extEdges.size() > 0)
					return o.getInDegree() / o.getExtEdges().size() - inDegree / extEdges.size();
				else if (o.getExtEdges().size() > 0)
					return 1000;
				else return -1000;
				
			case COMPARE_NO :
				return Integer.parseInt(name) - Integer.parseInt(o.getName());
		}
		return 0;
	}
	
	public CySubNetwork getNetwork() {
		return network;
	}
	public int getNbNodes() {
		return alNodes.size();
	}
	public void setTotalDegree(int degree) {
		totalDegree = degree;
	}
	public void setInDegree(int degree) {
		inDegree = degree;
	}
	public void setOutDegree(int degree) {
		outDegree = degree;
	}
	public int getTotalDegree() {
		return totalDegree;
	}
	public int getOutDegree() {
		return outDegree;
	}
	public void setModularity( double modularity) {
        this.modularity = modularity;
    }
	public boolean contains(CyNode node) {
		boolean ret = false;
		Iterator<CnSNode> it = alNodes.iterator();
		CyNode n;
		while (it.hasNext()) {
			n = it.next().getCyNode();
			ret = (node.getSUID() == n.getSUID());
			if (ret) break;
		}
		return ret;
	}
	public boolean contains(String nodeName) {
		boolean ret = false;
		Iterator<CnSNode> it = alNodes.iterator();
		CnSNode n;
		String name;
		
		while (it.hasNext()) {
			n = it.next();
			name = network.getRow(n.getCyNode()).get("shared name", String.class);
			if (name.equals(nodeName)) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public void setNetwork(CySubNetwork network) {
		this.network = network;
	}
	public void setNodes(Vector<CnSNode> alNodes) {
		this.alNodes = alNodes;
	}
	public void setEdges(Vector<CnSEdge> alEdges) {
		this.alEdges = alEdges;
	}
	public Vector<CnSNode> getNodes() {
		return alNodes;
	}
	public Vector<CnSEdge> getEdges() {
		return alEdges;
	}
	public Vector<CnSEdge> getExtEdges() {
		return extEdges;
	}
	public void setName(String n) {
		name = n;
	}
	public String getName() {
		return name;
	}
	public void setID(int id) {
		ID = id;
	}
	public int getID() {
		return ID;
	}
	
	/**
     * Compute the modularity, InDegree and OutDegree of the cluster
     * 
     * @param network The contextual network
     */
    public void calModularity(CyNetwork myNetwork) {

    	int inDegree = myNetwork.getEdgeList().size();
        int totalDegree = 0;
        Vector<CnSNode> nodes = getNodes();
        for( CnSNode node : nodes) {											// for each node in merged C1
            totalDegree += network.getRootNetwork().getNeighborList(network.getRootNetwork().getNode(node.getSUID()), CyEdge.Type.ANY).size();	// can this be useful?
        }
        int outDegree = totalDegree / 2 - inDegree;
        this.setInDegree( inDegree);
        this.setTotalDegree( totalDegree);
        this.setOutDegree(outDegree);
        double fModule = 0;
        if( outDegree != 0)
            fModule = (double) inDegree / (double) outDegree;
        else
            fModule = 0;
        setModularity( fModule);
    }
	/**
	 * 
	 * @param
	 * @return
	 */
	public int getInDegree() {
		return inDegree;
	}
	public double getModularity() {
		return modularity;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public void setSnapshot(ImageIcon imageIcon) {
		snapshot = imageIcon;
	}
	public ImageIcon getSnapshot() {
		return snapshot;
	}
	
	public int getNodeDegree(CnSNode node) {
		int ret = 0;
		CnSEdge edge;
		if (alNodes.contains(node)) {
			Iterator<CnSEdge> it = alEdges.iterator();
			while (it.hasNext()) {
				edge = it.next();
				if (edge.getCyEdge().getSource() == node.getCyNode() || edge.getCyEdge().getTarget() == node.getCyNode()) ret++;
			}
		}
		return ret;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public void setExtEdges(Vector<CnSEdge> extEdges) {
		this.extEdges = extEdges;
	}
	
	public void setCyNode(CyNode n) {
		cyNode = n;
	}
	
	public CyNode getCyNode() {
		return cyNode;
	}
	
	public void addAnnotation(CnSClusterAnnotation annotation) {
		if (!annotations.contains(annotation)) annotations.add(annotation);
	}
	public void addAnnotation(CnSClusterAnnotation annotation, int index) {
		if (!annotations.contains(annotation)) annotations.add(index, annotation);
	}
	public void removeAnnotation(CnSClusterAnnotation annotation) {
		int index = annotations.indexOf(annotation);
		if (index != -1) annotations.remove(index);
	}
	public void removeAnnotation(int index) {
		if (annotations.size() > index)
			annotations.remove(index);
	}
	public ArrayList<CnSClusterAnnotation> getAnnotations() {
		return annotations;
	}
	public void setAttribute(String name, Object value, Class<?> type) {
		attributes.put(name, value);
		attributeTypes.put(name, type);
	}
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}
	/*public String toString() {
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.CLUSTER, this);
		CnSPartition part = (CnSPartition)CnSEventManager.handleMessage(ev);
		
		return part.getName() + ":" + part.getAlgorithmName() + ":" + getName();
	}*/
	public String toString() {
		if (getName().equals("all")) return "all";
		return "Cluster " + getName();
	}
	public double getDensity() {
		return 2.0 * getEdges().size() / (getNbNodes() * (getNbNodes() - 1));
	}
}
