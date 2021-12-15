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

import java.awt.event.ActionEvent;
import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;

/**
 * 
 */
public class CnSAFTreeNetworkNetnameDetailsNode extends CnSPanelTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4694232795562773696L;

	public CnSAFTreeNetworkNetnameDetailsNode(Hashtable<Integer, Object> v) {
		super(v);
		panel = new CnSAFTreeNetworkNetnameDetailsNodePanel(v);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
