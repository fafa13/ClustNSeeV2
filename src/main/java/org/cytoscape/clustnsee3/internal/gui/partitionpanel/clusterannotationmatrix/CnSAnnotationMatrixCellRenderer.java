/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 mai 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusterannotationmatrix;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSAnnotationMatrixCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5495473881938299625L;
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private double threshold;
	private int stat;
	
	public CnSAnnotationMatrixCellRenderer(double thr, int stat) {
		super();
		threshold = thr;
		this.stat = stat;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		label.setOpaque(true);
		if (isSelected) {
	    	label.setFont(font.deriveFont(Font.BOLD));
			label.setBackground(Color.yellow);
		}
		else {
			label.setFont(font);
			label.setBackground(Color.white);
		}
	    label.setText(value.toString());
	    if ((stat == 0) && ((Double)value < threshold))
	    	label.setBackground(Color.green);
	    else if ((stat == 1) && ((Double)value > threshold))
	    	label.setBackground(Color.green);
		return label;
	}
}
