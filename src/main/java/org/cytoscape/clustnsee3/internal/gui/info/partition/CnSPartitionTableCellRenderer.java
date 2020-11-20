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

package org.cytoscape.clustnsee3.internal.gui.info.partition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSPartitionTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 9173113458642486427L;
	private Font font = new Font("serif", Font.PLAIN, 12);
	private Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel(value.toString());
	    label.setFont(font);
	    label.setOpaque(true);
	    label.setBorder(paddingBorder);
	    if (isSelected) {
	    	label.setFont(label.getFont().deriveFont(Font.BOLD));
			label.setBackground(Color.yellow.darker());
		}
		else
			label.setBackground(Color.white);
	    return label;
	}
}
