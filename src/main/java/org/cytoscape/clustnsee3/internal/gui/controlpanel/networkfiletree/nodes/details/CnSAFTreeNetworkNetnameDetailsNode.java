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

import org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname.CnSAFTreeNetworkNetnameNode;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;

public class CnSAFTreeNetworkNetnameDetailsNode extends CnSPanelTreeNode {
	public CnSAFTreeNetworkNetnameDetailsNode(CnSAFTreeNetworkNetnameNode parent, Hashtable<Integer, Object> v) {
		super(parent, v);
		panel = new CnSAFTreeNetworkNetnameDetailsNodePanel(v);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return ((CnSAFTreeNetworkNetnameDetailsNodePanel)panel).getValue();
	}
}
