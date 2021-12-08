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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.root;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;

/**
 * 
 */
public class CnSAFTreeRootNodePanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = 5954586797014306124L;
	private String value;
	private CnSButton addButton;
	private ImageIcon icon_delete;
	
	public CnSAFTreeRootNodePanel(String value) {
		super();
		this.value = value;
		icon_delete = new ImageIcon(getClass().getResource("/plus.png"));
		addButton = new CnSButton(icon_delete);
	}
	public CnSButton getAddButton() {
		return addButton;
	}
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel(value);
		label.setFont(font);
		label.setForeground(Color.BLACK);
		System.err.println("value = " + value);
		addComponent(label, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER ,CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
		
		addButton.setPreferredSize(new Dimension(icon_delete.getIconWidth() + 4, icon_delete.getIconHeight() + 4));
		addButton.setFocusable(false);
		addButton.setActionCommand("add_file");
		addComponent(addButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		
		setBackground(Color.WHITE);
		setBorder(null);
		setOpaque(false);
	}
}
