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

package org.cytoscape.clustnsee3.internal.analysis.edge;

import java.util.Iterator;
import java.util.Vector;

import org.cytoscape.model.CyEdge;

/**
 * 
 */
public class CnSEdgeS implements Iterable<CnSEdge> {
	private Vector<CnSEdge> edges;
	
	public CnSEdgeS() {
		super();
		edges = new Vector<CnSEdge>();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<CnSEdge> iterator() {
		return edges.iterator();
	}
	public void addEdge(CnSEdge edge) {
		edges.addElement(edge);
	}
	public int size() {
		return edges.size();
	}
	public CnSEdge get(int index) {
		return edges.elementAt(index);
	}
	public boolean contains(CnSEdge edge) {
		return edges.contains(edge);
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public boolean contains(CyEdge edge) {
		boolean ret = false;
		Iterator<CnSEdge> it = iterator();
		CyEdge e;
		while (it.hasNext()) {
			e = it.next().getCyEdge();
			ret = (edge.getSUID() == e.getSUID());
			if (ret) break;
		}
		return ret;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public CnSEdge get(CyEdge e) {
		CnSEdge ret = null, testEdge;
		Iterator<CnSEdge> it = iterator();
		
		while (it.hasNext()) {
			testEdge = it.next();
			if (testEdge.getCyEdge().getSUID() == e.getSUID()) {
				ret = testEdge;
				break;
			}
		}
		return ret;
	}

	public void remove(CnSEdge cnsEdge) {
		edges.remove(cnsEdge);
	}
}
