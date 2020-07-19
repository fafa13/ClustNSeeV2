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
 * 
 */
public class CnSCluster implements Comparable<CnSCluster> {
	private int totalDegree, inDegree, outDegree;
	private double modularity;
	private ImageIcon snapshot = null;
	private String name;
	private CySubNetwork network;
	
	private Vector<CnSNode> alNodes;
	private Vector<CnSEdge> alEdges;
	private Vector<CnSEdge> extEdges;
	
	private CyNode cyNode;
	
	private ArrayList<CnSClusterAnnotation> annotations;
	
	public CnSCluster() {
		super();
		alNodes = null;
		alEdges = null;
		extEdges = null;
		annotations = new ArrayList<CnSClusterAnnotation>();
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
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CnSCluster o) {
		return alNodes.size() - o.getNbNodes();
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
		annotations.add(annotation);
	}
	public void removeAnnotation(int index) {
		if (annotations.size() > index)
			annotations.remove(index);
	}
	public ArrayList<CnSClusterAnnotation> getAnnotations() {
		return annotations;
	}
}
