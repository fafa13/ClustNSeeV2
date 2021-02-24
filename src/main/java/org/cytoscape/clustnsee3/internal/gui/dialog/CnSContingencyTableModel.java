/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSContingencyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -5638398154610258836L;
	private Vector<String> colName, rowName;
	private Vector<Vector<Integer>> data;
	
	/**
	 * @param
	 * @return
	 */
	public CnSContingencyTableModel(Vector<Vector<Integer>> data, CnSPartition p1, CnSPartition p2) {
		super();
		colName = new Vector<String>();
		colName.addElement("");
		rowName = new Vector<String>();
		for (int i = 0; i < p1.getClusters().size(); i++) colName.addElement("C" + (i + 1));
		for (int i = 0; i < p2.getClusters().size(); i++) rowName.addElement("C" + (i + 1));
		this.data = data;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return colName.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return rowName.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0)
			return rowName.get(row);
		return data.elementAt(row).elementAt(column - 1);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return String.class;
		else
			return Integer.class;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return colName.elementAt(columnIndex);
	}
}
