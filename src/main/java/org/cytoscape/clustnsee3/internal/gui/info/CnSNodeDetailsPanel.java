/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 22 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info;

import java.awt.Font;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNodeDetailsPanel extends CnSPanel {
	private static final long serialVersionUID = -7395889517315642188L;
	private JTable clusterListTable, propertiesTable;
	private JLabel nodeInteractionsLabel, nodeNameLabel;
	private Vector<Vector<String>> dataClusterListTable, dataPropertiesTable;
	
	public CnSNodeDetailsPanel() {
		super();
		nodeInteractionsLabel = new JLabel();
		nodeNameLabel = new JLabel();
		nodeNameLabel.setFont(nodeNameLabel.getFont().deriveFont(Font.BOLD));
		clusterListTable = new JTable();
		Vector<String> columnNamesClusterListTable = new Vector<String>();
		columnNamesClusterListTable.addElement("Node cluster list");
		dataClusterListTable = new Vector<Vector<String>>();
		clusterListTable = new JTable(dataClusterListTable, columnNamesClusterListTable);
		
		propertiesTable = new JTable();
		Vector<String> columnNamesPropertiesTable = new Vector<String>();
		columnNamesPropertiesTable.addElement("Property name");
		columnNamesPropertiesTable.addElement("Value");
		dataPropertiesTable = new Vector<Vector<String>>();
		propertiesTable = new JTable(dataPropertiesTable, columnNamesPropertiesTable);
		
		initGraphics();
	}
	
	protected void initGraphics() {
		super.initGraphics();
		CnSPanel leftPanel = new CnSPanel();
		//leftPanel.addComponent(nodeNameLabel, 0, 0, 2, 1, 1.0, 0.0, NORTHWEST, NONE, 0, 5, 0, 0, 0, 0);
		leftPanel.addComponent(new JLabel("Node interactions :"), 0, 1, 1, 1, 0.0, 0.0, NORTHWEST, NONE, 0, 0, 0, 0, 0, 0);
		leftPanel.addComponent(nodeInteractionsLabel, 1, 1, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 0, 5, 0, 0, 0, 0);
		leftPanel.addComponent(new JScrollPane(clusterListTable), 0, 2, 2, 1, 1.0, 1.0, NORTHWEST, BOTH, 5, 0, 0, 0, 0, 0);
		addComponent(leftPanel, 0, 0, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, 5, 5, 5, 0, 0, 0);
		
		CnSPanel rightPanel = new CnSPanel();
		rightPanel.addComponent(new JScrollPane(propertiesTable), 0, 0, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, 5, 0, 0, 0, 0, 0);
		addComponent(rightPanel, 1, 0, 1, 1, 1.0, 1.0, NORTHWEST, BOTH, 5, 5, 5, 5, 0, 0);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void init(CnSNode node) {
		Vector<CyNode> links = new Vector<CyNode>(); 
		for (CnSCluster cl : node.getClusters()) {
			for (CnSEdge ed : cl.getEdges()) 
				if (ed.getCyEdge().getSource() == node.getCyNode()) {
					if (!links.contains(ed.getCyEdge().getTarget()))
						links.addElement(ed.getCyEdge().getTarget());
				}
				else if (ed.getCyEdge().getTarget() == node.getCyNode()) {
					if (!links.contains(ed.getCyEdge().getSource()))
						links.addElement(ed.getCyEdge().getSource());
				}
			
			for (CnSEdge ed : cl.getExtEdges()) 
				if (ed.getCyEdge().getSource() == node.getCyNode()) {
					if (!links.contains(ed.getCyEdge().getTarget()))
						links.addElement(ed.getCyEdge().getTarget());
				}
				else if (ed.getCyEdge().getTarget() == node.getCyNode()) {
					if (!links.contains(ed.getCyEdge().getSource()))
						links.addElement(ed.getCyEdge().getSource());
				}
		}
		nodeInteractionsLabel.setText(String.valueOf(links.size()));
		nodeNameLabel.setText(node.getClusters().firstElement().getNetwork().getRootNetwork().getRow(node.getCyNode()).get("shared name", String.class));
		dataClusterListTable.clear();
		for (CnSCluster cl : node.getClusters()) {
			Vector<String> v = new Vector<>();
			v.addElement(cl.getName());
			dataClusterListTable.addElement(v);
		}
		//clusterListTable.updateUI();
		clusterListTable.repaint();
	}
}
