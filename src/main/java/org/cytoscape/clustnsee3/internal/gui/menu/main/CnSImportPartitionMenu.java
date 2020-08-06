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

/**
 * 
 */
public class CnSImportPartitionMenu extends AbstractCyAction {
	private static final long serialVersionUID = -5381959952948728885L;
	private static CnSImportPartitionMenu instance;
	
	private CnSImportPartitionMenu() {
		super("Import partition"); 						
		setPreferredMenu("Apps.Clust&see");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean insertSeparatorAfter() {
		return true;
	}
	
	public static CnSImportPartitionMenu getInstance() {
		if (instance == null) instance = new CnSImportPartitionMenu();
		return instance;
	}
}
