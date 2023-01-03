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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;

/**
 * 
 */
public class CnSPartitionTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 9173113458642486427L;
	private Font font = new Font("serif", Font.PLAIN, 12);
	private Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel();
		try {
			label.setText(value.toString());
		    label.setFont(font);
		    label.setOpaque(true);
		    label.setBorder(paddingBorder);
		    double alpha;
		    CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ALPHA, CnSEventManager.ANNOTATION_MANAGER);
		    alpha = (Double)CnSEventManager.handleMessage(ev);
	    
		    if (isSelected) {
		    	label.setFont(label.getFont().deriveFont(Font.BOLD));
		    	label.setBackground(Color.yellow.darker());
		    }
		    else
		    	label.setBackground(Color.white);
		    if (column == 5) {
		    	if (value instanceof CnSEnrichmentStatValue) {
		    		CnSEnrichmentStatValue v = (CnSEnrichmentStatValue)value;
		    		NumberFormat format = new DecimalFormat("##.00%");
		    		label.setText(v.toString());
		    		if (!isSelected && v.getBhValue() <= alpha) {
		    			label.setBackground(Color.lightGray);
		    		}
		    	}
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	    return label;
	}
}
