/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSPartitionTableHeaderRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -7084782142485553305L;
	
	private Font font = new Font("serif", Font.BOLD, 12);
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel lab = new JLabel((String)value);
	    lab.setBorder(BorderFactory.createRaisedBevelBorder());
	    lab.setHorizontalAlignment(CENTER);
	    lab.setHorizontalTextPosition(JLabel.LEFT);
	    lab.setFont(font);
	    return lab;
	}
}
