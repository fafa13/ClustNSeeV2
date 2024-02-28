/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 20 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.infopanel;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSClusterMulticlassPanel extends CnSPanel {
	private static final long serialVersionUID = -6650107681589813963L;
	private JTable nodesTable;
	private JLabel nbNodesLabel;
	private JScrollPane scrollPane;
	private Vector<String> columnNames;
	private Vector<Vector<String>> data;
	
	public CnSClusterMulticlassPanel() {
		super();
		data = new Vector<Vector<String>>();
		columnNames = new Vector<>();
		columnNames.addElement("Node name");
		initGraphics();
	}
	
	public void initGraphics() {
		addComponent(new JLabel("Multiclassed nodes :"), 0, 0, 1, 1, 1.0, 0.0, EAST, NONE, 5, 5, 5, 5, 0, 0);
		nbNodesLabel = new JLabel();
		addComponent(nbNodesLabel, 1, 0, 1, 1, 1.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		
		nodesTable = new JTable(data, columnNames);
		scrollPane = new JScrollPane(nodesTable);
		addComponent(scrollPane, 0, 1, 2, 1, 1.0, 1.0, CENTER, BOTH, 5, 5, 5, 5, 0, 0);
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	public void init(CnSClusterLink clusterLink) {
		nbNodesLabel.setText(String.valueOf(clusterLink.getNodes().size()));
		
		data.clear();
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER, this.getClass());
		ev.addParameter(CnSPartitionManager.CLUSTER, clusterLink.getSource());
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev, true).getValue();
		
		for (CnSNode n : clusterLink.getNodes()) {
			Vector<String> rv = new Vector<String>();
			rv.addElement(partition.getInputNetwork().getRow(n.getCyNode()).get(CyNetwork.NAME, String.class));
			data.addElement(rv);
		}
		nodesTable.updateUI();
		nodesTable.repaint();
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void clear() {
		nbNodesLabel.setText("");
		data.clear();
	}
}
