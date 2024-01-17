/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 23 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.tree.TreeNode;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.controlpanel.CnSControlPanel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNode;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;

public class CnSAFTreeNetworkNetnameNode extends CnSPanelTreeNode implements ActionListener {
	public final static int NETWORK_NAME = 1;
	public static final int NETWORK = 2;
	public static final int ANNOTATION_FILE = 3;
	
	public CnSAFTreeNetworkNetnameNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeNetworkNetnameNodePanel(getData(NETWORK).toString(), this);
		panel.initListeners(this);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreeNodSystem.err.println("ACTIONPERFORMED - getData(ANNOTATION_FILE) = " + getData(ANNOTATION_FILE));
	 */
	@Override
	public Object getValue() {
		return getData(NETWORK);
	}
	
	public void closeButtonAction(ActionEvent e) { 
		
	}
	public void bhButtonAction(ActionEvent e) { 
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("remove_network")) {
			System.err.println("ACTIONPERFORMED - getData(ANNOTATION_FILE) = " + getData(ANNOTATION_FILE));
			CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.DEANNOTATE_NETWORK, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION_FILE, getData(ANNOTATION_FILE));
			ev.addParameter(CnSNodeAnnotationManager.NETWORK, getData(NETWORK));
			CnSEventManager.handleMessage(ev, true);
			
			ev = new CnSEvent(CnSControlPanel.REMOVE_MAPPED_NETWORK, CnSEventManager.CONTROL_PANEL, this.getClass());
			ev.addParameter(CnSControlPanel.TREE_FILE_NODE, this);
			CnSEventManager.handleMessage(ev, true);
		}
		else if (e.getActionCommand().equals("bh")) {
		
		}
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
