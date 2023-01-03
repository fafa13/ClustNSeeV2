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
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSPartitionTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3984889084020756303L;
	private static final String[] columnNames = new String[7]; // = {"Cluster ID", "Nodes", "Links", "Annotations", "Enrichment nb.", "Enrichment stat."}; 
	private CnSPartition partition;
	private CnSNodeAnnotation selectedAnnotation;
	
	public CnSPartitionTableModel(CnSPartition partition) {
		super();
		this.partition = partition;
		selectedAnnotation = null;
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR);
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev);
		rBundle = CyActivator.getResourcesBundle();
		System.err.println("BUNDLE = " + rBundle);
		
		
		columnNames[0] = rBundle.getString("CnSPartitionTableModel.clusterID");
		columnNames[1] = rBundle.getString("CnSPartitionTableModel.userAnnotations");
		columnNames[2] = rBundle.getString("CnSPartitionTableModel.nodes");
		columnNames[3] = rBundle.getString("CnSPartitionTableModel.edges");
		columnNames[4] = rBundle.getString("CnSPartitionTableModel.annotationTerms");
		columnNames[5] = rBundle.getString("CnSPartitionTableModel.enrichmentTerms");
		columnNames[6] = rBundle.getString("CnSPartitionTableModel.enrichmentStat");
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
		CnSNodeAnnotation annot;
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
			case 2 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
			 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			 		 Integer n = (Integer)CnSEventManager.handleMessage(ev);
			 		 return new CnSTwoIntegers(cluster.getNbNodes(), n);
			case 3 : return cluster.getEdges().size();
			/*case 3 : ev = new CnSEvent(CnSPartitionManager.GET_NB_MULTICLASS_NODES, CnSEventManager.PARTITION_MANAGER);
					 ev.addParameter(CnSPartitionManager.PARTITION, partition);
					 ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
					 nb = (Integer)CnSEventManager.handleMessage(ev);
					 return nb;
			case 4 : return ((int)(cluster.getDensity() * 1000)) / 1000D;*/
			case 4 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
			 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			 		 nb = ((Vector<?>)CnSEventManager.handleMessage(ev)).size();
			 		 return nb;
			case 5 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_FILTERED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
					 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
					 return ((Vector<CnSAnnotationClusterPValue>)CnSEventManager.handleMessage(ev)).size();
			case 6 : ev = new CnSEvent(CnSPartitionPanel.GET_SEARCHED_ANNOTATION, CnSEventManager.PARTITION_PANEL);
					 o = (String)CnSEventManager.handleMessage(ev);
					 ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATION, CnSEventManager.ANNOTATION_MANAGER);
					 ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, o);
					 annot = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev);
					 
					 if (annot != null) {
						 ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
						 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
						 ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annot);
						 Vector<CyNode> vcn = (Vector<CyNode>)CnSEventManager.handleMessage(ev);
						 ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_HYPERGEOMETRIC, CnSEventManager.ANNOTATION_MANAGER);
						 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
						 ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annot);
						 CnSAnnotationClusterPValue pv = (CnSAnnotationClusterPValue)CnSEventManager.handleMessage(ev);
						 CnSEnrichmentStatValue v = new CnSEnrichmentStatValue(vcn.size(), pv.getBHValue(), (double)vcn.size() / (double)cluster.getNbNodes());
						 //System.err.println(v.toString());
						 return v;
					 }
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
		//if (columnIndex == 5) return CnSEnrichmentStatValue.class;
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
