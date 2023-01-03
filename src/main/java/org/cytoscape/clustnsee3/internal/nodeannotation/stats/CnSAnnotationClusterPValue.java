/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 13 juil. 2022
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.nodeannotation.stats;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
//import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationFile;

/**
 * 
 */
public class CnSAnnotationClusterPValue implements Comparable<CnSAnnotationClusterPValue> {
	private CnSNodeAnnotation annotation;
	//private CnSNodeAnnotationFile annotationFile;
	private CnSCluster cluster;
	private double pvalue, bhvalue;
	private int annotatedNodesInCluster, annotatedNodesInGraph;

	public CnSAnnotationClusterPValue(CnSNodeAnnotation annotation, CnSCluster cluster, int annotatedNodesInGraph, int annotatedNodesInCluster, double pvalue) {
		this.annotation = annotation;
		this.cluster = cluster;
		this.pvalue = pvalue;
		this.annotatedNodesInCluster = annotatedNodesInCluster;
		this.annotatedNodesInGraph = annotatedNodesInGraph;
		bhvalue = 1.0;
	}

	public double getPValue() {
		return pvalue;
	}

	public CnSNodeAnnotation getAnnotation() {
		return annotation;
	}

	public CnSCluster getCluster() {
		return cluster;
	}

	public int getAnnotatedNodesInCluster() {
		return annotatedNodesInCluster;
	}

	public int getAnnotatedNodesInGraph() {
		return annotatedNodesInGraph;
	}

	public void setBHValue(double bhvalue) {
		this.bhvalue = bhvalue;
	}

	public double getBHValue() {
		return bhvalue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CnSAnnotationClusterPValue arg0) {
		double diff = pvalue - arg0.getPValue();
		if (diff < 0) 
			return 1;
		else if (diff > 0)
			return -1;
		return 0;
	}
	public String toString() {
		return annotatedNodesInCluster + " (" + ((int)(annotatedNodesInCluster * 10000.0/ cluster.getNbNodes()))/100.0 + " %)";
	}
}
