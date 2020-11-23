/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 23 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition.annotation;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;

/**
 * 
 */
public class CnSIntrinsicAnnotation extends CnSPartitionAnnotation {

	/**
	 * @param
	 * @return
	 */
	public CnSIntrinsicAnnotation(CnSPartition partition, String name) {
		super(partition, name);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.CnSPartitionAnnotation#getValueAt(int)
	 */
	@Override
	public Object getValueAt(int index) {
		CnSCluster cluster = partition.getClusters().elementAt(index - 1);
		
		if (name.equals("Nb. nodes") )
			return new Integer(cluster.getNbNodes());
		else if (name.equals("Intra cluster edges"))
			return new Integer(cluster.getEdges().size());
		else if (name.equals("Extra cluster edges"))
			return new Integer(cluster.getExtEdges().size());
		else if (name.equals("Intra/extra edges ratio"))
			return new Double((int)((double)cluster.getEdges().size() / (double)cluster.getExtEdges().size() * 1000.0D) / 1000.0D);
		else {
			CnSEvent ev = new CnSEvent(CnSPartitionManager.GET_NB_MULTICLASS_NODES, CnSEventManager.PARTITION_MANAGER);
			ev.addParameter(CnSPartitionManager.PARTITION, partition);
			ev.addParameter(CnSPartitionManager.CLUSTER, cluster);
			Integer nb = (Integer)CnSEventManager.handleMessage(ev);
			if (name.equals("Multi-clustered nodes"))
				return nb;
			else if (name.equals("Mono-clustered nodes"))
				return cluster.getNbNodes() - nb.intValue();
			else return "NA";
		}
	}

}
