/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 15 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.view.state;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;

/**
 * 
 */
public class CnSClusterViewState extends CnSViewState {
	public CnSClusterViewState(Object cluster) {
		super(cluster);
	}
	public Vector<CnSCluster> getClusters() {
		Vector<CnSCluster> clusters = new Vector<CnSCluster>();
		clusters.addElement((CnSCluster)getReference());
		return clusters;
	}
}
