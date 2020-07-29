/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 29 mai 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.partition;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmParameters;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.analysis.CnSClusterLink;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdgeS;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNode;
import org.cytoscape.clustnsee3.internal.analysis.node.CnSNodeS;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSPartition {
	private Vector<CnSCluster> clusters;
	private CnSNodeS nodes;
	private CnSNodeS clusterNodes;
	private CnSEdgeS edges;
	private CnSEdgeS clusterEdges;
	private String algorithmName;
	private CnSAlgorithmParameters algorithmParameters;
	private Vector<CnSClusterLink> clusterLinks;
	private String name, scope;
	private CyNetwork inputNetwork;
	
	public CnSPartition(String algorithmName, CnSAlgorithmParameters algorithmParameters, CyNetwork inputNetwork, String scope) {
		super();
		clusters = new Vector<CnSCluster>();
		clusterLinks = new Vector<CnSClusterLink>();
		nodes = new CnSNodeS();
		clusterNodes = new CnSNodeS();
		edges = new CnSEdgeS();
		clusterEdges = new CnSEdgeS();
		this.algorithmName = algorithmName;
		this.name = inputNetwork.getRow(inputNetwork).get(CyNetwork.NAME, String.class);
		this.algorithmParameters = algorithmParameters;
		this.inputNetwork = inputNetwork;
		this.scope = scope;
	}
	public CyNetwork getInputNetwork() {
		return inputNetwork;
	}
	public String getAlgorithmName() {
		return algorithmName;
	}
	public String getName() {
		return name;
	}
	public CnSAlgorithmParameters getAlgorithmParameters() {
		return algorithmParameters;
	}
	public void addCluster(CnSCluster cluster) {
		if (cluster != null) {
			clusters.addElement(cluster);
			makeClusterLinks(cluster);
		}
	}
	public Iterator<CnSCluster> getClusterIterator() {
		return clusters.iterator();
	}
	public boolean containsNode(CyNode node) {
		return nodes.contains(node);
	}
	public boolean containsClusterNode(CyNode node) {
		return clusterNodes.contains(node);
	}
	public boolean containsEdge(CyEdge edge) {
		return edges.contains(edge);
	}
	public boolean containsClusterEdge(CyEdge edge) {
		return clusterEdges.contains(edge);
	}
	public boolean containsNode(CnSNode node) {
		return nodes.contains(node);
	}
	public boolean containsClusterNode(CnSNode node) {
		return clusterNodes.contains(node);
	}
	public boolean containsEdge(CnSEdge edge) {
		return edges.contains(edge);
	}
	public boolean containsClusterEdge(CnSEdge edge) {
		return clusterEdges.contains(edge);
	}
	public boolean containsCluster(CnSCluster c) {
		return clusters.contains(c);
	}
	public CnSNode addNode(CyNode node, CnSCluster cluster) {
		CnSNode cnsNode = null;
		if (nodes.contains(node)) {
			cnsNode = nodes.get(node);
			cnsNode.addCluster(cluster);
		}
		else {
			cnsNode = new CnSNode(node, cluster);
			nodes.addNode(cnsNode);
		}
		return cnsNode;
	}
	public CnSNode addClusterNode(CyNode node) {
		CnSNode cnsNode = null;
		if (clusterNodes.contains(node)) {
			cnsNode = clusterNodes.get(node);
		}
		else {
			cnsNode = new CnSNode(node, null);
			clusterNodes.addNode(cnsNode);
		}
		return cnsNode;
	}
	public void removeNode(CyNode node, CnSCluster cluster) {
		CnSNode cnsNode = null;
		if (nodes.contains(node)) {
			cnsNode = nodes.get(node);
			cnsNode.removeCluster(cluster);
			if (cnsNode.getNbClusters() == 0) nodes.removeNode(cnsNode);
		}
	}
	public void removeClusterNode(CyNode node) {
		CnSNode cnsNode = null;
		if (clusterNodes.contains(node)) {
			cnsNode = clusterNodes.get(node);
			clusterNodes.removeNode(cnsNode);
		}
	}
	public CnSEdge addEdge(CyEdge edge) {
		CnSEdge cnsEdge = null;
		if (edges.contains(edge)) {
			cnsEdge = edges.get(edge);
		}
		else {
			cnsEdge = new CnSEdge();
			cnsEdge.setCyEdge(edge);
			edges.addEdge(cnsEdge);
		}
		return cnsEdge;
	}
	public CnSEdge addClusterEdge(CyEdge edge) {
		CnSEdge cnsEdge = null;
		if (clusterEdges.contains(edge)) {
			cnsEdge = clusterEdges.get(edge);
		}
		else {
			cnsEdge = new CnSEdge();
			cnsEdge.setCyEdge(edge);
			clusterEdges.addEdge(cnsEdge);
		}
		return cnsEdge;
	}
	public void sortClusters() {
		Collections.sort(clusters);
	}
	public Vector<CnSCluster> getClusters() {
		return clusters;
	}
	
	@SuppressWarnings("unchecked")
	public void makeClusterLinks(CnSCluster cluster) {
		Vector<CnSEdge> commonEdges = null;
		Vector<CnSNode> commonNodes = null;
		Vector<CyNode> commonCyNodes = null;
		
		for (CnSCluster cl : clusters)
			if (cl != cluster) {
				CnSClusterLink clusterLink = new CnSClusterLink(cluster, cl);
				if (!clusterLinks.contains(clusterLink)) {
					commonNodes = (Vector<CnSNode>)cluster.getNodes().clone();
					commonNodes.retainAll(cl.getNodes());
					commonCyNodes = new Vector<CyNode>();
					if (commonNodes.size() > 0) {
						for (CnSNode n : commonNodes) {
							clusterLink.addNode(n);
							commonCyNodes.addElement(n.getCyNode());
						}
						clusterLink.setMulticlassEdge(null);
						clusterLink.getMulticlassEdge().setAttribute("CnS:isInteraction", false, Boolean.class);
						clusterLink.getMulticlassEdge().setAttribute("CnS:size", clusterLink.getNodes().size(), Integer.class);
					}
					commonEdges = (Vector<CnSEdge>)cluster.getExtEdges().clone();
					commonEdges.retainAll(cl.getExtEdges());
					if (commonEdges.size() > 0) {
						for (CnSEdge e : commonEdges)
							if (!commonCyNodes.contains(e.getCyEdge().getSource()) && !commonCyNodes.contains(e.getCyEdge().getTarget()))
								clusterLink.addEdge(e);
						clusterLink.setInteractionEdge(null);
						clusterLink.getInteractionEdge().setAttribute("CnS:isInteraction", true, Boolean.class);
						clusterLink.getInteractionEdge().setAttribute("CnS:size", clusterLink.getEdges().size(), Integer.class);
					}
					if ((clusterLink.getEdges().size() > 0) || (clusterLink.getNodes().size() > 0)) {
						clusterLinks.addElement(clusterLink);
					}
				}
			}
	}
	
	public Vector<CnSClusterLink> getClusterLinks() {
		return clusterLinks;	
	}
	
	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSNode getNode(Long suid) {
		CnSNode ret = null;
		ret = nodes.getNode(suid);
		return ret;
	}
	public CnSNode getNode(CyNode cyNode) {
		CnSNode ret = null;
		ret = nodes.getNode(cyNode);
		return ret;
	}
	public CnSNode getClusterNode(Long suid) {
		CnSNode ret = null;
		ret = clusterNodes.getNode(suid);
		return ret;
	}
	public CnSNode getClusterNode(CyNode cyNode) {
		CnSNode ret = null;
		ret = clusterNodes.getNode(cyNode);
		return ret;
	}
	public String getScope() {
		return scope;
	}
	public CnSCluster getCluster(CyNode cyNode) {
		CnSCluster ret = null;
		for (CnSCluster cl : clusters)
			if (cl.getCyNode() == cyNode) {
				ret = cl;
				break;
			}
		return ret;
	}
}
