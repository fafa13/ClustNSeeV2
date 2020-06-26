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

import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.menu.action.CnSCompressClusterNodeAction;
import org.cytoscape.clustnsee3.internal.gui.menu.action.CnSExpandClusterNodeAction;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.state.CnSViewState;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * 
 */
public class CnSView {
	private CnSViewState state;
	private CyNetworkView view;
	private boolean modifCluster;
	
	public CnSView(CyNetworkView view, CnSViewState state) {
		super();
		this.view = view;
		modifCluster = false;
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
	/**
	 * 
	 * @param
	 * @return
	 */
	public String getExpandCompressText(View<CyNode> nodeView, CyNetworkView netView) {
		String ret = null;
		
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_NODE, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.CY_NODE, nodeView.getModel());
		CnSNode cnsNode = (CnSNode)CnSEventManager.handleMessage(ev);
		
		if (cnsNode != null)
			if (cnsNode.getNbClusters() > 0)
				ret = CnSCompressClusterNodeAction.ACTION;
			else
				ret = CnSExpandClusterNodeAction.ACTION;
		return ret;
	}
	public void setModifCluster(boolean b) {
		modifCluster = b;
	}
	public boolean getModifCluster() {
		return modifCluster;
	}
}
