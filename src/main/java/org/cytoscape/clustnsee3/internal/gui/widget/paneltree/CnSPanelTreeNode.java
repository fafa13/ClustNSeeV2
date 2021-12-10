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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;

/**
 * 
 */
public abstract class CnSPanelTreeNode extends DefaultMutableTreeNode implements ActionListener {
	private Hashtable<Integer, Object> data;
	protected Vector<CnSPanelTreeNode> children;
	protected CnSPanelTreeNode parent;
	protected CnSPanelTreePanel panel;
	private boolean editable;
	
	public CnSPanelTreeNode() {
		super();
		children = new Vector<CnSPanelTreeNode>();
		parent = null;
		panel = new CnSPanelTreePanel();
		editable = false;
	}
	public CnSPanelTreeNode(CnSPanelTreeNode parent) {
		this();
		this.parent = parent;
	}
	public CnSPanelTreeNode(CnSPanelTreeNode parent, Hashtable<Integer, Object> v) {
		this(parent);
		data = v;
	}
	public Enumeration<CnSPanelTreeNode> children() {
		return children.elements();
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
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getChildAt(children = new Vector<CnSAFTreeNode>();
		int)
	 */
	@Override
	public TreeNode getChildAt(int index) {
		return children.elementAt(index);
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		return children.size();
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	@Override
	public TreeNode getParent() {
		return parent;
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return children.size() == 0;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public boolean isRoot() {
		return parent == null;
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode, int)
	 */
	@Override
	public void insert(MutableTreeNode child, int index) {
		children.add(index, (CnSPanelTreeNode)child);
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#remove(int)
	 */
	@Override
	public void remove(int index) {
		children.remove(index);
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#remove(javax.swing.tree.MutableTreeNode)
	 */
	@Override
	public void remove(MutableTreeNode node) {
		children.removeElement(node);
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
	 */
	@Override
	public void setUserObject(Object object) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#removeFromParent()
	 */
	@Override
	public void removeFromParent() {
		parent.children.removeElement(this);
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#setParent(javax.swing.tree.MutableTreeNode)
	 */
	@Override
	public void setParent(MutableTreeNode newParent) {
		parent = (CnSPanelTreeNode)newParent;
	}
}
