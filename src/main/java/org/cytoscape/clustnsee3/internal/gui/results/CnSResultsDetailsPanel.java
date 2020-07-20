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

package org.cytoscape.clustnsee3.internal.gui.results;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSResultsDetailsPanel extends CnSPanel {
	private static final long serialVersionUID = -5528941988598666087L;

	public CnSResultsDetailsPanel(CnSCluster value, boolean isSelected) {
		super();
		initGraphics(value);
		setBorder(BorderFactory.createLoweredSoftBevelBorder());
		setBackground(Color.white);
		setOpaque(true);
	}
	
	private void initGraphics(CnSCluster data) {
		super.initGraphics();
		JLabel l;
		l = new JLabel("Name :");
		l.setForeground(Color.blue);
		addComponent(l, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		l = new JLabel(data.getName());
		addComponent(l, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		l = new JLabel("Nodes :");
		l.setForeground(Color.blue);
		addComponent(l, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		l = new JLabel(String.valueOf(data.getNodes().size()));
		addComponent(l, 1, 1, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		l = new JLabel("Intra cluster edges :");
		l.setForeground(Color.blue);
		addComponent(l, 0, 2, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		l = new JLabel(String.valueOf(data.getInDegree()));
		addComponent(l, 1, 2, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		l = new JLabel("Extra cluster edges :");
		l.setForeground(Color.blue);
		addComponent(l, 0, 3, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		l = new JLabel(String.valueOf(data.getExtEdges().size()));
		addComponent(l, 1, 3, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		l = new JLabel("Intra/extra edges ratio :");
		l.setForeground(Color.blue);
		addComponent(l, 0, 4, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 0, 0, 0);
		l = new JLabel(String.valueOf(data.getModularity()));
		addComponent(l, 1, 4, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
	}
}
