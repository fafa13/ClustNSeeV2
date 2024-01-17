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
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

/**
 * 
 */
public class CnSPartitionTableFixedColumnRenderer implements TableCellRenderer {
	private Font font = new Font("serif", Font.PLAIN, 12);
	private static JLabel label;
	private static Border paddingBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
	
	static {
		label = new JLabel();
		label.setOpaque(true);
		label.setBorder(paddingBorder);
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
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
