/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis.edge;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSNodeEdge extends CnSEdge {

	/**
	 * @param
	 * @return
	 */
	public CnSNodeEdge(CyEdge cyEdge, CnSCluster cluster) {
		super(cyEdge, cluster);
	}

}
