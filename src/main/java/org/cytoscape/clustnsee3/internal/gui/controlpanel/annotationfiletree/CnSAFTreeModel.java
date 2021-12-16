/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 26 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeModel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

public class CnSAFTreeModel extends CnSPanelTreeModel {
	public CnSAFTreeModel(CnSPanelTreeNode treeNode) {
		super(treeNode);
	}
	
	public void addAnnotationFile(CnSPanelTreeNode parent, CnSNodeAnnotationFile annotationFile, int nbAnnotations, int nbNodes) {
		CnSAFTreeFileNode node = null;
		if (!contains(annotationFile.getFile())) {
			Hashtable<Integer, Object> v = new Hashtable<Integer, Object>();
			v.put(CnSAFTreeFileNode.ANNOTATION_FILE, annotationFile);
			v.put(CnSAFTreeFileNode.NB_ANNOTATIONS, nbAnnotations);
			v.put(CnSAFTreeFileNode.NB_NODES, nbNodes);
			node = new CnSAFTreeFileNode((CnSAFTreeRootNode)getRoot(), v);
			node.setEditable(true);
			
			System.err.println("node : " + node.getClass());
			System.err.println("parent : " + parent.getClass());
			System.err.println("nb child : " + parent.getChildCount());

			CnSAFTreeDetailsNode detailsNode = new CnSAFTreeDetailsNode(node, v);
			detailsNode.setEditable(true);
			detailsNode.getPanel().deriveFont(11);
			//detailsNode.getPanel().initGraphics();
			
			//node.addChild(detailsNode);
			//insertNodeInto(detailsNode, node, node.getChildCount());
		}
	}
	
	public boolean contains(File f) {
		Enumeration<CnSPanelTreeNode> nodes = (Enumeration<CnSPanelTreeNode>)(((CnSPanelTreeNode)getRoot()).children());
		while (nodes.hasMoreElements()) {
			CnSPanelTreeNode node = nodes.nextElement();
			CnSNodeAnnotationFile af = (CnSNodeAnnotationFile)node.getData(CnSAFTreeFileNode.ANNOTATION_FILE);
			if (af.getFile().getAbsolutePath().equals(f.getAbsolutePath())) return true;
		}
		return false;
	}

	public void printStructure(CnSPanelTreeNode node, int level) {
		printNode(node, level);
		for (int i = 0; i < node.getChildCount(); i++) printStructure(node.getChildAt(i), level + 1);
	}
	private void printNode(CnSPanelTreeNode node, int level) {
		for (int i = 0; i < level; i++) System.err.print("  ");
		System.err.println(node.getValue());
	}
}
