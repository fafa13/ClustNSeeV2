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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.network;

import java.awt.Color;
import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.CnSAFTreeModel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTree;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeCellEditor;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;

/**
 * 
 */
public class CnSAFTreeNetworksPanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = 8723572659604754855L;
	public static final int TITLE = 1;
	public static final int DATA = 2;
	
	private CnSPanelTree networksTree;
	private CnSAFTreeModel networksTreeModel;
	private CnSAFTreeRootNode rootNode;
	private Hashtable<Integer, Object> value;
	
	public CnSAFTreeNetworksPanel(Hashtable<Integer, Object> v) {
		super();
		value = v;
	}
	
	public void initGraphics() {
		super.initGraphics();
		rootNode = new CnSAFTreeRootNode(value);
		rootNode.getPanel().deriveFont(11);
		rootNode.getPanel().initGraphics();
		networksTreeModel = new CnSAFTreeModel(rootNode);
		networksTree = new CnSPanelTree(networksTreeModel);
		
		networksTree.setCellRenderer(new CnSPanelTreeCellRenderer());
		networksTree.setCellEditor(new CnSPanelTreeCellEditor());
		addComponent(networksTree, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 0, 0, 0, 0, 0, 0);
		
		setBackground(Color.WHITE);
		setOpaque(false);
	}
}
