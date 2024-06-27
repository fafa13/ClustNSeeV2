/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 19 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSPartitionTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 9173113458642486427L;
	private Font font = new Font("SansSerif", Font.PLAIN, 12);
	private static Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);
	private static JLabel label;
	
	static {
		label = new JLabel();
		label.setOpaque(true);
		label.setBorder(paddingBorder);
		label.setHorizontalAlignment(SwingConstants.CENTER);
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 4)
			//label.setText(String.valueOf(((Vector)value).size()));
			label.setText(String.valueOf(value));
		else
			label.setText(value.toString());
		if (isSelected) {
			label.setFont(font.deriveFont(Font.BOLD));
			label.setBackground(Color.yellow);
		}
		else {
			label.setFont(font);
			label.setBackground(Color.white);
		}
		return label;
	}
}
