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

import javax.swing.SwingUtilities;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.details.CnSAFTreeNetworkNetnameDetailsNode;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeModel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSNetworksTreeModel extends CnSPanelTreeModel {
	private static final long serialVersionUID = 3498551477771510355L;
	public CnSNetworksTreeModel(CnSPanelTreeNode treeNode) {
		super(treeNode);
	}
	public CnSAFTreeNetworkNetnameNode addNetwork(CyNetwork network, CnSNodeAnnotationFile af, int mappedAnnotations, int mappedNodes, int networkNodes, int fileAnnotations) {
		final Hashtable<Integer, Object> v = new Hashtable<Integer, Object>();
		v.put(CnSAFTreeNetworkNetnameNode.NETWORK, network);
		v.put(CnSAFTreeNetworkNetnameNode.ANNOTATION_FILE, af);
		final CnSAFTreeNetworkNetnameNode networkNode = new CnSAFTreeNetworkNetnameNode(rootNode, v);
		v.clear();
		v.put(CnSAFTreeNetworkNetnameDetailsNode.MAPPED_ANNOTATIONS, mappedAnnotations);
		v.put(CnSAFTreeNetworkNetnameDetailsNode.MAPPED_NODES, mappedNodes);
		v.put(CnSAFTreeNetworkNetnameDetailsNode.NETWORK_NODES, networkNodes);
		v.put(CnSAFTreeNetworkNetnameDetailsNode.FILE_ANNOTATIONS, fileAnnotations);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				CnSAFTreeNetworkNetnameDetailsNode detailsNode = new CnSAFTreeNetworkNetnameDetailsNode(networkNode, v);
				detailsNode.setEditable(true);
				detailsNode.getPanel().deriveFont(11);
			}
			
		});
		
		
		return networkNode;
	}
	public void removeNetwork(CnSAFTreeNetworkNetnameNode netNode, CyNetwork network, CnSNodeAnnotationFile annotationFile) {
		netNode.remove();
	}
}
