/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 sept. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationanalysis;

import javax.swing.table.AbstractTableModel;

import java.util.ResourceBundle;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTableModel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;

/**
 * 
 */
public class CnSAnnotationTableModel extends CnSTableModel {
	private static final long serialVersionUID = 142374013826060311L;
	private static final String[] columnNames = new String[5]; 
	private static final Class<?>[] columnClass = {Integer.class, Integer.class, Double.class, Double.class, Double.class};
	private CnSPartition partition;
	private CnSNodeAnnotation selectedAnnotation;
	
	public CnSAnnotationTableModel(CnSPartition partition) {
		super();
		this.partition = partition;
		selectedAnnotation = null;
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, true).getValue();
		rBundle = CyActivator.getResourcesBundle();
		
		columnNames[0] = rBundle.getString("CnSAnnotationTableModel.clusterID");
		columnNames[1] = rBundle.getString("CnSAnnotationTableModel.annotatedNodes");
		columnNames[2] = rBundle.getString("CnSAnnotationTableModel.annotatedNodesPercent");
		columnNames[3] = rBundle.getString("CnSAnnotationTableModel.selectedAnnotationPhyper");
		columnNames[4] = rBundle.getString("CnSAnnotationTableModel.selectedAnnotationMajorityPercent");
	}
	
	public void setSelectedAnnotation(CnSNodeAnnotation an) {
		selectedAnnotation = an;
	}
	
//	
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
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		if (partition != null)
			return columnNames.length;
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		//if (partition != null)
			return columnNames[columnIndex];
		//return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int column) {
		CnSEvent ev;
		CnSCluster cluster = null;
		Integer n;
		if (partition != null) cluster = partition.getClusters().elementAt(row);
		
		if (cluster == null) return null;
		
		switch (column) {
			case 0 : return cluster.getID();
			case 1 : if (selectedAnnotation != null) {
			 			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, selectedAnnotation);
			 			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
				 		n = ((Vector<?>)CnSEventManager.handleMessage(ev, false).getValue()).size();
			 		 }
			 		 else {
			 			ev = new CnSEvent(CnSNodeAnnotationManager.GET_NB_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			 			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
				 		n = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
			 		 }
			 		 return n;
			case 2 : ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
	 		 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
	 		 		 if (selectedAnnotation != null) {
	 		 			 ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		 		 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
		 		 		 ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, selectedAnnotation);
	 		 			 n = ((Vector<?>)CnSEventManager.handleMessage(ev, false).getValue()).size();
	 		 		 }
	 		 		 else {
	 		 			 ev = new CnSEvent(CnSNodeAnnotationManager.GET_NB_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		 		 		 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
		 		 		 n = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
	 		 		 }
	 		 		 return n.doubleValue() / (double)cluster.getNbNodes();
			case 3 : if (selectedAnnotation != null) {
						 ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_HYPERGEOMETRIC, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
						 ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
						 ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, selectedAnnotation);
						 CnSAnnotationClusterPValue pv = (CnSAnnotationClusterPValue)CnSEventManager.handleMessage(ev, false).getValue();
						 return pv.getBHValue();
					 }
					 return "N/A";
			case 4 : if (selectedAnnotation == null) return "N/A";
	 				 if (selectedAnnotation != null) {
	 					ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
	 	 				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
	 	 				ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, selectedAnnotation);
	 	 				int annotation_count = ((Vector<?>)CnSEventManager.handleMessage(ev, false).getValue()).size();
	 	 				ev = new CnSEvent(CnSNodeAnnotationManager.GET_NB_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
	 	 				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
	 	 				int node_count = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
	 	 				return (double)annotation_count / (double)node_count;
	 				 }
			default : return "NA";
		}
	}
	public CnSNodeAnnotation getSelectedAnnotation() {
		return selectedAnnotation;
	}
	public CnSCluster getCluster(int row) {
		return partition.getClusters().elementAt(row);
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		//if (getRowCount() == 0) return Object.class;
		return columnClass[columnIndex];
	}
}
