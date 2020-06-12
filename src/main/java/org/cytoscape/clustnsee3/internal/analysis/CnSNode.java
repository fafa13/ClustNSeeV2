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

package org.cytoscape.clustnsee3.internal.analysis;

import java.util.Vector;

import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNode {
	private CyNode cyNode;
	private Vector<CnSCluster> clusters;
	
	public CnSNode(CyNode cyNode, CnSCluster cluster) {
		super();
		this.cyNode = cyNode;
		clusters = new Vector<CnSCluster>();
		clusters.addElement(cluster);
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
}
