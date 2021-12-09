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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree;

import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.details.CnSAFTreeNetworkNetnameDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.details.CnSAFTreeNetworkNetnameDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeModel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSNetworksTreeModel extends CnSPanelTreeModel {
	private static final long serialVersionUID = -7718553296538201630L;

	public CnSNetworksTreeModel(CnSPanelTreeNode treeNode) {
		super(treeNode);
	}
	public void addNetwork(CnSPanelTreeNode parent, CyNetwork network, CnSNodeAnnotationFile af, int mappedAnnotations, int mappedNodes, int networkNodes, int fileAnnotations) {
		Hashtable<Integer, Object> v = new Hashtable<Integer, Object>();
		v.put(CnSAFTreeNetworkNetnameNode.NETWORK, network);
		v.put(CnSAFTreeNetworkNetnameNode.ANNOTATION_FILE, af);
		CnSAFTreeNetworkNetnameNode networkNode = new CnSAFTreeNetworkNetnameNode(parent, v);
		insertNodeInto(networkNode, rootNode, rootNode.getChildCount());
		v.clear();
		v.put(CnSAFTreeNetworkNetnameDetailsNodePanel.MAPPED_ANNOTATIONS, mappedAnnotations);
		v.put(CnSAFTreeNetworkNetnameDetailsNodePanel.MAPPED_NODES, mappedNodes);
		v.put(CnSAFTreeNetworkNetnameDetailsNodePanel.NETWORK_NODES, networkNodes);
		v.put(CnSAFTreeNetworkNetnameDetailsNodePanel.FILE_ANNOTATIONS, fileAnnotations);
		CnSAFTreeNetworkNetnameDetailsNode detailsNode = new CnSAFTreeNetworkNetnameDetailsNode(networkNode, v);
		detailsNode.setEditable(true);
		detailsNode.getPanel().deriveFont(11);
		detailsNode.getPanel().initGraphics();
		insertNodeInto(detailsNode, networkNode, networkNode.getChildCount());
	}
	/**
	 * 
	 * @param treeFileNode 
	 * @param
	 * @return
	 */
	public void removeNetwork(CnSAFTreeNetworkNetnameNode netNode, CyNetwork network, CnSNodeAnnotationFile annotationFile) {
		removeNodeFromParent(netNode);
	}
}
