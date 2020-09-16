/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.network;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;

/**
 * 
 * 
 */
public class CnSNetwork {
	private CySubNetwork network;
	private HashMap<String, Class<?>> cnsNodeColumns, cnsEdgeColumns;
	
	public CnSNetwork(CySubNetwork network) {
		this.network = network;
		cnsNodeColumns = new HashMap<String, Class<?>>();
		cnsNodeColumns.put("CnS:isCluster", Boolean.class);
		if (network.getDefaultNodeTable().getColumn("CnS:isCluster") == null)
			network.getDefaultNodeTable().createColumn("CnS:isCluster", Boolean.class, true, false);
		cnsNodeColumns.put("CnS:size", Integer.class);
		if (network.getDefaultNodeTable().getColumn("CnS:size") == null)
			network.getDefaultNodeTable().createColumn("CnS:size", Integer.class, true, 0);
		cnsNodeColumns.put(CyNetwork.NAME, String.class);
		//cnsNodeColumns.put("canonicalName", String.class);
		
		cnsEdgeColumns = new HashMap<String, Class<?>>();
		cnsEdgeColumns.put("CnS:isInteraction", Boolean.class);
		if (network.getDefaultEdgeTable().getColumn("CnS:isInteraction") == null)
			network.getDefaultEdgeTable().createColumn("CnS:isInteraction", Boolean.class, true, false);
		cnsEdgeColumns.put("CnS:size", Integer.class);
		if (network.getDefaultEdgeTable().getColumn("CnS:size") == null)
			network.getDefaultEdgeTable().createColumn("CnS:size", Integer.class, true, 1);
		
		cnsEdgeColumns.put("interaction", String.class);
		cnsEdgeColumns.put("name", String.class);
		//cnsEdgeColumns.put("canonicalName", String.class);
	}
	public CySubNetwork getNetwork() {
		return network;
	}
	public String getName() {
		return network.getRow(network).get(CySubNetwork.NAME, String.class);
	}
	public HashMap<String, Class<?>> getNodeColumns() {
		return cnsNodeColumns;
	}
	public HashMap<String, Class<?>> getEdgeColumns() {
		return cnsEdgeColumns;
	}
}
