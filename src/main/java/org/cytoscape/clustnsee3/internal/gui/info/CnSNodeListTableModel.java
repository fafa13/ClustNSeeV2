/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 mai 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

/**
 * 
 */
public class CnSNodeListTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5044909086728506896L;
	private String colName[] = {"Name", "# clusters", "Degree"};
	private CnSCluster cluster;
	
	public CnSNodeListTableModel(CnSCluster cluster) {
		super();
		this.cluster = cluster;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (cluster != null)
			return cluster.getNodes().size();
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return colName.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (cluster != null)
			if (columnIndex == 0) {
				CyNode cn = cluster.getNodes().get(rowIndex).getCyNode();
				CyRow cr = cluster.getNetwork().getRootNetwork().getRow(cn);
				String name = cr.get("shared name", String.class);
				//Map<String, Object> map = cr.getAllValues();
				//System.out.println("CyNode = " + cn + " ; CyRow = " + cr + " ; name = " + name);
				//for (String key : map.keySet()) {
				//	System.out.println("  " + key + " -> " + map.get(key));
				//}
				return name;
			}
			else if (columnIndex == 1)
				return cluster.getNodes().get(rowIndex).getNbClusters();
			else if (columnIndex == 2)
				return cluster.getNodeDegree(cluster.getNodes().get(rowIndex));
		return null;
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
			return String.class;
		else if (columnIndex == 1)
			return Integer.class;
		else if (columnIndex == 2)
			return Integer.class;
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
}
