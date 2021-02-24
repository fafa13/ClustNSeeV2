/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSAnnotationFileTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1974159369704905347L;
	
	private Font font = new Font("serif", Font.PLAIN, 11);
	private Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		int begin = value.toString().lastIndexOf(File.separatorChar);
		
		JLabel label = new JLabel(value.toString().substring(begin + 1));
	    label.setFont(font);
	    label.setOpaque(true);
	    label.setBorder(paddingBorder);
	    if (begin != -1) label.setToolTipText(value.toString().substring(0, begin));
	    if (isSelected) {
	    	label.setFont(label.getFont().deriveFont(Font.BOLD));
			label.setBackground(Color.lightGray);
		}
		else
			label.setBackground(Color.white);
	    return label;
	}
}
