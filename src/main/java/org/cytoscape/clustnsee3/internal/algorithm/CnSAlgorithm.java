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

import org.cytoscape.model.CyNetwork;

/**
 * The clustering algorithms implemented with the Strategy pattern
 */
public abstract class CnSAlgorithm {
	protected String NAME = "";
	protected CnSAlgorithmParameters parameters;
	
	public CnSAlgorithmParameters getParameters() {
		return parameters;
	}
    public abstract CnSAlgorithmResult execute(CyNetwork network);
    public String getName() {
    	return NAME;
    }
}