/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 mai 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.analysis.node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNodeS implements Iterable<CnSNode> {
	private Vector<CnSNode> nodes;
	private HashMap<CnSNode, CyNode> cnsnode2cynodeMap;
	private HashMap<CyNode, CnSNode> cynode2cnsnodeMap;
	
	public CnSNodeS() {
		super();
		nodes = new Vector<CnSNode>();
		cnsnode2cynodeMap = new HashMap<CnSNode, CyNode>();
		cynode2cnsnodeMap = new HashMap<CyNode, CnSNode>();
	}
	public void addNode(CnSNode node) {
		nodes.addElement(node);
		cnsnode2cynodeMap.putIfAbsent(node, node.getCyNode());
		cynode2cnsnodeMap.putIfAbsent(node.getCyNode(), node);
	}
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<CnSNode> iterator() {
		return nodes.iterator();
	}
	public int size() {
		return nodes.size();
	}
	public CnSNode get(int index) {
		return nodes.elementAt(index);
	}
	public boolean contains(CnSNode node) {
		return nodes.contains(node);
	}
	public boolean contains(CyNode node) {
		boolean ret = false;
		Iterator<CnSNode> it = iterator();
		CyNode n;
		while (it.hasNext()) {
			n = it.next().getCyNode();
			ret = (node == n);
			if (ret) break;
		}
		return ret;
	}
	public CnSNode get(CyNode n) {
		CnSNode ret = null, testNode;
		Iterator<CnSNode> it = iterator();
		
		while (it.hasNext()) {
			testNode = it.next();
			if (testNode.getCyNode().getSUID() == n.getSUID()) {
				ret = testNode;
				break;
			}
		}
		return ret;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public void removeNode(CnSNode cnsNode) {
		nodes.remove(cnsNode);
		cynode2cnsnodeMap.remove(cnsnode2cynodeMap.get(cnsNode));
		cnsnode2cynodeMap.remove(cnsNode);
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSNode getNode(Long suid) {
		CnSNode node = null;
		
		for (CnSNode n : nodes) {
			if (n.getCyNode().getSUID() == suid) {
				node = n;
				break;
			}
		}
		return node;
	}
	public CnSNode getNode(CyNode cyNode) {
		return cynode2cnsnodeMap.get(cyNode);
	}
}
