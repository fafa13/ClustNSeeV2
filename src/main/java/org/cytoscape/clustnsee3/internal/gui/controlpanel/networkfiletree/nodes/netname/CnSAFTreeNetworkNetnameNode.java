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
import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;

/**
 * 
 */
public class CnSAFTreeNetworkNetnameNode extends CnSPanelTreeNode {
	public final static int NETWORK_NAME = 1;

	public CnSAFTreeNetworkNetnameNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		super();
		panel = new CnSAFTreeNetworksNetnameNodePanel(v.get(NETWORK_NAME).toString());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
