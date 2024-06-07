/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 nov. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.multiclassednodes;

import javax.swing.table.AbstractTableModel;
import java.util.ResourceBundle;

import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSMulticlassedNodesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -7427528685395392344L;
	private CnSPartition partition;
	private static final String[] columnNames = new String[5]; 
	
	/**
	 * @param
	 * @return
	 */
	public CnSMulticlassedNodesTableModel(CnSPartition partition) {
		super();
		this.partition = partition;
		
		CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR, this.getClass());
		ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev, true).getValue();
		rBundle = CyActivator.getResourcesBundle();
		
		columnNames[0] = rBundle.getString("CnSMulticlassedNodesTableModel.nodeID");	
		columnNames[1] = rBundle.getString("CnSMulticlassedNodesTableModel.degree");
		columnNames[2] = rBundle.getString("CnSMulticlassedNodesTableModel.betweeness");
		columnNames[3] = rBundle.getString("CnSMulticlassedNodesTableModel.clusters");
		columnNames[4] = rBundle.getString("CnSMulticlassedNodesTableModel.enrichedAnnotationPairs");
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
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
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (partition != null && columnIndex < 5)
			return columnNames[columnIndex];
		return null;
	}
}
