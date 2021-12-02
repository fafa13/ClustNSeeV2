/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 déc. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.details;

import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;

/**
 * 
 */
public class CnSAFTreeNetworkNetnameDetailsNodePanel extends CnSPanelTreePanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2767529159005195713L;
	private Hashtable<Integer, Object> value;
	
	public CnSAFTreeNetworkNetnameDetailsNodePanel(Hashtable<Integer, Object> v) {
		super();
		value =v;
	}
	public void initGraphics() {
		super.initGraphics();
	}
}
