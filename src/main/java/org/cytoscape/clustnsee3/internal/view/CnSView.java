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

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Vector;

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
	private HashMap<CnSCluster, Point2D.Double> clusterLocation;
	
	public CnSView(CyNetworkView view, CnSViewState state) {
		super();
		this.view = view;
		modifCluster = false;
		isExpanded = new HashMap<CnSCluster, Boolean>();
		clusterLocation = new HashMap<CnSCluster, Point2D.Double>();
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
	protected CnSViewState getState() {
		return state;
	}
	public Vector<CnSCluster> getClusters() {
		return state.getClusters();
	}
	public void addCluster(CnSCluster c) {
		state.addCluster(c);
	}
	public void removeCluster(CnSCluster c) {
		state.removeCluster(c);
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
	public boolean isPartitionView() {
		return state.isPartitionView();
	}
	protected void updateNodeContextMenu() {
		state.updateNodeContextMenu();
	}
	
	public void setModifCluster(boolean b) {
		modifCluster = b;
	}
	protected boolean getModifCluster() {
		return modifCluster;
	}
	protected boolean isExpanded(CnSCluster c) {
		if (isExpanded.containsKey(c))
			return isExpanded.get(c);
		return false;
	}
	protected void setExpanded (CnSCluster c, boolean b) {
		if (isExpanded.containsKey(c)) isExpanded.remove(c);
		isExpanded.put(c, b);
	}
	protected void setLocation(CnSCluster cluster, Double x, Double y) {
		clusterLocation.put(cluster, new Point2D.Double(x, y));
	}
	protected Point2D.Double getClusterLocation(CnSCluster cluster) {
		return clusterLocation.get(cluster);
	}
	public String getStateValue() {
		return state.getState();
	}
}
