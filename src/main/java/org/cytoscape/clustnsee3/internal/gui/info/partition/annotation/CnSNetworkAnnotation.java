/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 18 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * 
 */
public class CnSNetworkAnnotation<T> {
	private HashMap<CyNode, T> data;
	private CyNetwork network;
	private String name;
	private Vector<String> list;
	private Class<T> type;
	
	public CnSNetworkAnnotation(CyNetwork network, String name, Class<T> cl) {
		super();
		this.network = network;
		this.name = name;
		type = cl;
	}
	public CnSNetworkAnnotation(CyNetwork network, String name, String list, Class<T> cl) {
		this(network, name, cl);
		this.list = new Vector<String>();
		for (String s : list.split(",")) this.list.addElement(s);
	}
	
	public void addData(CyNode node, String data) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Constructor<T> constructor = type.getDeclaredConstructor(type);
		constructor.setAccessible(true);
		constructor.newInstance(type);
	}
	
	public void setData(HashMap<CyNode, T> data) {
		this.data = data;
	}
	
	public HashMap<CyNode, T> getData() {
		return data;
	}
	
	public String getName() {
		return name;
	}

	public T getValue(String key) {
		return data.get(key);
	}
	
	public CyNetwork getNetwork() {
		return network;
	}
}
