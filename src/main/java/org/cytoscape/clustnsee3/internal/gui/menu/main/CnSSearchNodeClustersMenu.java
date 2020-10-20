/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 11 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.main;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.clustnsee3.internal.gui.dialog.CnSSearchNodeClustersDialog;

/**
 * 
 */
public class CnSSearchNodeClustersMenu extends AbstractCyAction {
	private static final long serialVersionUID = 5680982903583583075L;
	private static CnSSearchNodeClustersMenu instance;
	private boolean en;
	
	private CnSSearchNodeClustersMenu() {
		super("Search node clusters"); 
		setPreferredMenu("Apps.Clust&see");
		en = true;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		CnSSearchNodeClustersDialog dialog = new CnSSearchNodeClustersDialog();
		dialog.pack();
		dialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - dialog.getWidth() / 2, 
				(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - dialog.getHeight() / 2);
		dialog.setVisible(true);
	}
	
	public static CnSSearchNodeClustersMenu getInstance() {
		if (instance == null) instance = new CnSSearchNodeClustersMenu();
		return instance;
	}
	
	public void setEnabled_(boolean b) {
		super.setEnabled(b);
		en = b;
	}
	public boolean isEnabled() {
		return en;
	}
}
