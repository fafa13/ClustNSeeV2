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

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.annotation.CnSClusterAnnotation;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTwoIntegers;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSPartitionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3984889084020756303L;
	private static final String[] columnNames = new String[6];
	private CnSPartition partition;
	private CnSNodeAnnotation selectedAnnotation;
	
	public CnSPartitionTableModel(CnSPartition partition) {
		super();
		this.partition = partition;
		selectedAnnotation = null;
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, true).getValue();
		rBundle = CyActivator.getResourcesBundle();
		
		columnNames[0] = rBundle.getString("CnSPartitionTableModel.clusterID");
		columnNames[1] = rBundle.getString("CnSPartitionTableModel.userAnnotations");
		columnNames[2] = rBundle.getString("CnSPartitionTableModel.nodes");
		columnNames[3] = rBundle.getString("CnSPartitionTableModel.edges");
		columnNames[4] = rBundle.getString("CnSPartitionTableModel.annotationTerms");
		columnNames[5] = rBundle.getString("CnSPartitionTableModel.enrichmentTerms");
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
		CnSCluster cluster = null;
		if (partition != null) cluster = partition.getClusters().elementAt(row);
		String o;
		
		if (cluster == null) return null;
		
		switch (column) {
			case 0 : return cluster.getID();
			case 1 : o = "";
					 Iterator<CnSClusterAnnotation> it = cluster.getAnnotations().iterator();
					 if (it.hasNext()) {
						 o = it.next().getAnnotation();
						 while (it.hasNext()) o += " ; " + it.next().getAnnotation();
					 }
					 return o;
			case 2 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_NB_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			 		 Integer n = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
			 		 return new CnSTwoIntegers(n, cluster.getNbNodes());
			case 3 : return cluster.getEdges().size();
			case 4 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			 		 nb = ((Vector<?>)CnSEventManager.handleMessage(ev, false).getValue()).size();
			 		 return nb;
			case 5 : ev = new CnSEvent(CnSPartitionPanel.GET_SELECTED_STAT, CnSEventManager.PARTITION_PANEL, this.getClass());
					 int selectedStat = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
					 if (selectedStat == 0)
						 ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_FILTERED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
					 else
						 ev = new CnSEvent(CnSNodeAnnotationManager.GET_MAJORITY_FILTERED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
					 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
					 return ((Vector<?>)CnSEventManager.handleMessage(ev, false).getValue()).size();
			case -5 : ev = new CnSEvent(CnSPartitionPanel.GET_SELECTED_STAT, CnSEventManager.PARTITION_PANEL, this.getClass());
			 		  selectedStat = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
			 		  if (selectedStat == 0)
			 			  ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_FILTERED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 		  else
			 			  ev = new CnSEvent(CnSNodeAnnotationManager.GET_MAJORITY_FILTERED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 		  ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			 		  return (Vector<CnSAnnotationClusterPValue>)CnSEventManager.handleMessage(ev, false).getValue();
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

	public void fireTableDataChanged() {
		super.fireTableDataChanged();
	}
	
	public CnSCluster getCluster(int row) {
		return partition.getClusters().elementAt(row);
	}
}
