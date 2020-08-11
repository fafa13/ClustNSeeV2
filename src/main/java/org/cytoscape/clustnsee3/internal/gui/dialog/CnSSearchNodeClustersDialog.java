/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 11 août 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

/**
 * 
 */
public class CnSSearchNodeClustersDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -9003182148463078172L;
	private JTextField searchTextField;
	private CnSButton searchButton, closeButton;
	private JTable clustersTable;
	private Vector<Vector<CnSCluster>> data;
	
	public CnSSearchNodeClustersDialog() {
		super();
		setModal(true);
		data = new Vector<Vector<CnSCluster>>();
		initGraphics();
		initListeners();
	}
	
	private void initGraphics() {
		setTitle("Search node clusters");
		CnSPanel mainPanel = new CnSPanel();
		CnSPanel nodeInformationPanel = new CnSPanel();
		CnSPanel nodeClustersPanel = new CnSPanel();
		nodeInformationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Node information"));
		mainPanel.addComponent(nodeInformationPanel, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.NORTH, CnSPanel.HORIZONTAL, 5, 5, 0, 5, 30, 30);
		nodeClustersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED), "Node clusters"));
		mainPanel.addComponent(nodeClustersPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.NORTH, CnSPanel.BOTH, 5, 5, 0, 5, 30, 30);
		closeButton = new CnSButton("Close");
		mainPanel.addComponent(closeButton, 0, 2, 1, 1, 1.0, 0.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		nodeInformationPanel.addComponent(new JLabel("Node name :"), 0, 0, 1, 1, 0.0, 1.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 15, 10, 5, 0, 0, 0);
		searchTextField = new JTextField(20);
		nodeInformationPanel.addComponent(searchTextField, 1, 0, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 15, 5, 5, 0, 0, 0);
		searchButton = new CnSButton("Search");
		nodeInformationPanel.addComponent(searchButton, 2, 0, 1, 1, 0.0, 1.0, CnSPanel.WEST, CnSPanel.HORIZONTAL, 15, 5, 5, 10, 0, 0);
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Cluster name");
		clustersTable = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(clustersTable);
		scrollPane.setPreferredSize(new Dimension(200, 150));
		nodeClustersPanel.addComponent(scrollPane, 0, 0, 1, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 20, 10, 10, 10, 0, 0);
		
		getContentPane().add(mainPanel);
	}
	
	private void initListeners() {
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!searchTextField.getText().equals("")) {
					CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_NODE_CLUSTERS, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.NODE_NAME, searchTextField.getText());
					@SuppressWarnings("unchecked")
					Vector<CnSCluster> v = (Vector<CnSCluster>)CnSEventManager.handleMessage(ev);
					System.err.println("found " + v.size() + " clusters.");
					data.addElement(v);
					clustersTable.updateUI();
					clustersTable.repaint();
				}
			}
			
		});
		
		searchTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!searchTextField.getText().equals("")) {
					CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_NODE_CLUSTERS, CnSEventManager.PARTITION_MANAGER);
					ev.addParameter(CnSPartitionManager.NODE_NAME, searchTextField.getText());
					@SuppressWarnings("unchecked")
					Vector<CnSCluster> v = (Vector<CnSCluster>)CnSEventManager.handleMessage(ev);
					System.err.println("found " + v.size() + " clusters.");
					data.addElement(v);
					clustersTable.updateUI();
					clustersTable.repaint();
				}
			}
		});
		closeButton.addActionListener(this);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}
}
