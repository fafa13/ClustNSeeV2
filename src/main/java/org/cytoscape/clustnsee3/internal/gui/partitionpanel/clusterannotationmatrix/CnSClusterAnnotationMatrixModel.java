/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 16 mai 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusterannotationmatrix;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTableModel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;

/**
 * 
 */
public class CnSClusterAnnotationMatrixModel extends CnSTableModel {
	private static final long serialVersionUID = -3598575148601265039L;
	private Vector<CnSNodeAnnotation> annotations;
	private CnSPartition partition;
	private int stat;
	
	public CnSClusterAnnotationMatrixModel() {
		super();
		stat = 0;
		init(null);
	}
	public void init(CnSPartition partition) {
		this.partition = partition;
		if (partition != null) {
			CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_NETWORK_MAPPED_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.NETWORK, partition.getInputNetwork());
			annotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, true).getValue();
		}
		else
			annotations = new Vector<CnSNodeAnnotation>();
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return annotations.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return partition.getClusters().size() + 1;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CnSEvent ev;
		int annotation_count, node_count;
		
		if (columnIndex == 0)
			return annotations.get(rowIndex).getValue();
		else if (stat == 0) {
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_BH_HYPERGEOMETRIC, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, partition.getCluster(columnIndex));
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(rowIndex));
			CnSAnnotationClusterPValue pv = (CnSAnnotationClusterPValue)CnSEventManager.handleMessage(ev, false).getValue();
			return pv.getBHValue();
		} 
		else {
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, partition.getCluster(columnIndex));
			ev.addParameter(CnSNodeAnnotationManager.ANNOTATION, annotations.elementAt(rowIndex));
			annotation_count = ((Vector<?>)CnSEventManager.handleMessage(ev, false).getValue()).size();
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_NB_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, partition.getCluster(columnIndex));
			node_count = (Integer)CnSEventManager.handleMessage(ev, false).getValue();
			return (double)annotation_count / (double)node_count;
		}
	}
	public String getColumnName(int column) {
		if (column == 0)
			return "Annotation term";
		return String.valueOf(partition.getClusters().get(column - 1).getName());
	}
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return String.class;
		return Double.class;
	}
	
	public void setStat(int selectedIndex) {
		stat = selectedIndex;
	}
}
