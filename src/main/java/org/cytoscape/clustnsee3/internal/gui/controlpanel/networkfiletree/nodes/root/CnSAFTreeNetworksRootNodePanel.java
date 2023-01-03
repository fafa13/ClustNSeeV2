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

import java.awt.Color;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.paneltree.CnSPanelTreePanel;

public class CnSAFTreeNetworksRootNodePanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = 8311613134967963237L;
	private String value;
	
	public CnSAFTreeNetworksRootNodePanel(String value) {
		super();
		this.value = value;
		initGraphics();
	}
	
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel(value);
		label.setFont(font);
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER ,CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		
		setBackground(Color.WHITE);
		setBorder(null);
		setOpaque(false);
	}
}
