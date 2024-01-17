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
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root.CnSAFTreeRootNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.CnSNetworksTreeModel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeModel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.model.CyNetwork;

public class CnSAFTreeModel extends CnSPanelTreeModel {
	private static final long serialVersionUID = -6953596223506537360L;
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
	
	public Vector<CnSNodeAnnotationFile> getAnnotationFiles() {
		Vector<CnSNodeAnnotationFile> ret = new Vector<CnSNodeAnnotationFile>();
		Enumeration<CnSPanelTreeNode> nodes = (Enumeration<CnSPanelTreeNode>)(((CnSPanelTreeNode)getRoot()).children());
		while (nodes.hasMoreElements()) {
			CnSAFTreeFileNode node = (CnSAFTreeFileNode)nodes.nextElement();
			
			CnSNodeAnnotationFile af = (CnSNodeAnnotationFile)node.getData(CnSAFTreeFileNode.ANNOTATION_FILE);
			ret.addElement(af);
		}
		return ret;
	}
	
	public Vector<CnSAFTreeNetworkNetnameNode> getAnnotatedNetworks(CnSNodeAnnotationFile annotationfile) {
		Vector<CnSAFTreeNetworkNetnameNode> ret = new Vector<CnSAFTreeNetworkNetnameNode>();
		Enumeration<CnSPanelTreeNode> nodes = (Enumeration<CnSPanelTreeNode>)(((CnSPanelTreeNode)getRoot()).children());
		while (nodes.hasMoreElements()) {
			CnSAFTreeFileNode node = (CnSAFTreeFileNode)nodes.nextElement();
			
			CnSNodeAnnotationFile af = (CnSNodeAnnotationFile)node.getData(CnSAFTreeFileNode.ANNOTATION_FILE);
			if (af.equals(annotationfile)) {
				
				Enumeration<CnSPanelTreeNode> networknodes = node.children();
				while (networknodes.hasMoreElements()) {
					CnSAFTreeDetailsNode nnode = (CnSAFTreeDetailsNode)networknodes.nextElement();
					Enumeration<CnSPanelTreeNode> etm = ((CnSAFTreeDetailsNodePanel)nnode.getPanel()).getNetworksRootNode().children();					
					while (etm.hasMoreElements()) {
						CnSPanelTreeNode tm = etm.nextElement();
						CyNetwork cn = (CyNetwork)tm.getData(CnSAFTreeNetworkNetnameNode.NETWORK);
						System.err.println(af.getFile().getAbsolutePath() + " -> " + cn.getSUID());
						ret.addElement((CnSAFTreeNetworkNetnameNode)tm);
					}
				}
			}
		}
		return ret;
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
