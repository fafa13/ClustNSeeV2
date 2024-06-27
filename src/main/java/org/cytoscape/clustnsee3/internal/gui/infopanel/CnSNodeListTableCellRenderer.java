/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 9 févr. 2024
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.infopanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.cnstable.CnSTableCellRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;

/**
 * 
 */
public class CnSNodeListTableCellRenderer extends CnSTableCellRenderer {
	private static Border paddingBorder = BorderFactory.createEmptyBorder(0, 10, 0, 10);
	private CnSCluster cluster;
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Vector) {
			StringBuilder sb = new StringBuilder();
			for (CnSNodeAnnotation na : (Vector<CnSNodeAnnotation>)value) {
				sb.append(na.getValue());
				sb.append(";");
			}
			if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
			label.setText(sb.toString());
		}
		else if (value instanceof String) {
			JLabel lab = new JLabel(value.toString());
			lab.setBackground(Color.WHITE);
			if (cluster != null) {
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_NODE_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER, this.getClass());
				ev.addParameter(CnSNodeAnnotationManager.NODE, cluster.getNodes().get(row).getCyNode());
				Vector<CnSNodeAnnotation> clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev, false).getValue();
				if (clusterAnnotations == null) clusterAnnotations = new Vector<CnSNodeAnnotation>();
				CnSEvent ev2 = new CnSEvent(CnSPartitionPanel.GET_SELECTED_ANNOTATION, CnSEventManager.PARTITION_PANEL, this.getClass());
				CnSNodeAnnotation ann = (CnSNodeAnnotation)CnSEventManager.handleMessage(ev2, true).getValue();
				if (ann != null)
					if (clusterAnnotations.contains(ann))
						lab.setBackground(Color.GREEN);
			}
			lab.setOpaque(true);
			lab.setBorder(paddingBorder);
			if (isSelected) {
				lab.setFont(lab.getFont().deriveFont(Font.BOLD));
				if (lab.getBackground().equals(Color.WHITE)) lab.setBackground(Color.yellow);
			}
			lab.setHorizontalAlignment(SwingConstants.CENTER);
			return lab;
		}
		else
			label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		return label;
	}
	/**
	 * 
	 * @param
	 * @return
	 */
	public void setCluster(CnSCluster cluster) {
		this.cluster = cluster;
	}
	public CnSCluster getCluster() {
		return cluster;
	}
}
