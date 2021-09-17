/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 11 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.widget.paneltree;

import javax.swing.JTree;

/**
 * 
 */
public class CnSPanelTree extends JTree {
	private static final long serialVersionUID = 6307551690668443948L;

	public CnSPanelTree(CnSPanelTreeModel treeModel) {
		super(treeModel);
		setSelectionModel(null);
		setEditable(true);
		//setUI(new CnSPanelTreeUI());
		//UIManager.put("JTree.lineStyle", "Angled");
		//UIManager.put("Tree.paintLines", true);
	}
}
