/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 5 oct. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JMenuItem;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.cytoscape.clustnsee3.internal.CnSClustnseePlugin;

/**
 * 
 */
public class CnSStartMenuFactory implements CyNodeViewContextMenuFactory, ActionListener {
	private ServiceRegistration ref;
	private BundleContext context;
	private CyActivator cyActivator;
	
	/**
	 * @param
	 * @return
	 */
	public CnSStartMenuFactory(BundleContext context, CyActivator ca) {
		this.context = context;
		cyActivator = ca;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Hashtable<String, ?> dict = new Hashtable<String, Object>();
		ref = context.registerService(CnSClustnseePlugin.class.getName(), CnSClustnseePlugin.getInstance(context, cyActivator), dict);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CyNodeViewContextMenuFactory#createMenuItem(org.cytoscape.view.model.CyNetworkView, org.cytoscape.view.model.View)
	 */
	@Override
	public CyMenuItem createMenuItem(CyNetworkView netView, View<CyNode> nodeView) {
		JMenuItem menuItem = new JMenuItem("Start");
		menuItem.addActionListener(this);
		CyMenuItem cyMenuItem = new CyMenuItem(menuItem, 0);
		return cyMenuItem;
	}
	
	public ServiceRegistration getRef() {
		return ref;
	}
	
}
