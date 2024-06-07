/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 4 oct. 2023
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.dialog.loadannotationfile;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSLoadAnnotationFileTableHeaderRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -5481419485529001820L;
	//private DefaultTableCellRenderer renderer;
	private CnSLoadAnnotationFileDialog dialog;

	public CnSLoadAnnotationFileTableHeaderRenderer(CnSLoadAnnotationFileDialog cnSLoadAnnotationFileDialog, JTable table) {
		super();
		//renderer = (DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer();
		dialog = cnSLoadAnnotationFileDialog;
	}
	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lab = new JLabel(value.toString());
		//Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if ((column == dialog.getNodeColSpinnerValue()) || (column == dialog.getAnnColSpinnerValue())) {
        	lab.setForeground(Color.BLUE);
        	lab.setFont(lab.getFont().deriveFont(Font.BOLD));
        }
        lab.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getPreferredWidth(), 16));
        return lab;
    }
}
