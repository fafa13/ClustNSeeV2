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

import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

/**
 * 
 */
public class CnSClusterDetailsPanel extends CnSPanel {
	private static final long serialVersionUID = -8732351304210969069L;
	private JTabbedPane tabbedPane;
	private CnSPanel clusterinfoPanel;
	private JTable nodeTable, annotationTable;
	private Vector<Vector<String>> data;
	private JLabel clusterNameLabel, nodesInClusterLabel, monoClusteredNodesLabel, multiClusteredNodesLabel, intraClusterEdgesLabel, extraClusterEdgesLabel;
	
	public CnSClusterDetailsPanel() {
		super();
		nodeTable = new JTable();
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement("Annotation");
		data = new Vector<Vector<String>>();
		annotationTable = new JTable(data, columnNames);
		initGraphics();
	}
	
	protected void initGraphics() {
		super.initGraphics();
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Node list", new JScrollPane(nodeTable));
		JScrollPane scrollPane = new JScrollPane(annotationTable);
		scrollPane.setPreferredSize(new Dimension(300, 150));
		tabbedPane.addTab("Annotation list", scrollPane);
		clusterinfoPanel = new CnSPanel();
		clusterinfoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		clusterNameLabel = new JLabel();
		clusterNameLabel.setFont(clusterNameLabel.getFont().deriveFont(clusterNameLabel.getFont().getStyle() | Font.BOLD));
		clusterinfoPanel.addComponent(clusterNameLabel, 0, 0, 2, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Nodes in cluster :"), 0, 1, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		nodesInClusterLabel = new JLabel();
		clusterinfoPanel.addComponent(nodesInClusterLabel, 1, 1, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Mono-clustered nodes :"), 0, 2, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		monoClusteredNodesLabel = new JLabel();
		clusterinfoPanel.addComponent(monoClusteredNodesLabel, 1, 2, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Multi-clustered nodes :"), 0, 3, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		multiClusteredNodesLabel = new JLabel();
		clusterinfoPanel.addComponent(multiClusteredNodesLabel, 1, 3, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Intra-cluster edges :"), 0, 4, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 0, 0, 0, 0);
		intraClusterEdgesLabel = new JLabel();
		clusterinfoPanel.addComponent(intraClusterEdgesLabel, 1, 4, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 0, 5, 0, 0);
		
		clusterinfoPanel.addComponent(new JLabel("Extra-cluster edges :"), 0, 5, 1, 1, 1.0, 1.0, CnSPanel.EAST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		extraClusterEdgesLabel = new JLabel();
		clusterinfoPanel.addComponent(extraClusterEdgesLabel, 1, 5, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		addComponent(clusterinfoPanel, 0, 0, 1, 1, 0.0, 0.0, CENTER, NONE, 0, 10, 10, 10, 0, 0);
		addComponent(tabbedPane, 1, 0, 1, 1, 1.0, 1.0, CENTER, BOTH, 0, 10, 10, 10, 0, 0);
	}
	
	public void init(CnSCluster cluster) {
		clusterNameLabel.setText(cluster.getName());
		
		nodesInClusterLabel.setText(String.valueOf(cluster.getNbNodes()));
		
		CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		
		ev = new CnSEvent(CnSPartitionManager.GET_NB_MULTICLASS_NODES, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.PARTITION, partition);
		ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
		Integer nb = (Integer)CnSEventManager.handleMessage(ev);
		
		multiClusteredNodesLabel.setText(nb.toString());
		monoClusteredNodesLabel.setText(String.valueOf(cluster.getNbNodes() - nb.intValue()));
		
		intraClusterEdgesLabel.setText(String.valueOf(cluster.getEdges().size()));
		extraClusterEdgesLabel.setText(String.valueOf(cluster.getExtEdges().size()));
		
		nodeTable.setModel(new CnSNodeListTableModel(cluster));
		data.clear();
		Vector<String> v;
		for (int i = 0 ; i < cluster.getAnnotations().size(); i++) {
			v = new Vector<String>();
			v.addElement(cluster.getAnnotations().get(i).getAnnotation());
			data.addElement(v);
		}
		annotationTable.updateUI();
		annotationTable.repaint();
	}

	public void clear() {
		nodesInClusterLabel.setText("");
		multiClusteredNodesLabel.setText("");
		monoClusteredNodesLabel.setText("");
		intraClusterEdgesLabel.setText("");
		extraClusterEdgesLabel.setText("");
		nodeTable.setModel(new CnSNodeListTableModel(null));
	}
}
