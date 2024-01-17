/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 27 sept. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.controlpanel.networkfiletree.nodes.netname;

import java.util.function.Predicate;

import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.nodeannotation.stats.CnSAnnotationClusterPValue;

/**
 * 
 */
public class CnSMajorityPredicate implements Predicate<CnSAnnotationClusterPValue> {
	private double threshold;
	
	public CnSMajorityPredicate(double threshold) {
		super();
		this.threshold = threshold;
	}
	
	/* (non-Javadoc)
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	public boolean test(CnSAnnotationClusterPValue pv) {
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATED_NODES, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
		ev.addParameter(CnSNodeAnnotationManager.CLUSTER, pv.getCluster());
		int node_count = (Integer)CnSEventManager.handleMessage(ev, true);
		return (((double)(pv.getAnnotatedNodesInCluster()) / (double)node_count) < threshold);
	}
}
