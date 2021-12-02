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

import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.CnSNetworksTreeModel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.root.CnSAFTreeNetworksRootNode;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTree;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeCellEditor;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAFTreeDetailsNodePanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = -3595555738562109511L;
	private static final int ANNOTAION_FILE = 1;
	private static final int NB_ANNOTATIONS = 2;
	private static final int NB_NODES = 3;

	private Hashtable<Integer, Object> value;
	private CnSPanelTree networksTree;
	private CnSNetworksTreeModel networksTreeModel;
	private CnSAFTreeNetworksRootNode rootNode;
	
	public CnSAFTreeDetailsNodePanel(Hashtable<Integer, Object> v) {
		super();
		value =v;
	}
	
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel("Location :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		CnSNodeAnnotationFile nodeAnnotationFile = (CnSNodeAnnotationFile)value.get(ANNOTAION_FILE);
		String path = nodeAnnotationFile.getFile().getPath();
		label = new JLabel(path.substring(0, path.lastIndexOf(File.separatorChar)));
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		label = new JLabel("Annotations :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 1, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		label = new JLabel(value.get(NB_ANNOTATIONS).toString());
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 1, 1, 1, 0.0, 0.0, CnSPanel.WEST ,CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		label = new JLabel("Nodes :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 2, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		label = new JLabel(value.get(NB_NODES).toString());
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 2, 1, 1, 0.0, 0.0, CnSPanel.WEST ,CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		
		Hashtable<Integer, Object> v= new Hashtable<Integer, Object>();
		v.put(CnSAFTreeNetworksRootNode.TITLE, "Networks");
		rootNode = new CnSAFTreeNetworksRootNode(v);
		rootNode.getPanel().deriveFont(Font.PLAIN, 12);
		rootNode.getPanel().initGraphics();
		
		networksTreeModel = new CnSNetworksTreeModel(rootNode);
		//networksTreeModel.addNetwork(rootNode, null, NB_ANNOTATIONS, NB_NODES);
		
		networksTree = new CnSPanelTree(networksTreeModel);
		networksTree.setShowsRootHandles(true);
		networksTree.setCellRenderer(new CnSPanelTreeCellRenderer());
		networksTree.setCellEditor(new CnSPanelTreeCellEditor());
		addComponent(networksTree, 0, 3, 1, 1, 1.0, 1.0, CnSPanel.CENTER ,CnSPanel.BOTH, 5, 5, 5, 5, 0, 0);
		
		setBackground(Color.WHITE);
		setOpaque(false);
	}
	public CnSAFTreeNetworksRootNode getNetworksRootNode() {
		return rootNode;
	}
}
