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

package org.cytoscape.clustnsee3.internal.analysis;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.cytoscape.clustnsee3.internal.algorithm.CnSAlgorithmParameters;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdge;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSEdgeS;
import org.cytoscape.clustnsee3.internal.analysis.edge.CnSNodeEdge;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSPartition {
	private Vector<CnSCluster> clusters;
	private CnSNodeS nodes;
	private CnSEdgeS edges;
	private String algorithmName, networkName;
	private CnSAlgorithmParameters algorithmParameters;
	private Vector<CnSClusterLink> clusterLinks;
	
	public CnSPartition(String networkName, String algorithmName, CnSAlgorithmParameters algorithmParameters) {
		super();
		clusters = new Vector<CnSCluster>();
		clusterLinks = new Vector<CnSClusterLink>();
		nodes = new CnSNodeS();
		edges = new CnSEdgeS();
		this.algorithmName = algorithmName;
		this.algorithmParameters = algorithmParameters;
		this.networkName = networkName;
	}
	public String getAlgorithmName() {
		return algorithmName;
	}
	public String getNetworkName() {
		return networkName;
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
	public boolean containsEdge(CyEdge edge) {
		return edges.contains(edge);
	}
	public boolean containsNode(CnSNode node) {
		return nodes.contains(node);
	}
	public boolean containsEdge(CnSEdge edge) {
		return edges.contains(edge);
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
	public void removeNode(CyNode node, CnSCluster cluster) {
		CnSNode cnsNode = null;
		if (nodes.contains(node)) {
			cnsNode = nodes.get(node);
			cnsNode.removeCluster(cluster);
			if (cnsNode.getNbClusters() == 0) nodes.remove(cnsNode);
		}
	}
	public CnSEdge addEdge(CyEdge edge, CnSCluster cluster) {
		CnSEdge cnsEdge = null;
		if (edges.contains(edge)) {
			cnsEdge = edges.get(edge);
			cnsEdge.addCluster(cluster);
		}
		else {
			cnsEdge = new CnSNodeEdge(edge, cluster);
			edges.addEdge(cnsEdge);
		}
		return cnsEdge;
	}
	
	public void sortClusters() {
		Collections.sort(clusters);
	}
	public Vector<CnSCluster> getClusters() {
		return clusters;
	}
	
	public void makeClusterLinks(CnSCluster cluster) {
		Vector<CnSEdge> commonEdges = null;
		for (CnSCluster cl : clusters) {
			if (cl != cluster) {
				commonEdges = (Vector<CnSEdge>)cluster.getExtEdges().clone();
				commonEdges.retainAll(cl.getExtEdges());
				if (commonEdges.size() > 0) {
					CnSClusterLink clusterLink = new CnSClusterLink(cluster, cl);
					for (CnSEdge e : commonEdges) clusterLink.addEdge(e);
					if (!clusterLinks.contains(clusterLink)) clusterLinks.addElement(clusterLink);
				}
			}
		}
	}
	
	public Vector<CnSClusterLink> getClusterLinks() {
		return clusterLinks;	
	}
}
