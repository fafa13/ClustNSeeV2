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

import java.util.Hashtable;

import javax.swing.tree.TreeNode;

import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAFTreeDetailsNode extends CnSPanelTreeNode {
	private static final int ANNOTATION_FILE = 1;
	private static final int NB_ANNOTATIONS = 2;
	private static final int NB_NODES = 3;

	public CnSAFTreeDetailsNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeDetailsNodePanel((CnSNodeAnnotationFile)getData(ANNOTATION_FILE), (Integer)getData(NB_ANNOTATIONS), (Integer)getData(NB_NODES));
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getData(ANNOTATION_FILE);
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
