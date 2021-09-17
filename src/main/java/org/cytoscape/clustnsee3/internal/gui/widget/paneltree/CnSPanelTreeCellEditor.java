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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

import org.cytoscape.clustnsee3.internal.gui.controlpanel.annotationfiletree.nodes.file.CnSAFTreeFileNode;

/**
 * 
 */
public class CnSPanelTreeCellEditor extends AbstractCellEditor implements TreeCellEditor, ActionListener {
	private static final long serialVersionUID = 2385213976677776638L;
	private CnSPanelTreeNode node;
	
	@Override
    public void actionPerformed(ActionEvent e){
        String val = node.getData(CnSAFTreeFileNode.ANNOTATION_FILE).toString();
        System.out.println("Pressed: " + val);
        stopCellEditing();
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row){
    	node = (CnSPanelTreeNode)value;
    	if (node.isEditable()) node.addActionListener(this);
		return node.getPanel();
    }

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	@Override
	public Object getCellEditorValue() {
		return null;
	}
}
