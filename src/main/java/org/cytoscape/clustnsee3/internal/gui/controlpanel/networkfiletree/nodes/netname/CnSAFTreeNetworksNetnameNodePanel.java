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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname;

import java.awt.Color;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;

/**
 * 
 */
public class CnSAFTreeNetworksNetnameNodePanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = 8723572659604754855L;
	public static final int TITLE = 1;
	public static final int DATA = 2;
	
	private String value;
	
	public CnSAFTreeNetworksNetnameNodePanel(String networkName) {
		super();
		value = networkName;
	}
	
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel(value);
		label.setFont(font);
		
		addComponent(label, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 5, 5, 5, 0, 0, 0);
		
		
		setBackground(Color.WHITE);
		setBorder(null);
		setOpaque(false);
	}
}
