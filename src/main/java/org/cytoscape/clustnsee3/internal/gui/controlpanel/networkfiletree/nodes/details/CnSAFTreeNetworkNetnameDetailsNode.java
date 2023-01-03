/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 déc. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.details;

import java.util.Hashtable;

import javax.swing.tree.TreeNode;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;

public class CnSAFTreeNetworkNetnameDetailsNode extends CnSPanelTreeNode {
	public static final int MAPPED_ANNOTATIONS = 1;
	public static final int MAPPED_NODES = 2;
	public static final int NETWORK_NODES = 3;
	public static final int FILE_ANNOTATIONS = 4;
	
	public CnSAFTreeNetworkNetnameDetailsNode(CnSAFTreeNetworkNetnameNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeNetworkNetnameDetailsNodePanel((Integer)getData(MAPPED_NODES), 
				(Integer)getData(MAPPED_ANNOTATIONS), (Integer)getData(NETWORK_NODES), (Integer)getData(FILE_ANNOTATIONS));
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getData(MAPPED_ANNOTATIONS);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	@Override
	public int getIndex(TreeNode node) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}
}
