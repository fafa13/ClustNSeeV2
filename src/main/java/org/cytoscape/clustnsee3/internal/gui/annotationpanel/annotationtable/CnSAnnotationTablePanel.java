/**
/* Copyright (C) 2018 TAGC, Luminy, Marseille
/*
/* @author Fabrice Lopez (TAGC/BCF, Luminy, Marseille)
/* @date 2 févr. 2021
/*
/* with contributions from:
/* Lionel Spinelli (CIML/TAGC, Luminy, Marseille)
/* Christine Brun, Charles Chapple, Benoit Robisson (TAGC, Luminy, Marseille)
/* Alain Guénoche, Anaïs Baudot, Laurent Tichit (IML, Luminy, Marseille)
/* Philippe Gambette (LIGM, Marne-la-Vallée)
 */

package org.cytoscape.clustnsee3.internal.gui.annotationpanel.annotationtable;

import java.awt.Color;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSAnnotationTablePanel extends CnSPanel {
	private static final long serialVersionUID = 9174731642049743947L;
	
	private JCheckBox viewAllCheckBox, clusterSelectionCheckBox;
	private CnSAnnotationTable annotationTable;
	private CnSButton exportDataButton;
	
	public CnSAnnotationTablePanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	public void initGraphics( ) {
		CnSPanel selectionPanel = new CnSPanel();
		viewAllCheckBox = new JCheckBox("View all", true);
		clusterSelectionCheckBox = new JCheckBox("Cluster selection");
		ButtonGroup bg = new ButtonGroup();
		bg.add(clusterSelectionCheckBox);
		bg.add(viewAllCheckBox);
		selectionPanel.addComponent(viewAllCheckBox, 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		selectionPanel.addComponent(clusterSelectionCheckBox, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 20, 5, 5, 0, 0);
		selectionPanel.setBorder(BorderFactory.createEtchedBorder());
		addComponent(selectionPanel, 0, 0, 1, 1, 1.0, 0.0, CENTER, NONE, 0, 5, 5, 5, 0, 0);
		
		annotationTable = new CnSAnnotationTable();
		annotationTable.setRowHeight(26);
		annotationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		annotationTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		annotationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annotationTable.getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
		addComponent(new JScrollPane(annotationTable), 0, 1, 1, 1, 1.0, 1.0, CENTER, BOTH, 0, 5, 0, 5, 0, 0);
		
		CnSPanel commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		exportDataButton = new CnSButton("Export data");
		commandPanel.addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		addComponent(commandPanel, 0, 2, 2, 1, 0.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
	}
	
	private void initListeners() {
		
	}
	
	public void init(CnSPartition partition) {
		System.err.println("partition = " + partition);
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		Vector<CnSNodeAnnotation> annotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
		System.err.println("nb annot : " + annotations.size());
		CnSAnnotationTableModel model = new CnSAnnotationTableModel(annotations, partition);
		annotationTable.setModel(model);
		
		RowFilter<CnSAnnotationTableModel,Integer> annotationFilter = new RowFilter<CnSAnnotationTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSAnnotationTableModel, ? extends Integer> entry) {
				if (viewAllCheckBox.isSelected()) 
					return true;
				else {
					CnSAnnotationTableModel model = entry.getModel();
					CnSCluster cluster = model.getSelectedCluster();
					if (cluster == null) return true;
					CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
					ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
					Vector<?> clusterAnnotations = (Vector<?>)CnSEventManager.handleMessage(ev);
					return clusterAnnotations.contains(model.getAnnotation(entry.getIdentifier()));
				}
			}
		};
		TableRowSorter<CnSAnnotationTableModel> sorter = new TableRowSorter<CnSAnnotationTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		annotationTable.setRowSorter(sorter);
	}
	
	public void init(CnSCluster cluster) {
		((CnSAnnotationTableModel)annotationTable.getModel()).setSelectedCluster(cluster);
		((CnSAnnotationTableModel)annotationTable.getModel()).fireTableDataChanged();
	}
	
	public void clear() {
		
	}
}
