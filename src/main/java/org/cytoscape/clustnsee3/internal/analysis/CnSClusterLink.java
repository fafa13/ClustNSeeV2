/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.model.CyEdge;

public class CnSClusterLink {
	private CnSCluster source, target;
	private Vector<CnSEdge> edges;
	private Vector<CnSNode> nodes;
	private CyEdge cyEdge;
	
	public CnSClusterLink(CnSCluster source, CnSCluster target) {
		super();
		this.source = source;
		this.target = target;
		edges = new Vector<CnSEdge>();
		nodes = new Vector<CnSNode>();
	}
	
	public CnSCluster getSource() {
		return source;
	}
	
	public CnSCluster getTarget() {
		return target;
	}
	
	public void addEdge(CnSEdge e) {
		if (!edges.contains(e)) edges.addElement(e);
	}
	
	public void addNode(CnSNode n) {
		if (!nodes.contains(n)) nodes.addElement(n);
	}
	
	public Vector<CnSNode> getNodes() {
		return nodes;
	}
	
	public Vector<CnSEdge> getEdges() {
		return edges;
	}
	public boolean equals(Object o) {
		CnSClusterLink e = (CnSClusterLink)o;
		return (e.getSource() == source && e.getTarget() == target) || (e.getSource() == target && e.getTarget() == source);
	}
	public void setCyEdge(CyEdge ce) {
		cyEdge = ce;
	}
	public CyEdge getCyEdge() {
		return cyEdge;
	}
}
