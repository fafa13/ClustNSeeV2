/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 août 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationtable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSAnnotationTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private Font font = new Font("serif", Font.PLAIN, 12);
	private Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		if (value != null) label.setText(value.toString());
		label.setFont(font);
		label.setOpaque(true);
		label.setBorder(paddingBorder);
	    if (isSelected) {
	    	label.setFont(label.getFont().deriveFont(Font.BOLD));
			label.setBackground(Color.yellow);
		}
		else
			label.setBackground(Color.white);
	    if (value != null) {
	    	if (value instanceof Double) {
	    		NumberFormat format = new DecimalFormat("#0.00%");
	    		label.setText(format.format(value));
	    	}
		}
	    return label;
	}
}
