/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 30 sept. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNodeToName {
	private CyNode node;
	private String name;

	public CnSNodeToName(CyNode node, String name) {
		this.node = node;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	public CyNode getNode() {
		return node;
	}
}
