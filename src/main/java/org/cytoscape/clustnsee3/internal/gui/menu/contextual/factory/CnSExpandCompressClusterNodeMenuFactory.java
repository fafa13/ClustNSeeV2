/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 23 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.contextual.factory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.action.CnSCompressClusterNodeAction;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.action.CnSExpandClusterNodeAction;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * 
 */
public class CnSExpandCompressClusterNodeMenuFactory implements CyNodeViewContextMenuFactory {
	private String expandCompressText = "";
	
	private CnSExpandClusterNodeAction expandAction = new CnSExpandClusterNodeAction();
	private CnSCompressClusterNodeAction compressAction = new CnSCompressClusterNodeAction();
	
	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CyNodeViewContextMenuFactory#createMenuItem(org.cytoscape.view.model.CyNetworkView, org.cytoscape.view.model.View)
	 */
	@Override
	public CyMenuItem createMenuItem(CyNetworkView netView, final View<CyNode> nodeView) {
		CyMenuItem cyMenuItem = null;
		
		expandCompressText = getExpandCompressText(nodeView, netView);
		if (expandCompressText != null) {
			JMenuItem menuItem = new JMenuItem(expandCompressText);
			if (expandCompressText.contentEquals(CnSExpandClusterNodeAction.ACTION))
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						expandAction.doAction(nodeView.getModel().getSUID());
					}
					
				});
			
			else if (expandCompressText.contentEquals(CnSCompressClusterNodeAction.ACTION))
				menuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						compressAction.doAction(nodeView.getModel().getSUID());
					}
					
				});
			
			cyMenuItem = new CyMenuItem(menuItem, 0);
		}
		return cyMenuItem;
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	private String getExpandCompressText(View<CyNode> nodeView, CyNetworkView netView) {
		String ret = null;
		
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_NODE, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.CY_NODE, nodeView.getModel());
		CnSNode cnsNode = (CnSNode)CnSEventManager.handleMessage(ev);
		
		ev = new CnSEvent(CnSNetworkManager.GET_NETWORK, CnSEventManager.NETWORK_MANAGER);
		ev.addParameter(CnSNetworkManager.NETWORK, netView.getModel());
		CnSNetwork cnsNetwork = (CnSNetwork)CnSEventManager.handleMessage(ev);
		
		if (cnsNode != null && cnsNetwork != null)
			if (cnsNode.getNbClusters() > 0)
				ret = CnSCompressClusterNodeAction.ACTION;
			else
				ret = CnSExpandClusterNodeAction.ACTION;
		return ret;
	}
}