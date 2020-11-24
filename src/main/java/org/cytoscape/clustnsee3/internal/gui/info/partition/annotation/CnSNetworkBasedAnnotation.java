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

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.clustnsee3.internal.partition.CnSPartitionManager;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNetworkBasedAnnotation<T> extends CnSPartitionAnnotation {
	private CnSNetworkAnnotation<?> networkAnnotation;
	
	/**
	 * @param
	 * @return
	 */
	public CnSNetworkBasedAnnotation(CnSPartition partition, CnSNetworkAnnotation<?> networkAnnotation) {
		super(partition, networkAnnotation.getName());
		this.setNetworkAnnotation(networkAnnotation);
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.CnSPartitionAnnotation#getValueAt(int)
	 */
	@Override
	public Object getValueAt(int index) {
		/*CnSEvent ev= new CnSEvent(CnSPartitionManager.GET_CLUSTER_NODES, CnSEventManager.PARTITION_MANAGER);
		ev.addParameter(CnSPartitionManager.CLUSTER, partition.getClusters().get(index));
		Vector<CyNode> nodes = (Vector<CyNode>)CnSEventManager.handleMessage(ev);
		for (CyNode node : nodes) {
			if (networkAnnotation.getType() == Integer.class)
				networkAnnotation.getValue(node). + networkAnnotation.getValue(node);
		}*/
		CyNode n = partition.getClusters().get(index).getNodes().firstElement().getCyNode();
		System.err.println(index + " : " + n);
		return networkAnnotation.getValue(n);
		//return "NA";
	}

	/**
	 * @return the networkAnnotation
	 */
	public CnSNetworkAnnotation<?> getNetworkAnnotation() {
		return networkAnnotation;
	}
	
	/*public T getMeanValue(Vector<CyNode> nodes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> types[] = {String.class};
		Constructor<T> constructor = networkAnnotation.getType().getClass().getDeclaredConstructor(types);
		constructor.setAccessible(true);
		T ret = constructor.newInstance();
		for (CyNode node : nodes) {
			if (type.getClass() == Integer) ret. += data.get(node).intValue();
			
		}
		return ret;
	}*/
	
	
	/**
	 * @param networkAnnotation the networkAnnotation to set
	 */
	public void setNetworkAnnotation(CnSNetworkAnnotation<?> networkAnnotation) {
		this.networkAnnotation = networkAnnotation;
	}

}
