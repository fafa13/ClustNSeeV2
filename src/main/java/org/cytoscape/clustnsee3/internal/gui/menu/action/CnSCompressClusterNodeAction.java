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

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
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
		
		// get the clusters in witch suid node is (the clusters to compress)
		CnSEvent ev = new CnSEvent(CnSViewManager.GET_SELECTED_VIEW, CnSEventManager.VIEW_MANAGER);
		CnSView view = (CnSView)CnSEventManager.handleMessage(ev);
		ev = new CnSEvent(CnSViewManager.GET_NETWORK, CnSEventManager.VIEW_MANAGER);
		ev.addParameter(CnSViewManager.VIEW, view);
		CnSNetwork network = (CnSNetwork)CnSEventManager.handleMessage(ev);
		ev = new CnSEvent(CnSPartitionManager.GET_PARTITION, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.NETWORK, network);
		CnSPartition partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		if (partition == null) {
			ev = new CnSEvent(CnSViewManager.GET_VIEW_PARTITION, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.VIEW, view);
			partition = (CnSPartition)CnSEventManager.handleMessage(ev);
		}
		ev = new CnSEvent(CnSPartitionManager.GET_CLUSTERS, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.PARTITION, partition);
		ev.addParameter(CnSPartitionManager.CY_NODE, network.getNetwork().getNode(suid));
		@SuppressWarnings("unchecked")
		Vector<CnSCluster> clusters = (Vector<CnSCluster>)CnSEventManager.handleMessage(ev);
		
		// stop listening to network changes
		view.setModifCluster(true);
		
		// some useful  variables
		Boolean b;
		Vector<CyNode> toRemove;
		
		for (CnSCluster c : clusters) { // for each cluster to compress
			// gget the location of the cluster (before it was expanded)
			ev = new CnSEvent(CnSViewManager.GET_CLUSTER_LOCATION, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.VIEW, view);
			ev.addParameter(CnSViewManager.CLUSTER, c);
			Point2D.Double pos = (Point2D.Double)CnSEventManager.handleMessage(ev);
			// initialize the list of nodes to remove after the cluster is compressed
			toRemove = new Vector<CyNode>();
			
			// find if the cluster is expanded
			b = false;
			ev = new CnSEvent(CnSViewManager.IS_EXPANDED, CnSEventManager.VIEW_MANAGER);
			ev.addParameter(CnSViewManager.VIEW, view);
			ev.addParameter(CnSViewManager.CLUSTER, c);
			b = (Boolean)CnSEventManager.handleMessage(ev);
			if (b) { // the cluster is expanded; it has to be compressed
				
				// make the list of nodes to remove = those that are in the cluster but not in another expanded cluster
				for (CnSNode cnsnode : c.getNodes()) { // for each node in the cluster
					if (view.getView().getModel().containsNode(cnsnode.getCyNode())) { // if the node is in the view
						b = false;
								
						// find if the node is in another expanded cluster
						for (CnSCluster cnsc : cnsnode.getClusters()) {
							if (cnsc != c) {
								ev.addParameter(CnSViewManager.CLUSTER, cnsc);
								b = (Boolean)CnSEventManager.handleMessage(ev);
								if (b) break;
							}
						}
						// the node is not in another expanded cluster so it has to be removed from the view
						if (!b) toRemove.addElement(cnsnode.getCyNode());
					}
				}
				
				// remove the requested nodes
				network.getNetwork().removeNodes(toRemove);
				view.getView().updateView();
			
				// put back the cluster node
				network.getNetwork().addNode(c.getCyNode());
				view.getView().updateView();
			
				// fill some cosmetic needs 
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
						b = false;
						ev = new CnSEvent(CnSViewManager.IS_EXPANDED, CnSEventManager.VIEW_MANAGER);
						ev.addParameter(CnSViewManager.CLUSTER, partner);
						ev.addParameter(CnSViewManager.VIEW, view);
						b = (Boolean)CnSEventManager.handleMessage(ev);
					
						if (!b.booleanValue()) {
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
						else {
							for (CnSEdge cnse : cl.getEdges()) {
								if (partner.contains(cnse.getCyEdge().getSource()))
									network.getNetwork().addEdge(c.getCyNode(), cnse.getCyEdge().getSource(), false);
								else
									network.getNetwork().addEdge(c.getCyNode(), cnse.getCyEdge().getTarget(), false);
							}
							for (CnSNode cnsn : cl.getNodes()) {
								if (!network.getNetwork().containsEdge(c.getCyNode(), cnsn.getCyNode()) && 
										!network.getNetwork().containsEdge(cnsn.getCyNode(), c.getCyNode())) {
									CyEdge ce = network.getNetwork().addEdge(c.getCyNode(), cnsn.getCyNode(), false);
									view.getView().updateView();
									view.getView().getEdgeView(ce).setVisualProperty(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT, Color.green);
								}
							}
							view.getView().updateView();
							ev = new CnSEvent(CnSViewManager.SET_EXPANDED, CnSEventManager.VIEW_MANAGER);
							ev.addParameter(CnSViewManager.CLUSTER, c);
							ev.addParameter(CnSViewManager.VIEW, view);
							ev.addParameter(CnSViewManager.EXPANDED, false);
							CnSEventManager.handleMessage(ev);
						}					
					}
				}
			}
			//l += "\n" + c.getName() + " : " + pos.x + " , " + pos.y;
		}
		view.setModifCluster(false);
		//JOptionPane.showMessageDialog(null, l);
	}

}
