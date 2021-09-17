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

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.paneltree.CnSPanelTreePanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAFTreeFileNodePanel extends CnSPanelTreePanel {
	private static final long serialVersionUID = 3093699473334276081L;
	private CnSButton closeButton;
	private CnSNodeAnnotationFile value;
	
	public CnSAFTreeFileNodePanel(CnSNodeAnnotationFile nodeAnnotationFile) {
		super();
		value = nodeAnnotationFile;
	}
	public CnSButton getButton() {
		return closeButton;
	}
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel(value.getFile().getName());
		label.setFont(font);
		
		addComponent(label, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 5, 5, 5, 0, 0, 0);
		
		ImageIcon icon = new ImageIcon(getClass().getResource("/delete_annotation.gif"));
		closeButton = new CnSButton(icon);
		closeButton.setPreferredSize(new Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 4));
		//closeButton.setBorder(new LineBorder(Color.BLACK));
		closeButton.setFocusable(false);
		addComponent(closeButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		setBackground(Color.WHITE);
		setBorder(null);
		setOpaque(false);
	}
}
