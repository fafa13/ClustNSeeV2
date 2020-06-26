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

import org.cytoscape.model.subnetwork.CySubNetwork;

/**
 * 
 */
public class CnSNetwork {
	private CySubNetwork network;
	
	public CnSNetwork(CySubNetwork network) {
		this.network = network;
	}
	public CySubNetwork getNetwork() {
		return network;
	}
	public String getName() {
		return network.getRow(network).get(CySubNetwork.NAME, String.class);
	}
}
