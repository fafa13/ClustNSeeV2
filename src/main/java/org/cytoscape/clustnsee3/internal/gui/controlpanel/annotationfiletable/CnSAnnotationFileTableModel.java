/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletable;

import java.io.File;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAnnotationFileTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 431987641574491635L;
	private Vector<Vector<?>> data;
	private static final String[] columnNames = {"File", "Annotations", "Nodes", "Mapped annot.", "Found nodes", "Network"}; 
	
	private static final Class<?>[] columnClasses = {CnSNodeAnnotationFile.class, Integer.class, Integer.class, Integer.class, Integer.class, CyNetwork.class};

	/**
	 * @param
	 * @return
	 */
	public CnSAnnotationFileTableModel() {
		super();
		data = new Vector<Vector<?>>();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[columnIndex];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		if (data.size() > row) 
			return data.elementAt(row).elementAt(col);
		return null;
	}
	public void addItem(CnSNodeAnnotationFile file, int[] stats, CyNetwork network) {
		Vector<Object> v = new Vector<Object>();
		v.addElement(file);
		v.addElement(stats[1]);
		v.addElement(stats[0]);
		v.addElement(stats[3]);
		v.addElement(stats[2]);
		v.addElement(network);
		data.addElement(v);
	}
	
	public void removeItem(CnSNodeAnnotationFile file) {
		for (Vector<?> v : data)
			if (v.firstElement() == file) {
				data.remove(v);
				break;
			}
	}
	
	public boolean contains(File f) {
		for (Vector<?> v : data)
			if (((CnSNodeAnnotationFile)v.firstElement()).getFile() ==  f)
				return true;
		return false;
	}
}
