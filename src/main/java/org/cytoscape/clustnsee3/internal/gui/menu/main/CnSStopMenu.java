/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 6 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.view.style.CnSStyleManager;

/**
 * 
 */
public class CnSStopMenu extends AbstractCyAction {
	private static final long serialVersionUID = 7814074516214991713L;
	private static CnSStopMenu instance;
	private boolean en;
	
	private CnSStopMenu() {
		super("Stop"); 						
		setPreferredMenu("Apps.Clust&See");	
		en = true;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CnSEvent ev = new CnSEvent(CnSStyleManager.REMOVE_CNS_STYLES, CnSEventManager.STYLE_MANAGER, this.getClass());
		CnSEventManager.handleMessage(ev, true);
		ev = new CnSEvent(CyActivator.STOP, CnSEventManager.CY_ACTIVATOR, this.getClass());
		CnSEventManager.handleMessage(ev, true);
	}
	public static CnSStopMenu getInstance() {
		if (instance == null) instance = new CnSStopMenu();
		return instance;
	}
	public boolean insertSeparatorAfter() {
		return true;
	}
	public void setEnabled_(boolean b) {
		super.setEnabled(b);
		en = b;
	}
	public boolean isEnabled() {
		return en;
	}
}
