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

package org.cytoscape.clustnsee3.internal.gui.infopanel;

import java.awt.Color;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTableModel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import javax.swing.JLabel;

/**
 * 
 */
public class CnSNodeListTableModel extends CnSTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5044909086728506896L;
	private String colName[] = {"Name", "Degree", "# clusters", "Annotations"};
	private CnSCluster cluster;
	private Vector<CnSNodeAnnotation> clusterAnnotations;
	private CnSEvent ev;
	
	public CnSNodeListTableModel(CnSCluster cluster) {
		super();
		this.cluster = cluster;
		ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODE_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
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
				JLabel lab = new JLabel(name);
				lab.setBackground(Color.WHITE);
				ev.addParameter(CnSNodeAnnotationManager.NODE, cluster.getNodes().get(rowIndex).getCyNode());
				clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, false).getValue();
				if (clusterAnnotations == null) clusterAnnotations = new Vector<CnSNodeAnnotation>();
				CnSEvent ev2 = new CnSEvent(CnSPartitionPanel.GET_SELECTED_ANNOTATION, CnSEventManager.PARTITION_PANEL, this.getClass());
				CnSNodeAnnotation ann = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev2, true).getValue();
				if (ann != null)
					if (clusterAnnotations.contains(ann))
						lab.setBackground(Color.GREEN);
				return lab;
			}
			else if (columnIndex == 2)
				return cluster.getNodes().get(rowIndex).getNbClusters();
			else if (columnIndex == 1)
				return cluster.getNodeDegree(cluster.getNodes().get(rowIndex));
			else if (columnIndex == 3) {
				ev.addParameter(CnSNodeAnnotationManager.NODE, cluster.getNodes().get(rowIndex).getCyNode());
				clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, false).getValue();
				if (clusterAnnotations == null) clusterAnnotations = new Vector<CnSNodeAnnotation>();
				return clusterAnnotations;
			}
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
			return JLabel.class;
		else if (columnIndex == 2)
			return Integer.class;
		else if (columnIndex == 1)
			return Integer.class;
		else if (columnIndex == 3)
			return Vector.class;
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
