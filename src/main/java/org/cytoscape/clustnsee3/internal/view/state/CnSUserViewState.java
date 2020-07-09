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
public class CnSUserViewState extends CnSViewState {
	private Vector<CnSCluster> clusters;
	
	public CnSUserViewState() {
		super(null);
		clusters = new Vector<CnSCluster>();
	}
	public boolean isUserView() {
		return true;
	}
	public void addCluster(CnSCluster c) {
		if (!clusters.contains(c)) clusters.addElement(c);
	}
	public Vector<CnSCluster> getClusters() {
		return clusters;
	}
	public void removeCluster(CnSCluster c) {
		clusters.removeElement(c);
	}
}
