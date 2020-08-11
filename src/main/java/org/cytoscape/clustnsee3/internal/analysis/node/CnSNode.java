/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 mai 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis.node;

import java.util.HashMap;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNode {
	private CyNode cyNode;
	private Vector<CnSCluster> clusters;
	private HashMap<String, Object> attributes;
	private HashMap<String, Class<?>> attributeTypes;
	
	public CnSNode(CyNode cyNode, CnSCluster cluster) {
		super();
		attributes = new HashMap<String, Object>();
		attributeTypes = new HashMap<String, Class<?>>();
		this.cyNode = cyNode;
		clusters = new Vector<CnSCluster>();
		if (cluster != null) clusters.addElement(cluster);
	}
	
	public long getSUID() {
		return cyNode.getSUID();
	}
	
	public CyNode getCyNode() {
		return cyNode;
	}
	public void addCluster(CnSCluster cluster) {
		if (!clusters.contains(cluster)) clusters.addElement(cluster);
	}
	public int getNbClusters() {
		return clusters.size();
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void removeCluster(CnSCluster cluster) {
		clusters.removeElement(cluster);
	}
	public boolean equals(Object o) {
		boolean ret = false;
		CnSNode node = (CnSNode)o;
		
		ret = (node.getSUID() == getSUID());
		return ret;
	}
	public Vector<CnSCluster> getClusters() {
		return clusters;
	}
	public void setAttribute(String name, Object value, Class<?> type) {
		attributes.put(name, value);
		attributeTypes.put(name, type);
		if (cyNode != null) {
	
		}
	}
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}
}
