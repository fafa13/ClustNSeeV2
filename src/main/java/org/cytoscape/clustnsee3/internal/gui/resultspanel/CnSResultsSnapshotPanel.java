/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 27 avr. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.resultspanel;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSResultsSnapshotPanel extends CnSPanel {
	private static final long serialVersionUID = 8392860870801722055L;
	
	public CnSResultsSnapshotPanel(ImageIcon value, boolean isSelected) {
		super();
		if (isSelected) {
			setBorder(BorderFactory.createBevelBorder(1, Color.red, Color.red.darker()));
			setBackground(Color.yellow);
		}
		else {
			setBorder(BorderFactory.createLoweredSoftBevelBorder());
			setBackground(Color.white);
		}
		setOpaque(true);
		initGraphics(value, isSelected);
	}

	private void initGraphics(ImageIcon value, boolean isSelected) {
		super.initGraphics();
		JButton b = new JButton(value);
		if (isSelected) b.setBackground(Color.yellow);
		b.setBorder(null);
		addComponent(b, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 0, 0, 0, 0, 0, 0);
	}
}
