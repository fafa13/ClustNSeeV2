/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 déc. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation;

import java.util.Vector;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNodeNetworkSet {
	private Vector<CnSNodeNetwork> nodeNetworks;
	
	public CnSNodeNetworkSet() {
		nodeNetworks = new Vector<CnSNodeNetwork>();
	}
	public Vector<CnSNodeNetwork> getNodeNetworks() {
		return nodeNetworks;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSNodeNetwork getNodeNetwork(CyNetwork network, CyNode cn) {
		for (CnSNodeNetwork nn : nodeNetworks) {
			if (nn.getNetwork() == network && nn.getNode() == cn) {
				return nn;
			}
		}
		return null;
	}
}
