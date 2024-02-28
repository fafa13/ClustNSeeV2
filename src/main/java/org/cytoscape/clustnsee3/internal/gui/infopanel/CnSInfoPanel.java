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

package org.cytoscape.clustnsee3.internal.gui.infopanel;

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
import org.cytoscape.clustnsee3.internal.event.CnSEventResult;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.utils.CnSLogger;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSInfoPanel extends CnSPanel implements CytoPanelComponent, CnSEventListener {
	private static final long serialVersionUID = -271614706912742588L;
	
	public static final int INIT = 1;
	public static final int SELECT_PANEL = 2;
	public static final int CLEAR = 3;
	public final static int TAG_NODES = 4;
	
	
	public static final int CLUSTER = 1001;
	public static final int PANEL = 1002;
	public static final int EDGE = 1003;
	public static final int CLUSTER_LINK = 1004;
	public static final int NETWORK = 1005;
	public static final int VIEW = 1006;
	public static final int NODE = 1007;
	public static final int PARTITION = 1008;
	public static final int ANNOTATION = 1009;
	
	private static CnSInfoPanel instance;
	public static final String CLUSTER_DETAILS = "Cluster details";
	public static final String EDGE_DETAILS = "Edge details";
	public static final String NODE_DETAILS = "Node details";
	
	private CnSClusterDetailsPanel clusterDetailsPanel;
	private CnSEdgeDetailsPanel edgeDetailsPanel;
	private CnSNodeDetailsPanel nodeDetailsPanel;
	private String title;
	
	private CnSInfoPanel(String title) {
		super();
		this.title = title;
		this.setLayout(new CardLayout());
		clusterDetailsPanel = new CnSClusterDetailsPanel();
		add(clusterDetailsPanel, CLUSTER_DETAILS);
		edgeDetailsPanel = new CnSEdgeDetailsPanel();
		add(edgeDetailsPanel, EDGE_DETAILS);
		nodeDetailsPanel = new CnSNodeDetailsPanel();
		add(nodeDetailsPanel, NODE_DETAILS);
	}
	
	public String getActionName(int k) {
		switch(k) {
			case INIT : return "INIT";
			case SELECT_PANEL : return "SELECT_PANEL";
			case CLEAR : return "CLEAR";
			case TAG_NODES : return "TAG_NODES";
			default : return "UNDEFINED_ACTION"; 
		}
	}
	
	public String getParameterName(int k) {
		switch(k) {
			case CLUSTER : return "CLUSTER";
			case PANEL : return "PANEL";
			case EDGE : return "EDGE";
			case CLUSTER_LINK : return "CLUSTER_LINK";
			case NETWORK : return "NETWORK";
			case VIEW : return "VIEW";
			case NODE : return "NODE";
			case PARTITION : return "PARTITION";
			case ANNOTATION : return "ANNOTATION";
			default : return "UNDEFINED_PARAMETER"; 
		}
	}
			 
	public static CnSInfoPanel getInstance() {
		if (instance == null)
			instance = new CnSInfoPanel("C&S Details");
		return instance;
	}
	
	@Override
	public CnSEventResult<?> cnsEventOccured(CnSEvent event, boolean log) {
		
		if (log) CnSLogger.LogCnSEvent(event, this);
		
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
					CnSPartition p = (CnSPartition)event.getParameter(PARTITION);
					edgeDetailsPanel.init(clusterLink, edge, view, p);	
					edgeDetailsPanel.repaint();
				}
				else if (((String)event.getParameter(PANEL)).equals(NODE_DETAILS)) {
					CnSNode node = (CnSNode)event.getParameter(NODE);
					nodeDetailsPanel.init(node);
				}
				break;
			
			case SELECT_PANEL :
				((CardLayout)getLayout()).show(this, (String)event.getParameter(PANEL));
				doLayout();
				break;
				
			case CLEAR :
				if (((String)event.getParameter(PANEL)).equals(CLUSTER_DETAILS))
					clusterDetailsPanel.clear();
				else if (((String)event.getParameter(PANEL)).equals(EDGE_DETAILS))
					edgeDetailsPanel.clear();
				else if (((String)event.getParameter(PANEL)).equals(NODE_DETAILS))
					nodeDetailsPanel.clear();
				break;
				
			case TAG_NODES :
				CnSNodeAnnotation annotation = (CnSNodeAnnotation)event.getParameter(ANNOTATION);
				clusterDetailsPanel.tagNodes(annotation);
				break;
		}
		return new CnSEventResult<Object>(null);
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
	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}
}
