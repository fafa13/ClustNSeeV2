/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 11 mars 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.widget.paneltree;

import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;

/**
 * 
 */
public abstract class CnSPanelTreeNode implements ActionListener {
	private Hashtable<Integer, Object> data;
	private Vector<CnSPanelTreeNode> children;
	private CnSPanelTreeNode parent;
	
	protected CnSPanelTreePanel panel;
	private boolean editable;
	
	public CnSPanelTreeNode() {
		super();
		panel = new CnSPanelTreePanel();
		editable = false;
	}
	public CnSPanelTreeNode(CnSPanelTreeNode parent) {
		this();
		this.parent = parent;
	}
	public CnSPanelTreeNode(Hashtable<Integer, Object> v) {
		this();
		data = v;
	}
	public Object getData(int key) {
		return data.get(key);
	}
	public CnSPanelTreePanel getPanel() {
		return panel;
	}
	public CnSButton getButton() {
		return null;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean ed) {
		editable = ed;
	}
	public void addActionListener(ActionListener actionListener) {
		
	}
	public CnSPanelTreeNode getChildAt(int index) {
		return children.elementAt(index);
	}
	public int getChildCount() {
		return children.size();
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public int getIndexOfChild(CnSPanelTreeNode child) {
		return children.indexOf(child);
	}
}
