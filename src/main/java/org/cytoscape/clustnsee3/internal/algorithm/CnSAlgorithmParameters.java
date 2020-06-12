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

package org.cytoscape.clustnsee3.internal.algorithm;

import java.util.Hashtable;
import java.util.Iterator;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSAlgorithmParameters implements Iterable<Integer> {
	private Hashtable<Integer, CnSAlgorithmParameter> parameters;
	private CnSPanel algorithmPanel;
	
	public CnSAlgorithmParameters() {
		super();
		parameters = new Hashtable<Integer, CnSAlgorithmParameter>();
		algorithmPanel = null;
	}
	
	public void addParameter(String name, int key, Object value) {
		parameters.put(key, new CnSAlgorithmParameter(name, value));
	}
	
	public CnSAlgorithmParameter getParameter(int key) {
		return parameters.get(key);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Integer> iterator() {
		return parameters.keySet().iterator();
	}
	public CnSPanel getAlgorithmPanel() {
		return algorithmPanel;
	}
	public void setPanel(CnSPanel panel) {
		algorithmPanel = panel;
	}
}