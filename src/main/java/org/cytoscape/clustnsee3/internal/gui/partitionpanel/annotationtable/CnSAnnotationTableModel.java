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

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationtable;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeNetworkSet;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSAnnotationTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -408723168570428529L;
	private static final String[] columnNames = {"Annotation", "Nodes", "Frequency", "Annotation frequency", "Clusters", "Cluster percent", "Enriched clusters (phyper<5%)", "Enriched clusters (majority law)", "Top 3 clusters"}; 
	private static final Class<?>[] columnClasses = {String.class, Integer.class, Double.class, Double.class, Integer.class, Double.class, Double.class, Double.class, String.class};

	private CnSPartition partition;
	private Vector<CnSNodeAnnotation> annotations;
	private CnSCluster selectedCluster;
	
	/**
	 * @param
	 * @return
	 */
	public CnSAnnotationTableModel(Vector<CnSNodeAnnotation> annotations, CnSPartition partition) {
		super();
		this.annotations = annotations;
		selectedCluster = null;
		this.partition = partition;
	}
	
	public CnSAnnotationTableModel(Vector<CnSNodeAnnotation> annotations) {
		super();
		this.annotations = annotations;
		selectedCluster = null;
		partition = null;
	}
	public void setSelectedCluster(CnSCluster cluster) {
		selectedCluster = cluster;
	}
	
	public CnSCluster getSelectedCluster() {
		return selectedCluster;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
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
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		Vector<?> annotations = (Vector<?>)CnSEventManager.handleMessage(ev);
		return annotations.size();
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
	public Object getValueAt(int row, int column) {
		CnSEvent ev;
		int node_count;
		int annotation_count;
		int annotated_node_count;
		int annotated_cluster_count = 0;
		Vector<?> v;
				
		switch(column) {
			case 0 :	return annotations.elementAt(row).getValue();
			case 1 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER);
					 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
					 	return ((CnSNodeNetworkSet)CnSEventManager.handleMessage(ev)).getNodeNetworks().size();
			case 2 : 	if (partition != null) {
							node_count = partition.getInputNetwork().getNodeCount();
							ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER);
							ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
							annotation_count = ((CnSNodeNetworkSet)CnSEventManager.handleMessage(ev)).getNodeNetworks().size();
							return (int)(annotation_count * 1000D / node_count) / 1000D;
					 	}
						else 
							return null;
					 	
			case 3 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER);
	 		 		 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
	 		 		 	annotation_count = ((CnSNodeNetworkSet)CnSEventManager.handleMessage(ev)).getNodeNetworks().size();
	 		 		 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
	 		 		 	annotated_node_count = ((Vector<?>)CnSEventManager.handleMessage(ev)).size();
	 		 		 	return Double.valueOf((int)(annotation_count * 1000D / annotated_node_count) / 1000D);
	 		 		 	
			case 4 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER);
					 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
					 	v = (Vector<?>)(CnSEventManager.handleMessage(ev));
					 	if (v != null) annotated_cluster_count = v.size();
					 	return annotated_cluster_count;
					 	
			case 5 : 	if (partition != null) {
							ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER);
							ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
							v = (Vector<?>)(CnSEventManager.handleMessage(ev));
							if (v != null) annotated_cluster_count = v.size();
							return (int)(annotated_cluster_count * 1000D / partition.getClusters().size()) / 1000D;
						}
						else 
							return null;
		}
		return null;
	}

	public CnSNodeAnnotation getAnnotation(int index) {
		if (annotations != null)
			return annotations.elementAt(index);
		return null;
	}
}
