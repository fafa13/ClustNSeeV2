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

import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSEdge {
	private CyEdge cyEdge;
	
	public CnSEdge(CyEdge cyEdge) {
		super();
		this.cyEdge = cyEdge;
	}
	
	public long getSUID() {
		return cyEdge.getSUID();
	}
	
	public CyEdge getCyEdge() {
		return cyEdge;
	}

	public boolean equals(Object o) {
		CnSEdge e = (CnSEdge)o;
		boolean ret = false;
		ret = e.getCyEdge() == cyEdge;
		//ret = e.getCyEdge().getSource().getSUID() == cyEdge.getSource().getSUID() && e.getCyEdge().getTarget().getSUID() == cyEdge.getTarget().getSUID();
		//ret = ret || (e.getCyEdge().getSource().getSUID() == cyEdge.getTarget().getSUID() && e.getCyEdge().getTarget().getSUID() == cyEdge.getSource().getSUID());
		return ret;
	}
}
