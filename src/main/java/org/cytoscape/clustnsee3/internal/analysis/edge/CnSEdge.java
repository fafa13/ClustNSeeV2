/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 29 mai 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis.edge;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSEdge {
	private CyEdge cyEdge;
	//private Vector<CnSCluster> clusters;
	
	public CnSEdge(CyEdge cyEdge, CnSCluster cluster) {
		super();
		this.cyEdge = cyEdge;
		//clusters = new Vector<CnSCluster>();
		//if (cluster != null) clusters.addElement(cluster);
	}
	
	public long getSUID() {
		return cyEdge.getSUID();
	}
	
	public CyEdge getCyEdge() {
		return cyEdge;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void addCluster(CnSCluster cluster) {
		//if (!clusters.contains(cluster)) clusters.addElement(cluster);
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public void removeCluster(CnSCluster cluster) {
		//clusters.removeElement(cluster);
	}
	
	public boolean equals(Object o) {
		CnSEdge e = (CnSEdge)o;
		boolean ret = false;
		ret = e.getCyEdge().getSource().getSUID() == cyEdge.getSource().getSUID() && e.getCyEdge().getTarget().getSUID() == cyEdge.getTarget().getSUID();
		ret = ret || (e.getCyEdge().getSource().getSUID() == cyEdge.getTarget().getSUID() && e.getCyEdge().getTarget().getSUID() == cyEdge.getSource().getSUID());
		//if (ret) JOptionPane.showMessageDialog(null, "(" + e.getCyEdge().getSource().getSUID() + "->" + e.getCyEdge().getTarget().getSUID() + ")\n(" + cyEdge.getSource().getSUID() + "->" + cyEdge.getTarget().getSUID() + ")");
		return ret;
	}
}
