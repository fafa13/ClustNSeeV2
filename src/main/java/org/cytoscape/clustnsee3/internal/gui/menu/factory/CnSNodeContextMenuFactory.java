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

package org.cytoscape.clustnsee3.internal.gui.menu.factory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.menu.action.CnSCompressClusterNodeAction;
import org.cytoscape.clustnsee3.internal.gui.menu.action.CnSExpandClusterNodeAction;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

/**
 * 
 */
public class CnSNodeContextMenuFactory implements CyNodeViewContextMenuFactory {
	private String expandCompressText = "";
	
	private CnSExpandClusterNodeAction expandAction = new CnSExpandClusterNodeAction();
	
	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CyNodeViewContextMenuFactory#createMenuItem(org.cytoscape.view.model.CyNetworkView, org.cytoscape.view.model.View)
	 */
	@Override
	public CyMenuItem createMenuItem(CyNetworkView netView, final View<CyNode> nodeView) {
		CyMenuItem cyMenuItem = null;
		
		CnSEvent ev =  new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
		final CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
		
		expandCompressText = view.getExpandCompressText(nodeView, netView);
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
				menuItem.addActionListener(new CnSCompressClusterNodeAction());
			cyMenuItem = new CyMenuItem(menuItem, 0);
		}
		return cyMenuItem;
	}
}