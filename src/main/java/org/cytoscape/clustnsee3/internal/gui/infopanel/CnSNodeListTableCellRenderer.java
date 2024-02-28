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

import java.awt.Component;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTableCellRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSNodeListTableCellRenderer extends CnSTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		System.err.println("******************* " + value.getClass());
		if (value instanceof Vector) {
			StringBuilder sb = new StringBuilder();
			for (CnSNodeAnnotation na : (Vector<CnSNodeAnnotation>)value) {
				sb.append(na.getValue());
				sb.append(";");
			}
			sb.deleteCharAt(sb.length() - 1);
			label.setText(sb.toString());
		}
		else
			label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return label;
	}
}
