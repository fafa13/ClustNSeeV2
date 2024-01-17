/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 3 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusteranalysis;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSClusterTable extends JTable {
	private static final long serialVersionUID = -5658527590778206930L;

	public CnSClusterTable() {
		super();
		setDefaultRenderer(Object.class, new CnSClusterTableCellRenderer());
		setDefaultRenderer(Double.class, new CnSClusterTableCellRenderer());
		setDefaultRenderer(Integer.class, new CnSClusterTableCellRenderer());
	}
	public CnSClusterTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
		setDefaultRenderer(Object.class, new CnSClusterTableCellRenderer());
		setDefaultRenderer(Double.class, new CnSClusterTableCellRenderer());
		setDefaultRenderer(Integer.class, new CnSClusterTableCellRenderer());
	}
	public void fireTableDataChanged() {
		if (getModel() instanceof CnSClusterTableModel) {
			((CnSClusterTableModel)getModel()).refreshModel();
			((CnSClusterTableModel)getModel()).fireTableDataChanged();
		}
	}
	
	public String getToolTipText(MouseEvent me) {
		int c = columnAtPoint(me.getPoint());
		int r = rowAtPoint(me.getPoint());
		return getValueAt(r, c).toString();
	}
	
	public void clear() {
		
	}
	public int indexOf(CnSNodeAnnotation ann) {
		return ((CnSClusterTableModel)getModel()).getIndex(ann);
	}
}
