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
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeModel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAFTreeModel extends CnSPanelTreeModel {
	private static final long serialVersionUID = 7818357834166609872L;
	
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
			node = new CnSAFTreeFileNode(v);
			node.setEditable(true);
			node.getPanel().initGraphics();
			System.err.println("node : " + node.getClass());
			System.err.println("parent : " + parent.getClass());
			insertNodeInto(node, parent, parent.getChildCount());
			CnSAFTreeDetailsNode detailsNode = new CnSAFTreeDetailsNode(parent, v);
			detailsNode.setEditable(true);
			detailsNode.getPanel().deriveFont(11);
			detailsNode.getPanel().initGraphics();
			insertNodeInto(detailsNode, node, node.getChildCount());
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
}
