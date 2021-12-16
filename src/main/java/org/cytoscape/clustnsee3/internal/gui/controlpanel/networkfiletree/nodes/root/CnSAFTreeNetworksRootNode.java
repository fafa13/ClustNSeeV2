/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 25 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.root;

import java.util.Hashtable;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.details.CnSAFTreeDetailsNodePanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode;

/**
 * 
 */
public class CnSAFTreeNetworksRootNode extends CnSPanelTreeNode {
	public static final int TITLE = 1;
	public static final int DETAILS_NODE_PANEL = 2;
	
	public CnSAFTreeNetworksRootNode(Hashtable<Integer, Object> v) {
		super(null, v);
		panel = new CnSAFTreeNetworksRootNodePanel(getData(TITLE).toString());
	}

	public CnSAFTreeDetailsNodePanel getDetailsNodePanel() {
		return (CnSAFTreeDetailsNodePanel)getData(DETAILS_NODE_PANEL);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return getData(TITLE).toString();
	}
}
