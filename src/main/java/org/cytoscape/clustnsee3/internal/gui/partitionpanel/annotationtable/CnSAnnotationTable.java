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

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationtable;

import javax.swing.JTable;

import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTableCellRenderer;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.partitiontable.CnSPartitionTableModel;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;

/**
 * 
 */
public class CnSAnnotationTable extends JTable {
	private static final long serialVersionUID = -5658527590778206930L;

	public CnSAnnotationTable() {
		super();
		setDefaultRenderer(Object.class, new CnSAnnotationTableCellRenderer());
    	
	}
	public void fireTableDataChanged() {
		if (getModel() instanceof CnSAnnotationTableModel) {
			((CnSAnnotationTableModel)getModel()).refreshModel();
			((CnSAnnotationTableModel)getModel()).fireTableDataChanged();
		}
	}
	
	public void clear() {
		
	}
	public int indexOf(CnSNodeAnnotation ann) {
		return ((CnSAnnotationTableModel)getModel()).getIndex(ann);
	}
}
