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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;

public abstract class CnSPanelTreeNode implements ActionListener {
	private Hashtable<Integer, Object> data;
	protected CnSPanelTreePanel panel;
	private boolean editable;
	private CnSPanelTreeNode parent;
	private Vector<CnSPanelTreeNode> children;
	
	private CnSPanelTreeNode(CnSPanelTreeNode parent) {
		panel = new CnSPanelTreePanel();
		editable = false;
		this.parent = parent;
		children = new Vector<CnSPanelTreeNode>();
		if (parent != null) parent.addChild(this);
	}
	public CnSPanelTreeNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		this(parent);
		data= new Hashtable<Integer, Object>(v);
		
	}
	public abstract Object getValue();
	
	public CnSPanelTreeNode getParent() {
		return parent;
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
	public int getChildCount() {
		return children.size();
	}
	public CnSPanelTreeNode getChildAt(int index) {
		return children.elementAt(index);
	}
	public int getIndex(CnSPanelTreeNode node) {
		return children.indexOf(node);
	}
	public void addChild(CnSPanelTreeNode child) {
		children.addElement(child);
	}
	public void removeChild(CnSPanelTreeNode child) {
		children.removeElement(child);
		child.removeFromParent();
	}
	public void removeFromParent() {
		parent = null;
	}
	public void remove() {
		if (parent != null) parent.removeChild(this);
		parent = null;
	}
	public Enumeration<CnSPanelTreeNode> children() {
		return children.elements();
	}
	public void actionPerformed(ActionEvent e) {
		
	}
}
