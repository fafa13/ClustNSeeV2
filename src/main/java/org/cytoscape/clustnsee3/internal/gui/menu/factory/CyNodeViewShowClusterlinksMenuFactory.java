/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 29 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.factory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * 
 */
public class CyNodeViewShowClusterlinksMenuFactory implements CyNodeViewContextMenuFactory {

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CyNodeViewContextMenuFactory#createMenuItem(org.cytoscape.view.model.CyNetworkView, org.cytoscape.view.model.View)
	 */
	@Override
	public CyMenuItem createMenuItem(CyNetworkView netView, final View<CyNode> nodeView) {
		CyMenuItem cyMenuItem = null;
		JMenuItem menuItem = new JMenuItem("Show cluster links");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CnSEvent ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
				CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
				
				ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
				ev.addParameter(CnSViewManager.VIEW, view);
				CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
				
				ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
				ev.addParameter(CnSPartitionManager.NETWORK, network);
				CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
				CnSNode node = partition.getClusterNode(nodeView.getModel().getSUID());
				
				CnSCluster cluster = null;
				for (CnSCluster c : partition.getClusters()) {
					if (c.getCyNode() == node.getCyNode()) {
						cluster = c;
						break;
					}
				}
				String l = "";
				if (cluster != null) {
					for (CnSClusterLink cl : partition.getClusterLinks()) {
						if (cl.getSource() == cluster) {
							l += cl.getTarget().getName() + "\n";
							for (CnSEdge ce : cl.getEdges()) {
								l += "  " + ce.getCyEdge().getSource().getSUID() + " -> " + ce.getCyEdge().getTarget().getSUID() + "\n";
							}
						}
						else if (cl.getTarget() == cluster) {
							l += cl.getSource().getName() + "\n";
							for (CnSEdge ce : cl.getEdges()) {
								l += "  " + ce.getCyEdge().getSource().getSUID() + " -> " + ce.getCyEdge().getTarget().getSUID() + "\n";
							}
						}
						
					}
				}
				else 
					l = "Cluster not found !";
				JOptionPane.showMessageDialog(null,  l);
			}
			
		});
		cyMenuItem = new CyMenuItem(menuItem, 0);
		return cyMenuItem;
	}

}
