/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 20 avr. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.resultspanel;

import java.util.Collections;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;

/**
 * 
 */
public class CnSResultsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 288902092163456192L;
	private String colName[] = {"Snapshot", "Details"};
	private Vector<CnSCluster> cluster;
	
	/**
	 * @param
	 * @return
	 */
	public CnSResultsTableModel() {
		super();
		cluster = new Vector<CnSCluster>();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return cluster.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return colName.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return colName[columnIndex];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return ImageIcon.class;
		else if (columnIndex == 1)
			return String[].class;
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return cluster.elementAt(rowIndex).getSnapshot();
		else if (columnIndex == 1)
			return cluster.elementAt(rowIndex);
		return null;
	}
	public void addCluster(CnSCluster cluster) {
		this.cluster.addElement(cluster);
	}
	public CnSCluster getCluster(int i) {
		if (i != -1)
			return cluster.elementAt(i);
		return null;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public int getClusterIndex(long nodeId) {
		int ret = -1;
		for (CnSCluster cl : cluster)
			if (cl.getCyNode() != null)
				if (cl.getCyNode().getSUID() == nodeId) {
					ret = cluster.indexOf(cl);
					break;
				}
		return ret;
	}
	
	public int getClusterName(long nodeId) {
		int ret = -1;
		for (CnSCluster cl : cluster)
			if (cl.getCyNode() != null)
				if (cl.getCyNode().getSUID() == nodeId) {
					ret = Integer.parseInt(cl.getName());
					break;
				}
		return ret;
	}
	
	public int getClusterIndex(Integer name) {
		int ret = -1;
		for (CnSCluster cl : cluster) {
			if (cl.getName().equals(name.toString())) {
				ret = cluster.indexOf(cl);
				break;
			}
		}
		return ret;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void sortClusters() {
		Collections.sort(cluster);
	}
}
