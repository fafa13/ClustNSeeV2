/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 23 juil. 2020
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

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSNodeClusterInteractionsPanel extends CnSPanel {
	private static final long serialVersionUID = -43684097377535689L;
	private JLabel nbInteractionsLabel;
	private JTable nodesTable;
	private JScrollPane scrollPane;
	private Vector<String> columnNames;
	private Vector<Vector<String>> data;
	
	public CnSNodeClusterInteractionsPanel() {
		super();
		columnNames = new Vector<String>();
		columnNames.addElement("Node name");
		data = new Vector<Vector<String>>();
		initGraphics();
	}
	
	public void initGraphics() {
		addComponent(new JLabel("Number of interactions :"), 0, 0, 1, 1, 1.0, 0.0, NORTHWEST, NONE, 5, 5, 0, 0, 0, 0);
		nbInteractionsLabel = new JLabel();
		addComponent(nbInteractionsLabel, 1, 0, 1, 1, 1.0, 0.0, NORTHWEST, HORIZONTAL, 5, 5, 0, 5, 0, 0);
		nodesTable = new JTable(data, columnNames);
		scrollPane = new JScrollPane(nodesTable);
		addComponent(scrollPane, 0, 1, 2, 1, 1.0, 1.0, NORTH, BOTH, 5, 5, 5, 5, 0, 0);
	}
	
	public void init(CnSCluster cluster, CnSNode node) {
		int nb = 0;
		Vector<String> v;
		data.clear();
		for (CnSEdge e : cluster.getExtEdges())
			if ((e.getCyEdge().getSource() == node.getCyNode()) || (e.getCyEdge().getTarget() == node.getCyNode())) {
				v = new Vector<>();
				if (e.getCyEdge().getSource() == node.getCyNode())
					v.addElement(cluster.getNetwork().getRootNetwork().getRow(e.getCyEdge().getTarget()).get("shared name", String.class));
				else
					v.addElement(cluster.getNetwork().getRootNetwork().getRow(e.getCyEdge().getSource()).get("shared name", String.class));
				nb++;
				data.addElement(v);
			}
		nbInteractionsLabel.setText(String.valueOf(nb));
		nodesTable.repaint();
	}
}
