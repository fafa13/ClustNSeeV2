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

import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CnSPanelTreeModel implements TreeModel {
	protected CnSPanelTreeNode rootNode;
	private Vector<TreeModelListener> listeners;
	
	public CnSPanelTreeModel(CnSPanelTreeNode treeNode) {
		super();
		System.err.println("init tree model !!!");
		rootNode = treeNode;
		listeners = new Vector<TreeModelListener>();
	}
	
	public Object getRoot() {
		return rootNode;
	}

	public Object getChild(Object parent, int index) {
		return ((CnSPanelTreeNode)parent).getChildAt(index);
	}

	public int getChildCount(Object parent) {
		return ((CnSPanelTreeNode)parent).getChildCount();
	}

	public boolean isLeaf(Object node) {
		return ((CnSPanelTreeNode)node).getChildCount() == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		System.err.println("Value for path " + path + " has changed.");
	}
	
	public int getIndexOfChild(Object parent, Object child) {
		return ((CnSPanelTreeNode)parent).getIndex((CnSPanelTreeNode)child);
	}

	public void addTreeModelListener(TreeModelListener listener) {
		listeners.addElement(listener);
	}

	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.removeElement(listener);
	}

}
