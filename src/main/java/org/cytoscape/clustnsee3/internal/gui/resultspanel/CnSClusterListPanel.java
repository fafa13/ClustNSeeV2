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

package org.cytoscape.clustnsee3.internal.gui.resultspanel;

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
import org.cytoscape.clustnsee3.internal.gui.infopanel.CnSInfoPanel;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.network.CnSNetworkManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
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
		model = new CnSResultsTableModel();
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
						CnSCluster cluster;
						if (table.getSelectionModel().isSelectedIndex(table.getSelectionModel().getMaxSelectionIndex()))
							cluster = (CnSCluster)model.getValueAt(table.getSelectionModel().getMaxSelectionIndex(), 1);
						else
							cluster = (CnSCluster)model.getValueAt(table.getSelectionModel().getMinSelectionIndex(), 1);
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
						
						ev = new CnSEvent(CnSPartitionPanel.SELECT_CLUSTER, CnSEventManager.PARTITION_PANEL);
						ev.addParameter(CnSPartitionPanel.CLUSTER, cluster);
						CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
						ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
						CnSPartition p = (CnSPartition)CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSPartitionManager.GET_PARTITION_NETWORK, CnSEventManager.PARTITION_MANAGER);
						ev.addParameter(CnSPartitionManager.PARTITION, p);
						CnSNetwork nw = (CnSNetwork)CnSEventManager.handleMessage(ev);
						
						ev = new CnSEvent(CnSNetworkManager.SELECT_CLUSTER, CnSEventManager.NETWORK_MANAGER);
						ev.addParameter(CnSNetworkManager.CLUSTER, cluster);
						ev.addParameter(CnSNetworkManager.NETWORK, nw);
						CnSEventManager.handleMessage(ev);
					}
					else {
						CnSEvent ev = new CnSEvent(CnSInfoPanel.CLEAR, CnSEventManager.INFO_PANEL);
						ev.addParameter(CnSInfoPanel.PANEL, CnSInfoPanel.CLUSTER_DETAILS);
						CnSEventManager.handleMessage(ev);
						ev = new CnSEvent(CnSViewManager.SELECT_CLUSTER, CnSEventManager.VIEW_MANAGER);
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
	public int selectCluster(long nodeId) {
		int ret = -1;
		if (nodeId == -1) {
			table.clearSelection();
		}
		else {
			int clusterIndex = model.getClusterIndex(nodeId);
			if (clusterIndex >= 0 && clusterIndex < table.getModel().getRowCount()) {
				table.setRowSelectionInterval(clusterIndex, clusterIndex);
				table.scrollRectToVisible(table.getCellRect(clusterIndex, 0, true));
				table.repaint();
				ret = clusterIndex;
			}
		}
		return ret;
	}
	public int getClusterIndex(long suid) {
		return model.getClusterIndex(suid);
	}
	
	public void selectCluster(Integer name) {
		int clusterIndex = model.getClusterIndex(name);
		if (clusterIndex != -1) {
			table.setRowSelectionInterval(clusterIndex, clusterIndex);
			table.scrollRectToVisible(table.getCellRect(clusterIndex, 0, true));
			table.repaint();
		}
		else {
			table.clearSelection();
			table.scrollRectToVisible(table.getCellRect(0, 0, true));
			table.repaint();
		}
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void sort() {
		model.sortClusters();
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public Object getClusterName(long cluster_suid) {
		return model.getClusterName(cluster_suid);
	}
}
