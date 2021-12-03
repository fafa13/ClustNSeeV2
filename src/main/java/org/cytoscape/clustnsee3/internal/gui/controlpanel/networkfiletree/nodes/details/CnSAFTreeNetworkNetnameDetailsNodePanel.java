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

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;

/**
 * 
 */
public class CnSAFTreeNetworkNetnameDetailsNodePanel extends CnSPanelTreePanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2767529159005195713L;
	private Hashtable<Integer, Object> value;
	public static final int MAPPED_ANNOTATIONS = 1;
	public static final int MAPPED_NODES = 2;
	public static final int NETWORK_NODES = 3;
	public static final int FILE_ANNOTATIONS = 4;
	
	public CnSAFTreeNetworkNetnameDetailsNodePanel(Hashtable<Integer, Object> v) {
		super();
		value = v;
	}
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel("Annotated nodes :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST ,CnSPanel.NONE, 5, 10, 0, 0, 0, 0);
		label = new JLabel(value.get(MAPPED_NODES).toString() + " (" + (int)((Integer)value.get(MAPPED_NODES) * 10000 / (Integer)value.get(NETWORK_NODES)) / 100.0D + "%)");
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.WEST ,CnSPanel.NONE, 5, 5, 0, 10, 0, 0);
		label = new JLabel("Mapped annotations :");
		label.setFont(font.deriveFont(Font.BOLD, 11));
		label.setForeground(Color.BLUE);
		addComponent(label, 0, 1, 1, 1, 0.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 10, 5, 0, 0, 0);
		label = new JLabel(value.get(MAPPED_ANNOTATIONS).toString() + " (" + (int)((Integer)value.get(MAPPED_ANNOTATIONS) * 10000 / (Integer)value.get(FILE_ANNOTATIONS)) / 100.0D + "%)");
		label.setFont(font.deriveFont(Font.PLAIN, 11));
		addComponent(label, 1, 1, 1, 1, 0.0, 0.0, CnSPanel.WEST ,CnSPanel.NONE, 5, 5, 5, 10, 0, 0);
		setBackground(Color.WHITE);
		setOpaque(false);
	}
}
