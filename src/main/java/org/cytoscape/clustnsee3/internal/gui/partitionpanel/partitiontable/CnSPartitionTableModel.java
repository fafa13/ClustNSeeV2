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

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable;

import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

/**
 * 
 */
public class CnSPartitionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3984889084020756303L;
	private static final String[] columnNames = {"Cluster ID", "Nodes", "Links", /*"Multiclassed nodes", "Density",*/ "Annotations", "Enrichment nb.", "Enrichment stat."}; 
	private CnSPartition partition;
	private CnSNodeAnnotation selectedAnnotation;
	
	public CnSPartitionTableModel(CnSPartition partition) {
		super();
		this.partition = partition;
		selectedAnnotation = null;
	}
	
	public void clear() {
		partition = null;
	}
	
	public CnSNodeAnnotation getSelectedAnnotation() {
		return selectedAnnotation;
	}
	
	public void setSelectedAnnotation(CnSNodeAnnotation an) {
		selectedAnnotation = an;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		if (partition != null)
			return columnNames.length;
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		if (partition != null)
			return partition.getClusters().size();
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		CnSEvent ev;
		Integer nb;
		
		CnSCluster cluster = partition.getClusters().elementAt(row);
		
		switch (column) {
			case 0 : return cluster.getID();
			case 1 : return cluster.getNbNodes();
			case 2 : return cluster.getEdges().size();
			/*case 3 : ev = new CnSEvent(CnSPartitionManager.GET_NB_MULTICLASS_NODES, CnSEventManager.PARTITION_MANAGER);
					 ev.addParameter(CnSPartitionManager.PARTITION, partition);
					 ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
					 nb = (Integer)CnSEventManager.handleMessage(ev);
					 return nb;
			case 4 : return ((int)(cluster.getDensity() * 1000)) / 1000D;*/
			case 3 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
			 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			 		 nb = ((Vector<?>)CnSEventManager.handleMessage(ev)).size();
			 		 return nb;
			default : return "NA";
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		super.addTableModelListener(l);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (getRowCount() == 0) return Object.class;
	    return getValueAt(0, columnIndex).getClass();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (partition != null)
			return columnNames[columnIndex];
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
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		super.removeTableModelListener(l);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
	}
	
	public CnSCluster getCluster(int row) {
		return partition.getClusters().elementAt(row);
	}
}
