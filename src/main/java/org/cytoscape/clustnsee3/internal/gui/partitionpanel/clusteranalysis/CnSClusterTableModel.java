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

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSClusterTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -408723168570428529L;
	private static final String[] columnNames = new String[5]; //{"Annotation", "Nodes", "Frequency", "Annotation frequency", "Clusters", "Cluster percent", "Enriched clusters (phyper<5%)", "Enriched clusters (majority law)", "Top 3 clusters"}; 
	private static final Class<?>[] columnClasses = {CnSNodeAnnotation.class, Integer.class, Double.class, Double.class, Double.class};

	private CyNetwork network;
	private Vector<CnSNodeAnnotation> annotations;
	private CnSCluster selectedCluster;
	
	private HashMap<CnSNodeAnnotation, Integer> alphaEnrichedClusters;
	private HashMap<CnSNodeAnnotation, Integer> majoEnrichedClusters;
	
	public CnSClusterTableModel(CyNetwork network) {
		super();
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, true);
		rBundle = CyActivator.getResourcesBundle();
		
		columnNames[0] = rBundle.getString("CnSClusterTableModel.annotationTerm");
		columnNames[1] = rBundle.getString("CnSClusterTableModel.Nodes");
		columnNames[2] = rBundle.getString("CnSClusterTableModel.frequency");
		columnNames[3] = rBundle.getString("CnSClusterTableModel.enrichedClustersPhyper");
		columnNames[4] = rBundle.getString("CnSClusterTableModel.enrichedClustersMajorityPercent");
		
		refreshModel();
		selectedCluster = null;
		this.network = network;
		alphaEnrichedClusters = new HashMap<CnSNodeAnnotation, Integer>();
		majoEnrichedClusters = new HashMap<CnSNodeAnnotation, Integer>();
		init();
	}
	
	/**
	 * @param
	 * @return
	 */
	public CnSClusterTableModel(CnSPartition partition) {
		this(partition.getInputNetwork());
	}
	
	public void refreshModel() {
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		annotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, true);
	}
	
	public void init() {
		CnSEvent ev; // = new CnSEvent(CnSNodeAnnotationManager.GET_ALPHA, CnSEventManager.ANNOTATION_MANAGER);
		//double alpha = (Double)CnSEventManager.handleMessage(ev);
		int n;
		alphaEnrichedClusters.clear();
		majoEnrichedClusters.clear();
		for (CnSNodeAnnotation a : annotations) {
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ENRICHED_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			//ev.addParameter(CnSNodeAnnotationManager.HG_THRESHOLD, current);
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, a);
			n = ((Vector<?>)(CnSEventManager.handleMessage(ev, true))).size();
			alphaEnrichedClusters.put(a, n);
			ev.addParameter(CnSNodeAnnotationManager.HG_THRESHOLD, -1.0);
			n = ((Vector<?>)(CnSEventManager.handleMessage(ev, true))).size();
			majoEnrichedClusters.put(a, n);
		}
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
		
		switch(column) {
			case 0 :	return annotations.elementAt(row);
			case 1 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
					 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
					 	if (selectedCluster != null) 
					 		ev.addParameter(CnSNodeAnnotationManager.CLUSTER, selectedCluster);
					 	return ((Vector<Object>)CnSEventManager.handleMessage(ev, false)).size();
			case 2 :	if (network == null) return 0;
						ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
						ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
						if (selectedCluster != null) 
					 		ev.addParameter(CnSNodeAnnotationManager.CLUSTER, selectedCluster);
						annotation_count = ((Vector<Object>)CnSEventManager.handleMessage(ev, false)).size();
						if (selectedCluster == null)
							node_count = network.getNodeCount();
						else {
							ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER_NODES, CnSEventManager.PARTITION_MANAGER, this.getClass());
							ev.addParameter(CnSPartitionManager.CLUSTER, selectedCluster);
							node_count = ((Vector<CyNode>)CnSEventManager.handleMessage(ev, false)).size();
						}
						return (double)annotation_count / (double)node_count;
					 	
			case 3 :	if (selectedCluster == null) return "N/A";
 						String o = annotations.elementAt(row).getValue();
 						ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATION, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
 						ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, o);
 						CnSNodeAnnotation annot = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev, false);
 
 						if (annot != null) {
 							ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_HYPERGEOMETRIC, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
 							ev.addParameter(CnSNodeAnnotationManager.CLUSTER, selectedCluster);
 							ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annot);
 							CnSAnnotationClusterPValue pv = (CnSAnnotationClusterPValue)CnSEventManager.handleMessage(ev, false);
 							return pv.getBHValue();
 						}
			case 4 :	if (selectedCluster == null) return "N/A";
			 			o = annotations.elementAt(row).getValue();
			 			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATION, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, o);
			 			annot = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev, false);
			 
			 			if (annot != null) {
			 				ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, selectedCluster);
			 				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annot);
			 				annotation_count = ((Vector<Object>)CnSEventManager.handleMessage(ev, false)).size();
			 				ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, selectedCluster);
			 				node_count = (Integer)CnSEventManager.handleMessage(ev, false);
			 				return (double)annotation_count / (double)node_count;
			 			}
		}
		return null;
	}

	public CnSNodeAnnotation getAnnotation(int index) {
		if (annotations != null)
			return (CnSNodeAnnotation)annotations.elementAt(index);
		return null;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public int getIndex(CnSNodeAnnotation annotation) {
		return annotations.indexOf(annotation);
	}
	
	public void fireTableDataChanged() {
		super.fireTableDataChanged();
	}
}
