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

package org.cytoscape.clustnsee3.internal.analysis;

import java.util.Iterator;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;

/**
 * 
 */
public class CnSPartitionHandler implements CnSEventListener {
	/**
	 * @param
	 * @return
	 */
	
	private static CnSPartitionHandler instance;
	private Vector<CnSPartition> partitions;
	
	private CnSPartitionHandler() {
		super();
		partitions = new Vector<CnSPartition>();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public static CnSPartitionHandler getInstance() {
		if (instance == null)
			instance = new CnSPartitionHandler();
		return instance;
	}
	public void addPartition(CnSPartition partition) {
		if (partition != null) partitions.addElement(partition);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<CnSPartition> getPartitionIterator() {
		return partitions.iterator();
	}
}


