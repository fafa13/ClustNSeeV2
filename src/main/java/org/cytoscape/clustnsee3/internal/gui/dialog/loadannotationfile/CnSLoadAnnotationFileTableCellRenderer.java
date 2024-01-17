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

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * 
 */
public class CnSLoadAnnotationFileTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 6794360191485492806L;
    //private DefaultTableCellRenderer renderer;
    private CnSLoadAnnotationFileDialog dialog;

    public CnSLoadAnnotationFileTableCellRenderer(CnSLoadAnnotationFileDialog cnSLoadAnnotationFileDialog, JTable table) {
    	super();
    	//renderer = (DefaultTableCellRenderer)table.getDefaultRenderer(String.class);
    	dialog = cnSLoadAnnotationFileDialog;
    }
    
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected)
        	if ((row + 1) < dialog.getFromLine()) 
        		setForeground(Color.LIGHT_GRAY);
        	else
        		setForeground(Color.BLACK);
        setText(getHTML(value.toString()));
        return this;
    }
    private String getHTML(String string) {
        int index = 0;
        char annSep = dialog.isAnnComRadioButtonSelected()?',':dialog.isAnnSemRadioButtonSelected()?';':dialog.isAnnSpaRadioButtonSelected()?' ':'\t';
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        while (index < string.length()) {
        	if (string.charAt(index) != annSep)
        		sb.append(string.charAt(index));
        	else {
        		sb.append("<span style=\"background: blue;\">");
        		sb.append(string.charAt(index));
        		sb.append("</span>");
        	}
        	index++;
        }
        sb.append("</html>");
        return sb.toString();
    }
}
