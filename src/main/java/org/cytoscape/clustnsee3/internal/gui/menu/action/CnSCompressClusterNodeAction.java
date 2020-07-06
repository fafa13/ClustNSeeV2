/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 24 juin 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.menu.action;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.network.CnSNetwork;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.clustnsee3.internal.view.CnSView;
import org.cytoscape.clustnsee3.internal.view.CnSViewManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;

/**
 * 
 */
public class CnSCompressClusterNodeAction {
	public static final String ACTION = "Compress cluster";
	
	public void doAction(Long suid) {
		CnSEvent ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
		CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
		ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, view);
		CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
		ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.NETWORK, network);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		
		ev = new CnSEvent(CnSPartitionManager.GET_CLUSTERS, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.PARTITION, partition);
		ev.addParameter(CnSPartitionManager.CY_NODE, network.getNetwork().getNode(suid));
		
		@SuppressWarnings("unchecked")
		Vector<CnSCluster> clusters = (Vector<CnSCluster>)CnSEventManager.handleMessage(ev);
		
		//String l = "Compressing clusters :";
		view.setModifCluster(true);
			
		for (CnSCluster c : clusters) {
			ev = new CnSEvent(CnSViewManager.GET_CLUSTER_LOCATION, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.VIEW, view);
			ev.addParameter(CnSViewManager.CLUSTER, c);
			Point2D.Double pos = (Point2D.Double)CnSEventManager.handleMessage(ev);
			Vector<CyNode> toRemove = new Vector<CyNode>();
			
			for (CnSNode cnsnode : c.getNodes()) {
				if (view.getView().getModel().containsNode(cnsnode.getCyNode())) {
					toRemove.addElement(cnsnode.getCyNode());
				}
			}
			network.getNetwork().removeNodes(toRemove);
			view.getView().updateView();
			
			network.getNetwork().addNode(c.getCyNode());
			view.getView().updateView();
			
			view.getView().getNodeView(c.getCyNode()).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION, pos.x);
			view.getView().getNodeView(c.getCyNode()).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION, pos.y);
			view.getView().getNodeView(c.getCyNode()).setVisualProperty(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ROUND_RECTANGLE);
			view.getView().getNodeView(c.getCyNode()).setVisualProperty(BasicVisualLexicon.NODE_FILL_COLOR, Color.PINK);
			
			CnSCluster partner;
			for (CnSClusterLink cl : partition.getClusterLinks()) {
				if (cl.getSource() == c)
					partner = cl.getTarget();
				else if (cl.getTarget() == c)
					partner = cl.getSource();
				else
					partner = null;
				if (partner != null) {
					ev = new CnSEvent(CnSViewManager.IS_EXPANDED, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.CLUSTER, partner);
					ev.addParameter(CnSViewManager.VIEW, view);
					Boolean b = (Boolean)CnSEventManager.handleMessage(ev);
					
					CyEdge ie = cl.getInteractionEdge();
					if (ie != null) network.getNetwork().addEdge(ie);
					CyEdge me = cl.getMulticlassEdge();
					if (me != null) network.getNetwork().addEdge(me);
					view.getView().updateView();
					
					if (ie != null) {
						view.getView().getEdgeView(ie).setVisualProperty(BasicVisualLexicon.EDGE_WIDTH, Double.valueOf(Math.min(cl.getEdges().size(), 16)));
						view.getView().getEdgeView(ie).setVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.blue);
					}
					if (me != null) {
						view.getView().getEdgeView(me).setVisualProperty(BasicVisualLexicon.EDGE_WIDTH, Double.valueOf(Math.min(cl.getNodes().size(), 16)));
						view.getView().getEdgeView(me).setVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.green);
					}
					view.getView().updateView();
					ev = new CnSEvent(CnSViewManager.SET_EXPANDED, CnSEventManager.VIEW_MANAGER);
					ev.addParameter(CnSViewManager.CLUSTER, c);
					ev.addParameter(CnSViewManager.VIEW, view);
					ev.addParameter(CnSViewManager.EXPANDED, false);
					CnSEventManager.handleMessage(ev);
				}
			}
			//l += "\n" + c.getName() + " : " + pos.x + " , " + pos.y;
		}
		view.setModifCluster(false);
		//JOptionPane.showMessageDialog(null, l);
	}

}
