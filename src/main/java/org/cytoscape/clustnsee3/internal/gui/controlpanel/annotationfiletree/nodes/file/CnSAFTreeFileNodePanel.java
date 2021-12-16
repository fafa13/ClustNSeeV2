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
	private CnSButton closeButton, annotate_button;
	private CnSNodeAnnotationFile value;
	
	public CnSAFTreeFileNodePanel(CnSNodeAnnotationFile nodeAnnotationFile) {
		super();
		value = nodeAnnotationFile;
		initGraphics();
	}
	public CnSButton getDeleteButton() {
		return closeButton;
	}
	public CnSButton getAnnotateButton() {
		return annotate_button;
	}
	public void initGraphics() {
		super.initGraphics();
		JLabel label = new JLabel(value.getFile().getName());
		label.setFont(font);
		
		addComponent(label, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 5, 5, 5, 0, 0, 0);
		
		ImageIcon icon_delete = new ImageIcon(getClass().getResource("/delete_annotation.gif"));
		closeButton = new CnSButton(icon_delete);
		closeButton.setPreferredSize(new Dimension(icon_delete.getIconWidth() + 4, icon_delete.getIconHeight() + 4));
		closeButton.setFocusable(false);
		closeButton.setActionCommand("delete");
		addComponent(closeButton, 1, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		
		ImageIcon icon_right = new ImageIcon(getClass().getResource("/right-arrow.gif"));
		annotate_button = new CnSButton(icon_right);
		annotate_button.setPreferredSize(new Dimension(icon_right.getIconWidth() + 4, icon_right.getIconHeight() + 4));
		annotate_button.setFocusable(false);
		annotate_button.setActionCommand("annotate");
		addComponent(annotate_button, 2, 0, 1, 1, 0.0, 0.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		
		setBackground(Color.WHITE);
		setBorder(null);
		setOpaque(false);
	}
}
