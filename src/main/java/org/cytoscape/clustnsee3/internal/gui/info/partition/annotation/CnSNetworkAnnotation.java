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

import org.cytoscape.clustnsee3.internal.gui.info.partition.annotation.decorator.CnSNetworkAnnotationDecorator;
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
	private CnSNetworkAnnotationDecorator deco;
	
	public CnSNetworkAnnotation(CyNetwork network, String name, Class<T> cl) {
		super();
		this.network = network;
		this.name = name;
		type = cl;
		data = new HashMap<CyNode, T>();
	}
	
	public void setDecorator(CnSNetworkAnnotationDecorator deco) {
		this.deco = deco;
	}
	
	public CnSNetworkAnnotation(CyNetwork network, String name, String list, Class<T> cl) {
		this(network, name, cl);
		this.list = new Vector<String>();
		for (String s : list.split(",")) this.list.addElement(s);
	}
	
	public void addData(CyNode node, String data) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> types[] = {String.class};
		Constructor<T> constructor = type.getDeclaredConstructor(types);
		constructor.setAccessible(true);
		T newInstance = constructor.newInstance(data);
		this.data.put(node, newInstance);
	}
	
	public Object getMeanValue(Vector<CyNode> nodes) {
		return deco.getMeanValue(data, nodes);
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

	public T getValue(CyNode key) {
		return data.get(key);
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public CyNetwork getNetwork() {
		return network;
	}
}
