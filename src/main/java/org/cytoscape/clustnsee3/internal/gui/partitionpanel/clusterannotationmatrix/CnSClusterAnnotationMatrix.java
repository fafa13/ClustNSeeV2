/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 mai 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusterannotationmatrix;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTableCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTable;

/**
 * 
 */
public class CnSClusterAnnotationMatrix {
	private CnSTable matrix;
	private JScrollPane scrollPane;
	
	public CnSClusterAnnotationMatrix() {
		super();
		matrix = new CnSTable() {
			private static final long serialVersionUID = 1529937539415317297L;

			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				int r = rowAtPoint(me.getPoint());
				return matrix.getValueAt(r, c).toString();
			}
		};
		matrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		matrix.setRowHeight(26);
		scrollPane = new JScrollPane(matrix);
		//matrix.addPropertyChangeListener(this);
		matrix.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    	scrollPane.setRowHeaderView(matrix);
    	scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, matrix.getTableHeader());
    	//scrollPane.getRowHeader().addChangeListener(this);
    	matrix.setDefaultRenderer(Object.class, new CnSPartitionTableCellRenderer());
    	matrix.setDefaultRenderer(Integer.class, new CnSPartitionTableCellRenderer());
    	matrix.setDefaultRenderer(Double.class, new CnSPartitionTableCellRenderer());
    	matrix.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public CnSTable getTable() {
		return matrix;
	}
	
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
}
