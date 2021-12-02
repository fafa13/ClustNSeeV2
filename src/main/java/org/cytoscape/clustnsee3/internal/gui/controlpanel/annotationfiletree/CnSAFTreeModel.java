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
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.details.CnSAFTreeNetworkNetnameDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.root.CnSAFTreeNetworksRootNode;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeModel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAFTreeModel extends CnSPanelTreeModel {
	private static final long serialVersionUID = 7818357834166609872L;
	
	public CnSAFTreeModel(CnSPanelTreeNode treeNode) {
		super(treeNode);
	}

	public void addAnnotationFile(CnSPanelTreeNode parent, CnSNodeAnnotationFile annotationFile, int nbAnnotations, int nbNodes) {
		if (!contains(annotationFile.getFile())) {
			Hashtable<Integer, Object> v = new Hashtable<Integer, Object>();
			v.put(CnSAFTreeFileNode.ANNOTATION_FILE, annotationFile);
			v.put(CnSAFTreeFileNode.NB_ANNOTATIONS, nbAnnotations);
			v.put(CnSAFTreeFileNode.NB_NODES, nbNodes);
			//v.put(CnSAFTreeFileNode.NETWORKS, new Vector<Vector<Object>>());
			CnSAFTreeFileNode node = new CnSAFTreeFileNode(parent, v);
			node.setEditable(true);
			node.getPanel().initGraphics();
			insertNodeInto(node, parent, root.getChildCount());
			CnSAFTreeDetailsNode detailsNode = new CnSAFTreeDetailsNode(node, v);
			detailsNode.setEditable(true);
			detailsNode.getPanel().deriveFont(11);
			detailsNode.getPanel().initGraphics();
			insertNodeInto(detailsNode, node, node.getChildCount());
		}
	}
	
	public void addMappedNetwork(CnSNodeAnnotationFile annotationFile, CyNetwork network, CnSAFTreeNetworksRootNode rootNode,  int nbAnnotations, int nbNodes) {
		Enumeration<CnSPanelTreeNode> fileNodes = (Enumeration<CnSPanelTreeNode>)(getRootNode().children());
		while (fileNodes.hasMoreElements()) {
			CnSAFTreeFileNode node = (CnSAFTreeFileNode)fileNodes.nextElement();
			if (((CnSNodeAnnotationFile)node.getData(CnSAFTreeFileNode.ANNOTATION_FILE)).getFile().getAbsolutePath().equals(annotationFile.getFile().getAbsolutePath())) {
				Hashtable<Integer, Object> v = new Hashtable<Integer, Object>();
				v.put(CnSAFTreeNetworkNetnameNode.NETWORK, network);
				CnSAFTreeNetworkNetnameNode networkNode = new CnSAFTreeNetworkNetnameNode(node, v);
				insertNodeInto(networkNode, rootNode, rootNode.getChildCount());
				CnSAFTreeNetworkNetnameDetailsNode detailsNode = new CnSAFTreeNetworkNetnameDetailsNode(node, v);
				detailsNode.setEditable(true);
				detailsNode.getPanel().deriveFont(11);
				detailsNode.getPanel().initGraphics();
				insertNodeInto(detailsNode, networkNode, networkNode.getChildCount());
				break;
			}
		}
	}
	
	public boolean contains(File f) {
		Enumeration<CnSPanelTreeNode> nodes = rootNode.children();
		while (nodes.hasMoreElements()) {
			CnSPanelTreeNode node = nodes.nextElement();
			CnSNodeAnnotationFile af = (CnSNodeAnnotationFile)node.getData(CnSAFTreeFileNode.ANNOTATION_FILE);
			if (af.getFile().getAbsolutePath().equals(f.getAbsolutePath())) return true;
		}
		return false;
	}
}
