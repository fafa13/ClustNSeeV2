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

import java.util.HashMap;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;
import org.cytoscape.view.model.CyNetworkView;

/**
 * 
 */
public class CnSView {
	private CnSViewState state;
	private CyNetworkView view;
	private boolean modifCluster;
	private HashMap<CnSCluster, Boolean> isExpanded;
	
	public CnSView(CyNetworkView view, CnSViewState state) {
		super();
		this.view = view;
		modifCluster = false;
		isExpanded = new HashMap<CnSCluster, Boolean>();
		setViewState(state);
	}
	public void setViewState(CnSViewState state) {
		this.state = state;
	}
	public CyNetworkView getView() {
		return view;
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
		return state.equals(v.getState()) && view == v.getView() /*&& network == v.getNetwork() && v.getName() == name*/;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public boolean isUserView() {
		return state.isUserView();
	}
	public void updateNodeContextMenu() {
		state.updateNodeContextMenu();
	}
	
	public void setModifCluster(boolean b) {
		modifCluster = b;
	}
	public boolean getModifCluster() {
		return modifCluster;
	}
	public boolean isExpanded(CnSCluster c) {
		if (isExpanded.containsKey(c))
			return isExpanded.get(c);
		return false;
	}
	public void setExpanded (CnSCluster c, boolean b) {
		if (isExpanded.containsKey(c)) isExpanded.remove(c);
		isExpanded.put(c, b);
	}
}
