/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 18 juil. 2020
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
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.menu.contextual.action.CnSAnnotateClusterAction;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * 
 */
public class CnSAnnotateClusterMenuFactory implements CyNodeViewContextMenuFactory {
	private CnSAnnotateClusterAction annotateAction;
	private CnSCluster cluster;
	
	public CnSAnnotateClusterMenuFactory() {
		super();
		annotateAction = new CnSAnnotateClusterAction();
		cluster = null;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CyNodeViewContextMenuFactory#createMenuItem(org.cytoscape.view.model.CyNetworkView, org.cytoscape.view.model.View)
	 */
	@Override
	public CyMenuItem createMenuItem(CyNetworkView arg0, View<CyNode> nodeView) {
		CyMenuItem ret = null;
		CnSEvent ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER, this.getClass());
		CnSView view = (CnSView)CnSEventManager.handleMessage(ev, true).getValue();
		
		for (CnSCluster c : view.getClusters())
			if (c.getCyNode() == nodeView.getModel()) {
				cluster = c;
				break;
			}
		
		if (cluster != null) {
			JMenuItem menuItem = new JMenuItem(CnSAnnotateClusterAction.ACTION);
			ret = new CyMenuItem(menuItem, 0);
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					annotateAction.doAction(cluster);
				}
			});
		}
		return ret;
	}

}
