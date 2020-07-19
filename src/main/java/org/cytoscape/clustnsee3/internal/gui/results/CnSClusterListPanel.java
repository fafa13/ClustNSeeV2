/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 avr. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.results;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.info.CnSInfoPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;

/**
 * 
 */
public class CnSClusterListPanel extends CnSPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table = null;
	private CnSResultsTableModel model = null;
	private JScrollPane scrollPane;
	
	public void init (Vector<CnSCluster> clusters) {
		model = new CnSResultsTableModel(clusters.size());
		Iterator<CnSCluster> itClusters = clusters.iterator();
		while (itClusters.hasNext()) model.addCluster(itClusters.next());

		table = new JTable(model);
		table.setRowHeight(116);
		table.setBackground(Color.white);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Object.class, new CnSResultsTableRenderer());
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting())
					if (table.getSelectionModel().getMaxSelectionIndex() != -1) {
						CnSCluster cluster = (CnSCluster)model.getValueAt(table.getSelectionModel().getMaxSelectionIndex(), 1);
						CnSEvent ev = new CnSEvent(CnSInfoPanel.INIT, CnSEventManager.INFO_PANEL);
						ev.addParameter(CnSInfoPanel.CLUSTER, cluster);
						ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.CLUSTER_DETAILS);
						CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSInfoPanel.SELECT_PANEL, CnSEventManager.INFO_PANEL);
						ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.CLUSTER_DETAILS);
						CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSViewManager.SELECT_CLUSTER, CnSEventManager.VIEW_MANAGER);
						ev.addParameter(CnSViewManager.CLUSTER, cluster);
						CnSEventManager.handleMessage(ev);
					}
					else {
						CnSEvent ev = new CnSEvent(CnSInfoPanel.CLEAR, CnSEventManager.INFO_PANEL);
						CnSEventManager.handleMessage(ev);
					}
			}
		});
		scrollPane = new JScrollPane(table);
		addComponent(scrollPane, 0, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 10, 10, 10, 10, 0, 0);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSCluster getSelectedCluster() {
		return model.getCluster(table.getSelectedRow());
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void selectCluster(long nodeId) {
		if (nodeId == -1) {
			table.clearSelection();
		}
		else {
			int clusterIndex = model.getClusterIndex(nodeId);
			if (clusterIndex >= 0 && clusterIndex < table.getModel().getRowCount()) table.setRowSelectionInterval(clusterIndex, clusterIndex);
			scrollPane.scrollRectToVisible(table.getCellRect(clusterIndex, 0, true));
		}
	}
}
