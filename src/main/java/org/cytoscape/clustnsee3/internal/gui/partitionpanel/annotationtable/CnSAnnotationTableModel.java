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

import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeNetworkSet;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAnnotationTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -408723168570428529L;
	private static final String[] columnNames = new String[8]; //{"Annotation", "Nodes", "Frequency", "Annotation frequency", "Clusters", "Cluster percent", "Enriched clusters (phyper<5%)", "Enriched clusters (majority law)", "Top 3 clusters"}; 
	private static final Class<?>[] columnClasses = {CnSNodeAnnotation.class, Integer.class, Double.class, Double.class, Integer.class, Double.class, Integer.class, Integer.class, String.class};

	private CnSPartition partition;
	private CyNetwork network;
	private Vector<CnSNodeAnnotation> annotations;
	private CnSCluster selectedCluster;
	
	private HashMap<CnSNodeAnnotation, Integer> alphaEnrichedClusters;
	private HashMap<CnSNodeAnnotation, Integer> majoEnrichedClusters;
	
	public CnSAnnotationTableModel(CyNetwork network) {
		super();
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR);
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev);
		rBundle = CyActivator.getResourcesBundle();
		
		columnNames[0] = rBundle.getString("CnSAnnotationTableModel.annotationTerm");
		columnNames[1] = rBundle.getString("CnSAnnotationTableModel.Nodes");
		columnNames[2] = rBundle.getString("CnSAnnotationTableModel.frequency");
		columnNames[3] = rBundle.getString("CnSAnnotationTableModel.annotationFrequency");
		columnNames[4] = rBundle.getString("CnSAnnotationTableModel.clusters");
		columnNames[5] = rBundle.getString("CnSAnnotationTableModel.clusterPercent");
		columnNames[6] = rBundle.getString("CnSAnnotationTableModel.enrichedClustersPhyperInf5");
		columnNames[7] = rBundle.getString("CnSAnnotationTableModel.enrichedClustersMajorityRule");
		
		refreshModel();
		selectedCluster = null;
		partition = null;
		this.network = network;
		alphaEnrichedClusters = new HashMap<CnSNodeAnnotation, Integer>();
		majoEnrichedClusters = new HashMap<CnSNodeAnnotation, Integer>();
		init();
	}
	
	/**
	 * @param
	 * @return
	 */
	public CnSAnnotationTableModel(CnSPartition partition) {
		this(partition.getInputNetwork());
		this.partition = partition;
	}
	
	public void refreshModel() {
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		annotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
	}
	
	public void init() {
		System.err.println("CnSAnnotationTableModel.init : " + annotations.size());
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ALPHA, CnSEventManager.ANNOTATION_MANAGER);
		double alpha = (Double)CnSEventManager.handleMessage(ev);
		int n;
		alphaEnrichedClusters.clear();
		majoEnrichedClusters.clear();
		for (CnSNodeAnnotation a : annotations) {
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ENRICHED_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER);
			ev.addParameter(CnSNodeAnnotationManager.HG_THRESHOLD, alpha);
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, a);
			n = ((Vector<?>)(CnSEventManager.handleMessage(ev))).size();
			alphaEnrichedClusters.put(a, n);
			ev.addParameter(CnSNodeAnnotationManager.HG_THRESHOLD, -1.0);
			n = ((Vector<?>)(CnSEventManager.handleMessage(ev))).size();
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
		int annotated_node_count;
		int annotated_cluster_count = 0;
		Vector<CnSCluster> v;
		CnSPartition part;
		double alpha;

		//refreshModel();
		switch(column) {
			case 0 :	return annotations.elementAt(row);
			case 1 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER);
					 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
					 	return ((CnSNodeNetworkSet)CnSEventManager.handleMessage(ev)).getNodeNetworks().size();
			case 2 : 	if (network != null) {
							node_count = network.getNodeCount();
							ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER);
							ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
							annotation_count = ((CnSNodeNetworkSet)CnSEventManager.handleMessage(ev)).getNodeNetworks().size();
							return (double)annotation_count / (double)node_count;
					 	}
						else
							return null;
					 	
			case 3 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODES, CnSEventManager.ANNOTATION_MANAGER);
	 		 		 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
	 		 		 	annotation_count = ((CnSNodeNetworkSet)CnSEventManager.handleMessage(ev)).getNodeNetworks().size();
	 		 		 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER);
	 		 		 	annotated_node_count = ((Vector<?>)CnSEventManager.handleMessage(ev)).size();
	 		 		 	if (annotated_node_count != 0)
	 		 		 		return (double)annotation_count / (double)annotated_node_count;
	 		 		 	return 0.0;
	 		 		 	
			case 4 : 	ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER);
					 	ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
					 	v = (Vector<CnSCluster>)(CnSEventManager.handleMessage(ev));
					 	annotated_cluster_count = 0;
						if (v != null) 
					 		for (CnSCluster c : v) {
					 			ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
							 	ev.addParameter(CnSPartitionManager.CLUSTER, c);
							 	part = (CnSPartition)CnSEventManager.handleMessage(ev);
							 	if (part == partition) annotated_cluster_count++;
					 		}
					 	return annotated_cluster_count;
					 	
			case 5 : 	if (partition != null) {
							ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER);
							ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
							v = (Vector<CnSCluster>)(CnSEventManager.handleMessage(ev));
							annotated_cluster_count = 0;
							if (v != null) 
								for (CnSCluster c : v) {
						 			ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
								 	ev.addParameter(CnSPartitionManager.CLUSTER, c);
								 	part = (CnSPartition)CnSEventManager.handleMessage(ev);
								 	if (part == partition) annotated_cluster_count++;
						 		}
							return (int)(annotated_cluster_count * 1000D / partition.getClusters().size()) / 1000D;
						}
						else
							return null;
			
			case 6 :	return alphaEnrichedClusters.get(annotations.elementAt(row));
						
			case 7 :	return majoEnrichedClusters.get(annotations.elementAt(row));
						
			case 8 :	ev = new CnSEvent(CnSNodeAnnotationManager.GET_ALPHA, CnSEventManager.ANNOTATION_MANAGER);
						alpha = (Double)CnSEventManager.handleMessage(ev);
						ev = new CnSEvent(CnSNodeAnnotationManager.GET_TOP3_CLUSTERS, CnSEventManager.ANNOTATION_MANAGER);
						ev.addParameter(CnSNodeAnnotationManager.HG_THRESHOLD, alpha);
						ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(row));
						v = (Vector<CnSCluster>)(CnSEventManager.handleMessage(ev));
						String s = "";
						if (v.size() > 0) {
							Iterator<CnSCluster> it = v.iterator();
							CnSCluster cluster = it.next();
							s = cluster.getName();
							while (it.hasNext()) s = s + " ; " + it.next().getName();
						}
						return s;
		}
		return null;
	}

	public CnSNodeAnnotation getAnnotation(int index) {
		//CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		//Vector<?> annotations = (Vector<?>)CnSEventManager.handleMessage(ev);
		
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
