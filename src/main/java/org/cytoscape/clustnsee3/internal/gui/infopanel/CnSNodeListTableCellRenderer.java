/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 févr. 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.infopanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;

import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTableCellRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSNodeListTableCellRenderer extends CnSTableCellRenderer {
	private static Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Vector) {
			StringBuilder sb = new StringBuilder();
			for (CnSNodeAnnotation na : (Vector<CnSNodeAnnotation>)value) {
				sb.append(na.getValue());
				sb.append(";");
			}
			if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
			label.setText(sb.toString());
		}
		else if (value instanceof JLabel) {
			JLabel l = (JLabel)value;
			l.setOpaque(true);
			l.setBorder(paddingBorder);
			if (isSelected) {
				l.setFont(l.getFont().deriveFont(Font.BOLD));
				if (l.getBackground().equals(Color.WHITE)) l.setBackground(Color.yellow);
			}
			return l;
		}
		else
			label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return label;
	}
}
