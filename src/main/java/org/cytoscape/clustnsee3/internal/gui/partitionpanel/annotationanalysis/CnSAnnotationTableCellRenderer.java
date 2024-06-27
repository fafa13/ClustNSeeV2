/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 sept. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationanalysis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * 
 */
public class CnSAnnotationTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 5792279140943167553L;
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private static Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);
	private static JLabel label;
	
	static {
		label = new JLabel();
		label.setOpaque(true);
		label.setBorder(paddingBorder);
		label.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public CnSAnnotationTableCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
	    	label.setFont(label.getFont().deriveFont(Font.BOLD));
			label.setBackground(Color.yellow);
		}
		else {
			label.setFont(font);
			label.setBackground(Color.white);
		}
	    if (value != null)
	    	if (value instanceof Double) {
	    		if (column == 1 || column == 3) {
	    			NumberFormat format = new DecimalFormat("#0.00%");
	    			label.setText(format.format(value));
	    		}
	    		else
	    			label.setText(value.toString());
	    	}
	    	else
	    		label.setText(value.toString());
	    else
	    	label.setText("N/A");
		return label;
	}
}
