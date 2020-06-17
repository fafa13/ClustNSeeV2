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

package org.cytoscape.clustnsee3.internal.view;

import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
public class CnSView {
	private CnSViewState state;
	private CyNetworkView view;
	private CnSNetwork network;
	
	public CnSView(CnSNetwork network, CyNetworkView view, CnSViewState state) {
		super();
		this.view = view;
		this.network = network;
		setViewState(state);
	}
	public void setViewState(CnSViewState state) {
		this.state = state;
	}
	public CyNetworkView getView() {
		return view;
	}
	public CnSNetwork getNetwork() {
		return network;
	}
	public Object getReference() {
		return state.getReference();
	}
	public CnSViewState getState() {
		return state;
	}
	public boolean equals(Object o) {
		if (o == null) return false;
		CnSView v = (CnSView)o;
		return state.equals(v.getState()) && view == v.getView() && network == v.getNetwork()/* && v.getName() == name*/;
	}
}
