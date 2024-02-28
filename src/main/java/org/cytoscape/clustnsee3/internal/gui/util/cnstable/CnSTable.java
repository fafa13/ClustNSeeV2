/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 3 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.util.cnstable;

import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.cytoscape.clustnsee3.internal.gui.partitionpanel.clusteranalysis.CnSClusterTableModel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
/**
 * 
 */
public class CnSTable extends JTable {
	private static final long serialVersionUID = -5658527590778206930L;
	private CnSTableModel model;
	
	public CnSTable() {
		super();
		setDefaultRenderer(Object.class, new CnSTableCellRenderer());
		setDefaultRenderer(Double.class, new CnSTableCellRenderer());
		setDefaultRenderer(Integer.class, new CnSTableCellRenderer());
		model = new CnSDefaultTableModel();
	}
	public void setModel(CnSTableModel tm) {
		model = tm;
		super.setModel(model);
	}
	public CnSTable(Vector<? extends Vector<?>> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
		setDefaultRenderer(Object.class, new CnSTableCellRenderer());
		setDefaultRenderer(Double.class, new CnSTableCellRenderer());
		setDefaultRenderer(Integer.class, new CnSTableCellRenderer());
	}
	public void fireTableDataChanged() {
		/*if (getModel() instanceof CnSClusterTableModel) {
			((CnSClusterTableModel)getModel()).fireTableDataChanged();
		}
		else if (getModel() instanceof CnSTableModel) {
			((CnSTableModel)getModel()).fireTableDataChanged();
		}
		else*/
		if (model != null) model.fireTableDataChanged();
	}
	
	public String getToolTipText(MouseEvent me) {
		int c = columnAtPoint(me.getPoint());
		int r = rowAtPoint(me.getPoint());
		return getValueAt(r, c).toString();
	}
	
	public void clear() {
		
	}
	public TableModel getModel() {
		if (model == null) return super.getModel();
		return model;
	}
	public int indexOf(CnSNodeAnnotation ann) {
		return ((CnSClusterTableModel)getModel()).getIndex(ann);
	}
}
