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

package org.cytoscape.clustnsee3.internal.partition;

import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventListener;

/**
 * 
 */
public class CnSPartitionManager implements CnSEventListener {
	public static final int ADD_PARTITON = 1;
	public static final int GET_PARTITION = 2;
	
	public static final int PARTITION = 1000;
	public static final int PARTITION_INDEX = 1001;
	
	private static CnSPartitionManager instance;
	private Vector<CnSPartition> partitions;
	
	/**
	 * @param
	 * @return
	 */
	private CnSPartitionManager() {
		super();
		partitions = new Vector<CnSPartition>();
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.event.CnSEventListener#cnsEventOccured(org.cytoscape.clustnsee3.internal.event.CnSEvent)
	 */
	@Override
	public Object cnsEventOccured(CnSEvent event) {
		Object ret = null;
		
		switch (event.getAction()) {
			case ADD_PARTITON :
				CnSPartition p = (CnSPartition)event.getParameter(PARTITION);
				if (!partitions.contains(p)) partitions.addElement(p);
 				break;
				
			case GET_PARTITION :
				Integer index = (Integer)event.getParameter(PARTITION_INDEX);
				if (index != null)
					if (partitions.size() > index.intValue())
						ret = partitions.elementAt(index.intValue());
				break;
		}
		return ret;
	}

	/**
	 * 
	 * @param
	 * @return
	 */
	public static CnSPartitionManager getInstance() {
		if (instance == null)
			instance = new CnSPartitionManager();
		return instance;
	}
}


