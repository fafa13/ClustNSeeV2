/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 17 nov. 2020
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.info.partition;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 */
public class CnSPartitionTable implements ChangeListener, PropertyChangeListener {
	private JTable table;
	private JTable fixed;
	private JScrollPane scrollPane;
	
	public CnSPartitionTable() {
		super();
		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(20);
		scrollPane = new JScrollPane(table);
		table.addPropertyChangeListener(this);
		fixed = new JTable();
		fixed.setAutoCreateColumnsFromModel(false);
		fixed.setSelectionModel(table.getSelectionModel());
		fixed.setFocusable(false);
		fixed.setRowHeight(20);
	}
	
	public JTable getTable() {
		return table;
	}
	
	public JTable getFixedTable() {
		return fixed;
	}
	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public void setModel(CnSPartitionTableModel model) {
		table.setModel(model); 
		fixed.setModel(model);
	}
	
	public CnSPartitionTableModel getModel() {
		return (CnSPartitionTableModel)table.getModel();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("selectionModel"))
			fixed.setSelectionModel(table.getSelectionModel());
		else if (e.getPropertyName().equals("model"))
			fixed.setModel(table.getModel());
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		JViewport viewport = (JViewport)e.getSource();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}
}
