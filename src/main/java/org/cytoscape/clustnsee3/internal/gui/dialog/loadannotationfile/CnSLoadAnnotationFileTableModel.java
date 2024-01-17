/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 4 oct. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog.loadannotationfile;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * 
 */
public class CnSLoadAnnotationFileTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7914092055267256875L;
	private int nb_col;
	private Vector<String> data;
	private CnSLoadAnnotationFileDialog dialog;
	
	public CnSLoadAnnotationFileTableModel(CnSLoadAnnotationFileDialog cnSLoadAnnotationFileDialog) {
		super();
		data = new Vector<String>();
		nb_col = 0;
		dialog = cnSLoadAnnotationFileDialog;
	}
	public void setNbCol(int nc) {
		nb_col = nc;
	}
	public void addData(String s) {
		data.addElement(s);
	}
	public void clear() {
		data.clear();
	}
	public String getData(int i) {
		return data.elementAt(i);
	}
	public int getNbCol() {
		return nb_col;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return nb_col + 1;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int col) {
		if (col > 0)
			return "Col #" + String.valueOf(col);
		return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if (column > 0) {
			String[] word = data.elementAt(row).split(dialog.isColTabRadioButtonSelected()?"\t":(dialog.isColComRadioButtonSelected()?",":(dialog.isColSemRadioButtonSelected()?";":" ")));
			if (word.length > column - 1) return word[column - 1];
			return "";
		}
		return String.valueOf(row + 1);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
