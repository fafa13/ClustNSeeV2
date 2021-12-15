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

import javax.swing.tree.DefaultMutableTreeNode;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;

/**
 * 
 */
public abstract class CnSPanelTreeNode extends DefaultMutableTreeNode implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2180665833624336648L;
	private Hashtable<Integer, Object> data;
	protected CnSPanelTreePanel panel;
	private boolean editable;
	
	public CnSPanelTreeNode() {
		super();
		panel = new CnSPanelTreePanel();
		editable = false;
	}
	public CnSPanelTreeNode(CnSPanelTreeNode parent) {
		this();
		setParent(parent);
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
	
}
