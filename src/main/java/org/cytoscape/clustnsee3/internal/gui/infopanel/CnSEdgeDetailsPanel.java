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

import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSEdgeDetailsPanel extends CnSPanel {
	private static final long serialVersionUID = -2907729617937489552L;
	private CnSClusterDetailsPanel leftClusterPanel, rightClusterPanel;
	private CnSNodeDetailsPanel leftNodePanel, rightNodePanel;
	private CnSClusterInteractionsPanel interactionsPanel;
	private CnSClusterMulticlassPanel multiclassPanel;
	private CnSNodeClusterInteractionsPanel nodeClusterInteractionsPanel;
	
	private JLabel linkNameLabel;
	private CnSPanel mainPanel;
	private JPanel leftPanel, middlePanel, rightPanel;
	private CardLayout leftLayout, middleLayout, rightLayout;
	private static final String CLUSTER_DETAILS = "Cluster details";
	private static final String NODE_DETAILS = "Node details";
	private static final String CLUSTER_INTERACTION = "Interaction details";
	private static final String CLUSTER_MULTICLASS = "Multiclass details";
	private static final String NO_DETAILS = "No details";
	private static final String NODE_CLUSTER_INTERACTION = "Node to cluster interaction link";
	
	public CnSEdgeDetailsPanel() {
		super();
		initGraphics();
	}
	
	public void initGraphics() {
		leftPanel = new JPanel();
		leftLayout = new CardLayout();
		leftPanel.setLayout(leftLayout);
		
		middlePanel = new JPanel();
		middleLayout = new CardLayout();
		middlePanel.setLayout(middleLayout);
		
		rightPanel = new JPanel();
		rightLayout = new CardLayout();
		rightPanel.setLayout(rightLayout);
		
		leftClusterPanel = new CnSClusterDetailsPanel();
		rightClusterPanel = new CnSClusterDetailsPanel();
		leftNodePanel = new CnSNodeDetailsPanel();
		rightNodePanel = new CnSNodeDetailsPanel();
		interactionsPanel = new CnSClusterInteractionsPanel();
		multiclassPanel = new CnSClusterMulticlassPanel();
		nodeClusterInteractionsPanel = new CnSNodeClusterInteractionsPanel();
		
		leftPanel.add(leftClusterPanel, CLUSTER_DETAILS);
		leftPanel.add(leftNodePanel, NODE_DETAILS);
		rightPanel.add(rightClusterPanel, CLUSTER_DETAILS);
		rightPanel.add(rightNodePanel, NODE_DETAILS);
		middlePanel.add(interactionsPanel, CLUSTER_INTERACTION);
		middlePanel.add(multiclassPanel, CLUSTER_MULTICLASS);
		middlePanel.add(new CnSPanel(), NO_DETAILS);
		middlePanel.add(nodeClusterInteractionsPanel, NODE_CLUSTER_INTERACTION);
		
		linkNameLabel = new JLabel();
		linkNameLabel.setFont(linkNameLabel.getFont().deriveFont(Font.BOLD));
		
		addComponent(linkNameLabel, 0, 0, 1, 1, 1.0, 0.0, CnSPanel.WEST, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		
		mainPanel = new CnSPanel();
		
		mainPanel.addComponent(leftPanel, 0, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		mainPanel.addComponent(middlePanel, 1, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		mainPanel.addComponent(rightPanel, 2, 0, 1, 1, 1.0, 1.0, WEST, BOTH, 5, 5, 5, 5, 0, 0);
		
		addComponent(mainPanel, 0, 1, 1, 1, 1.0, 1.0, CnSPanel.WEST, CnSPanel.BOTH, 5, 5, 5, 5, 0, 0);
		
	}
	public void init(CnSClusterLink clusterLink, CyEdge edge, CnSView view, CnSPartition part) {
		if (clusterLink != null) {
			leftLayout.show(leftPanel, CLUSTER_DETAILS);
			rightLayout.show(rightPanel, CLUSTER_DETAILS);
			linkNameLabel.setText("Cluster #" + clusterLink.getSource().getName() + " -- Cluster #" + clusterLink.getTarget().getName());
			leftClusterPanel.init(clusterLink.getSource());
			rightClusterPanel.init(clusterLink.getTarget());
			if (clusterLink.getInteractionEdge() != null)
				if (clusterLink.getInteractionEdge().getCyEdge() == edge) {
					middleLayout.show(middlePanel, CLUSTER_INTERACTION);
					interactionsPanel.init(clusterLink);
				}
				else {
					middleLayout.show(middlePanel, CLUSTER_MULTICLASS);
					multiclassPanel.init(clusterLink);
				}
			else {
				middleLayout.show(middlePanel, CLUSTER_MULTICLASS);
				multiclassPanel.init(clusterLink);
			}
		}
		else {
			CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_NODE, CnSEventManager.PARTITION_MANAGER);
			ev.addParameter(CnSPartitionManager.CY_NODE, edge.getSource());
			ev.addParameter(CnSPartitionManager.PARTITION, part);
			CnSNode node1 = (CnSNode)CnSEventManager.handleMessage(ev);
			ev.addParameter(CnSPartitionManager.CY_NODE, edge.getTarget());
			CnSNode node2 = (CnSNode)CnSEventManager.handleMessage(ev);
			String name1 = view.getView().getModel().getRow(node1.getCyNode()).get("shared name", String.class);
			String name2 = view.getView().getModel().getRow(node2.getCyNode()).get("shared name", String.class);
			linkNameLabel.setText(name1 + " -- " + name2);
			CnSCluster cnsc1 = null, cnsc2 = null;
			if (node1.getClusters().size() == 0) {
				leftLayout.show(leftPanel, CLUSTER_DETAILS);
				ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER, CnSEventManager.PARTITION_MANAGER);
				ev.addParameter(CnSPartitionManager.CY_NODE, node1.getCyNode());
				cnsc1 = (CnSCluster)CnSEventManager.handleMessage(ev);
				leftClusterPanel.init(cnsc1);
			}
			else {
				leftLayout.show(leftPanel, NODE_DETAILS);
				leftNodePanel.init(node1);
			}
			if (node2.getClusters().size() == 0) {
				rightLayout.show(rightPanel, CLUSTER_DETAILS);
				ev = new CnSEvent(CnSPartitionManager.GET_CLUSTER, CnSEventManager.PARTITION_MANAGER);
				ev.addParameter(CnSPartitionManager.CY_NODE, node2.getCyNode());
				cnsc2 = (CnSCluster)CnSEventManager.handleMessage(ev);
				rightClusterPanel.init(cnsc2);
			}
			else {
				rightLayout.show(rightPanel, NODE_DETAILS);
				rightNodePanel.init(node2);
			}
			if ((node1.getClusters().size() == 0 && node2.getClusters().size() > 0) || 
					(node2.getClusters().size() == 0 && node1.getClusters().size() > 0)) {
				boolean multiclassLink = false;
				if (node1.getNbClusters() == 0 ) { // node1 is a cluster node
					for (CnSCluster cnsc : node2.getClusters())
						if (cnsc.getCyNode() == node1.getCyNode()) {
							multiclassLink = true;
							break;
						}
				}
				if (!multiclassLink)
					if (node2.getNbClusters() == 0 ) {
						for (CnSCluster cnsc : node1.getClusters())
							if (cnsc.getCyNode() == node2.getCyNode()) {
								multiclassLink = true;
								break;
							}
					}
				if (multiclassLink)
					middleLayout.show(middlePanel, NO_DETAILS);
				else {
					if (node1.getNbClusters() == 0)
						nodeClusterInteractionsPanel.init(cnsc1, node2);
					else
						nodeClusterInteractionsPanel.init(cnsc2, node1);
					middleLayout.show(middlePanel, NODE_CLUSTER_INTERACTION);
				}
			}
			else
				middleLayout.show(middlePanel, NO_DETAILS);
		}
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public void clear() {
		linkNameLabel.setText("");
		leftClusterPanel.clear();
		rightClusterPanel.clear();
		interactionsPanel.clear();
		multiclassPanel.clear();
	}
}
