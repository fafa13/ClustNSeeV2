/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 10 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.CnSNetworksTreeModel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.root.CnSAFTreeNetworksRootNode;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTree;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeCellEditor;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreePanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

public class CnSAFTreeDetailsNodePanel extends CnSPanelTreePanel implements TreeExpansionListener {
	private static final long serialVersionUID = -3595555738562109511L;
	
	private CnSPanelTree networksTree;
	private CnSNetworksTreeModel networksTreeModel;
	private CnSAFTreeNetworksRootNode rootNode;
	private CnSNodeAnnotationFile annotationFile;
	private int nbAnnotations, nbNodes;
	
	public CnSAFTreeDetailsNodePanel(CnSNodeAnnotationFile annotationFile, int nbAnnotations, int nbNodes) {
		super();
		this.annotationFile = annotationFile;
		this.nbAnnotations = nbAnnotations;
		this.nbNodes = nbNodes;
		initGraphics();
		networksTree.addTreeExpansionListener(this);
	}
	
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel("Location :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		String path = annotationFile.getFile().getPath();
		label = new JLabel(path.substring(0, path.lastIndexOf(File.separatorChar)));
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 10, 0, 0);
		label = new JLabel("Annotations :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 1, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		label = new JLabel(String.valueOf(nbAnnotations));
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 1, 1, 1, 0.0, 0.0, CnSPanel.WEST ,CnSPanel.NONE, 5, 5, 0, 10, 0, 0);
		label = new JLabel("Targets :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 2, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		label = new JLabel(String.valueOf(nbNodes));
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 2, 1, 1, 0.0, 0.0, CnSPanel.WEST ,CnSPanel.NONE, 5, 5, 0, 10, 0, 0);
		
		Hashtable<Integer, Object> v= new Hashtable<Integer, Object>();
		v.put(CnSAFTreeNetworksRootNode.TITLE, "Networks");
		v.put(CnSAFTreeNetworksRootNode.DETAILS_NODE_PANEL, this);
		rootNode = new CnSAFTreeNetworksRootNode(v);
		rootNode.getPanel().deriveFont(Font.PLAIN, 12);
		networksTreeModel = new CnSNetworksTreeModel(rootNode);
		networksTree = new CnSPanelTree(networksTreeModel);
		networksTree.setShowsRootHandles(true);
		networksTree.setCellRenderer(new CnSPanelTreeCellRenderer());
		networksTree.setCellEditor(new CnSPanelTreeCellEditor());
		networksTree.setRowHeight(0);
		addComponent(networksTree, 0, 3, 2, 1, 1.0, 1.0, CnSPanel.CENTER ,CnSPanel.BOTH, 5, 10, 5, 10, 0, 0);
		
		setBackground(Color.WHITE);
		setOpaque(false);
	}
	public CnSAFTreeNetworksRootNode getNetworksRootNode() {
		return rootNode;
	}
	public CnSNetworksTreeModel getNetworksTreeModel() {
		return networksTreeModel;
	}

	public CnSPanelTree getNetworksTree() {
		return networksTree;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		CnSEvent ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL);
		CnSEventManager.handleMessage(ev);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		CnSEvent ev = new CnSEvent(CnSControlPanel.REFRESH, CnSEventManager.CONTROL_PANEL);
		CnSEventManager.handleMessage(ev);
	}
}
