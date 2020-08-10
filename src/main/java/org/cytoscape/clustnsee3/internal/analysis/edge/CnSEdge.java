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

import java.util.HashMap;

import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSEdge {
	private CyEdge cyEdge;
	private HashMap<String, Object> attributes;
	private HashMap<String, Class> attributeTypes;
	
	public CnSEdge() {
		super();
		attributes = new HashMap<String, Object>();
		attributeTypes = new HashMap<String, Class>();
	}
	
	public void setCyEdge(CyEdge ce) {
		cyEdge = ce;
	}
	public long getSUID() {
		return cyEdge.getSUID();
	}
	
	public CyEdge getCyEdge() {
		return cyEdge;
	}

	public boolean equals(Object o) {
		CnSEdge e = (CnSEdge)o;
		long sourceSUID = e.getCyEdge().getSource().getSUID().longValue();
		long targetSUID = e.getCyEdge().getTarget().getSUID().longValue();
		boolean ret = sourceSUID == cyEdge.getSource().getSUID() && targetSUID == cyEdge.getTarget().getSUID();
		ret = ret || (sourceSUID == cyEdge.getTarget().getSUID() && targetSUID == cyEdge.getSource().getSUID());
		return ret;
	}
	public void setAttribute(String name, Object value, Class type) {
		attributes.put(name, value);
		attributeTypes.put(name, type);
		if (cyEdge != null) {
	
		}
	}
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}
}
