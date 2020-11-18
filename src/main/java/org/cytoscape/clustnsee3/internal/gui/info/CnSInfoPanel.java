/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date Nov 12, 2018
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.Icon;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSInfoPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -271614706912742588L;
	private static CnSInfoPanel instance;
	public static final String CLUSTER_DETAILS = "Cluster details";
	public static final String EDGE_DETAILS = "Edge details";
	public static final String NODE_DETAILS = "Node details";
	
	public static final int CLUSTER = 1001;
	public static final int PANEL = 1002;
	public static final int EDGE = 1003;
	public static final int CLUSTER_LINK = 1004;
	public static final int NETWORK = 1005;
	public static final int VIEW = 1006;
	public static final int NODE = 1007;
	
	public static final int INIT = 1;
	public static final int SELECT_PANEL = 2;
	public static final int CLEAR = 3;
	
	private CardLayout cardLayout;
	private CnSClusterDetailsPanel clusterDetailsPanel;
	private CnSEdgeDetailsPanel edgeDetailsPanel;
	private CnSNodeDetailsPanel nodeDetailsPanel;
	
	private CnSInfoPanel(String title) {
		super(title);
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		clusterDetailsPanel = new CnSClusterDetailsPanel();
		add(clusterDetailsPanel, CLUSTER_DETAILS);
		edgeDetailsPanel = new CnSEdgeDetailsPanel();
		add(edgeDetailsPanel, EDGE_DETAILS);
		nodeDetailsPanel = new CnSNodeDetailsPanel();
		add(nodeDetailsPanel, NODE_DETAILS);
	}
	public static CnSInfoPanel getInstance() {
		if (instance == null)
			instance = new CnSInfoPanel("Clust&see");
		return instance;
	}
	
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		switch (event.getAction()) {
			case INIT :
				if (((String)event.getParameter(PANEL)).equals(CLUSTER_DETAILS)) {
					CnSCluster cluster = (CnSCluster)event.getParameter(CLUSTER);
					clusterDetailsPanel.init(cluster);
					clusterDetailsPanel.repaint();
				}
				else if (((String)event.getParameter(PANEL)).equals(EDGE_DETAILS)) {
					CyEdge edge = (CyEdge)event.getParameter(EDGE);
					CnSClusterLink clusterLink = (CnSClusterLink)event.getParameter(CLUSTER_LINK);
					CnSView view = (CnSView)event.getParameter(VIEW);
					edgeDetailsPanel.init(clusterLink, edge, view);	
					edgeDetailsPanel.repaint();
				}
				else if (((String)event.getParameter(PANEL)).equals(NODE_DETAILS)) {
					CnSNode node = (CnSNode)event.getParameter(NODE);
					nodeDetailsPanel.init(node);
				}
				break;
			
			case SELECT_PANEL :
				cardLayout.show(this, (String)event.getParameter(PANEL));
				break;
				
			case CLEAR :
				if (((String)event.getParameter(PANEL)).equals(CLUSTER_DETAILS))
					clusterDetailsPanel.clear();
				else if (((String)event.getParameter(PANEL)).equals(EDGE_DETAILS))
					edgeDetailsPanel.clear();
				else if (((String)event.getParameter(PANEL)).equals(NODE_DETAILS))
					nodeDetailsPanel.clear();
				break;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getComponent()
	 */
	@Override
	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getCytoPanelName()
	 */
	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getIcon()
	 */
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
