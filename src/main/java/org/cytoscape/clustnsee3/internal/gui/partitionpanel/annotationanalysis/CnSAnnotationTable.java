/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 12 sept. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationanalysis;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTableFixedColumnRenderer;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTable;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSAnnotationTable implements ChangeListener, PropertyChangeListener {
	private CnSTable table;
	private CnSTable fixed;
	private JScrollPane scrollPane;
	
	public CnSAnnotationTable() {
		super();
		table = new CnSTable(new CnSAnnotationTableModel(null)) {
			private static final long serialVersionUID = 3844853711811737093L;

			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				int r = rowAtPoint(me.getPoint());
				return table.getValueAt(r, c).toString();
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setRowHeight(26);
		scrollPane = new JScrollPane(table);
		table.addPropertyChangeListener(this);
		fixed = new CnSTable();
		fixed.setAutoCreateColumnsFromModel(false);
		
		fixed.setSelectionModel(table.getSelectionModel());
		
		fixed.setFocusable(false);
		fixed.setRowHeight(26);
		fixed.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		fixed.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		fixed.getTableHeader().setResizingAllowed(false);
    	table.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    	scrollPane.setRowHeaderView(fixed);
    	scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());
    	scrollPane.getRowHeader().addChangeListener(this);
    	table.setDefaultRenderer(Object.class, new CnSAnnotationTableCellRenderer());
    	table.setDefaultRenderer(Integer.class, new CnSAnnotationTableCellRenderer());
    	table.setDefaultRenderer(Double.class, new CnSAnnotationTableCellRenderer());
    	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	fixed.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
	public void clear() {
		if (fixed.getColumnModel().getColumnCount() > 0) fixed.getColumnModel().removeColumn(fixed.getColumnModel().getColumn(0));
		
	}
	
	public TableModel getModel() {
		return table.getModel();
	}
	public void setModel(CnSAnnotationTableModel model) {
		table.setModel(model); 
		fixed.setModel(model);
		
		TableColumnModel columnModel = table.getColumnModel();
		if (model.getColumnCount() > 0) {
			TableColumn column = columnModel.getColumn(0);
			columnModel.removeColumn(column);
			if (fixed.getColumnModel().getColumnCount() > 0) fixed.getColumnModel().removeColumn(fixed.getColumnModel().getColumn(0));
			fixed.getColumnModel().addColumn(column);
			fixed.getColumnModel().getColumn(0).setCellRenderer(new CnSPartitionTableFixedColumnRenderer());
		}
    	table.fireTableDataChanged();
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
	public void fireTableDataChanged() {
		if (getModel() instanceof CnSAnnotationTableModel)
			((CnSAnnotationTableModel)getModel()).fireTableDataChanged();
	}

	public void setSelectedAnnotation(CnSNodeAnnotation annotation) {
		if (getModel() instanceof CnSAnnotationTableModel)
			((CnSAnnotationTableModel)getModel()).setSelectedAnnotation(annotation);
	}
	public CnSNodeAnnotation getSelectedAnnotation() {
		return ((CnSAnnotationTableModel)getModel()).getSelectedAnnotation();
	}
}
