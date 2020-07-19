/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 14 juil. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;

/**
 * 
 */
public class CnSClusterDetailsPanel extends CnSPanel {
	private static final long serialVersionUID = -8732351304210969069L;
	private JTabbedPane tabbedPane;
	private JLabel testLabel;
	private JTable nodeTable, annotationTable;
	
	public CnSClusterDetailsPanel() {
		super();
		nodeTable = new JTable();
		annotationTable = new JTable();
		initGraphics();
	}
	
	protected void initGraphics() {
		super.initGraphics();
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Node list", new JScrollPane(nodeTable));
		tabbedPane.addTab("Annotation list", new JScrollPane(annotationTable));
		testLabel = new JLabel();
		addComponent(testLabel, 0, 0, 1, 1, 0.0, 1.0, NORTH, VERTICAL, 0, 10, 10, 10, 0, 0);
		addComponent(tabbedPane, 1, 0, 1, 1, 1.0, 1.0, NORTH, BOTH, 0, 10, 10, 10, 0, 0);
	}
	
	public void init(CnSCluster cluster) {
		testLabel.setText(cluster.getName());
		nodeTable.setModel(new CnSNodeListTableModel(cluster));
	}

	public void clear() {
		testLabel.setText("");
		nodeTable.setModel(new CnSNodeListTableModel(null));
	}
}
