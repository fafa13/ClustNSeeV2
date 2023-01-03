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

package org.cytoscape.clustnsee3.internal.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSTableHeaderRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -7084782142485553305L;
	
	private Font font = new Font("serif", Font.BOLD, 12);
	private Icon ascIcon, descIcon;
    public CnSTableHeaderRenderer() {
    	super();
    	ascIcon = UIManager.getIcon("Table.ascendingSortIcon");
        descIcon = UIManager.getIcon("Table.descendingSortIcon");
    }
    
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel lab = new JLabel(value.toString());
	    lab.setBorder(BorderFactory.createRaisedBevelBorder());
	    lab.setHorizontalAlignment(CENTER);
	    lab.setHorizontalTextPosition(JLabel.LEFT);
	    lab.setFont(font);
	    lab.setOpaque(true);
	    
	    RowSorter<?> rs = table.getRowSorter();
	    if (rs != null) {
	    	List<? extends SortKey> sortKeys = rs.getSortKeys();
	    	for (SortKey sortKey : sortKeys) {
	    		if (sortKey.getColumn() == table.convertColumnIndexToModel(column)){
	    			SortOrder o = sortKey.getSortOrder();
	    			lab.setIcon(o == SortOrder.ASCENDING ? ascIcon : descIcon);
	    			break;
	    		}
	    	}
	    }
	    lab.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getPreferredWidth(), 24));
	    return lab;
	}
	
	public Font getFont() {
		return font;
	}
}
