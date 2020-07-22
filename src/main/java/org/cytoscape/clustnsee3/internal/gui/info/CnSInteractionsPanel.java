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

package org.cytoscape.clustnsee3.internal.gui.info;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSInteractionsPanel extends CnSPanel {
	private static final long serialVersionUID = -57773180407559986L;
	private JLabel nbInteractionsLabel;
	private JTable interactionsTable;
	private JScrollPane scrollPane;
	private Vector<String> columnNames;
	private Vector<Vector<String>> data;
	
	public CnSInteractionsPanel() {
		super();
		data = new Vector<Vector<String>>();
		columnNames = new Vector<>();
		columnNames.addElement("Node name");
		columnNames.addElement("Node name");
		initGraphics();
	}
	
	protected void initGraphics() {
		addComponent(new JLabel("Number of interactions :"), 0, 0, 1, 1, 1.0, 0.0, EAST, NONE, 5, 5, 5, 5, 0, 0);
		nbInteractionsLabel = new JLabel();
		addComponent(nbInteractionsLabel, 1, 0, 1, 1, 1.0, 0.0, WEST, NONE, 5, 5, 5, 5, 0, 0);
		
		interactionsTable = new JTable(data, columnNames);
		scrollPane = new JScrollPane(interactionsTable);
		addComponent(scrollPane, 0, 1, 2, 1, 1.0, 1.0, CENTER, BOTH, 5, 5, 5, 5, 0, 0);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void init(CnSClusterLink clusterLink) {
		nbInteractionsLabel.setText(String.valueOf(clusterLink.getEdges().size()));
		
		data.clear();
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.CLUSTER, clusterLink.getSource());
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		
		for (CnSEdge ed : clusterLink.getEdges()) {
			Vector<String> rv = new Vector<String>();
			rv.addElement(partition.getInputNetwork().getRow(ed.getCyEdge().getSource()).get(CyNetwork.NAME, String.class));
			rv.addElement(partition.getInputNetwork().getRow(ed.getCyEdge().getTarget()).get(CyNetwork.NAME, String.class));
			data.addElement(rv);
		}
		interactionsTable.updateUI();
		interactionsTable.repaint();
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void clear() {
		nbInteractionsLabel.setText("");
		data.clear();
	}
}
