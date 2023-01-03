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

package org.cytoscape.clustnsee3.internal.gui.partitionpanel.annotationtable;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.clustnsee3.internal.CyActivator;
import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.partitionpanel.CnSPartitionPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.util.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.util.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.gui.util.search.CnSSearchAnnotationComponent;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;
import org.cytoscape.model.CyNetwork;

/**
 * 
 */
public class CnSAnnotationTablePanel extends CnSPanel {
	private static final long serialVersionUID = 9174731642049743947L;
	
	private JCheckBox viewAllCheckBox, clusterSelectionCheckBox;
	private CnSAnnotationTable annotationTable;
	private CnSButton exportDataButton;
	private CnSSearchAnnotationComponent annotationSearchComponent;
	
	public CnSAnnotationTablePanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	public void initGraphics( ) {
		annotationSearchComponent = new CnSSearchAnnotationComponent(CnSPartitionPanel.SEARCH_ANNOTATION, CnSEventManager.PARTITION_PANEL, CnSPartitionPanel.ANNOTATION);
		addComponent(new JLabel("Focus on annotation :"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 5, 0, 0);
		addComponent(annotationSearchComponent.getTextField(), 1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 0, 5, 0, 5, 0, 0);
		
		CnSPanel selectionPanel = new CnSPanel();
		viewAllCheckBox = new JCheckBox("All annotations", true);
		clusterSelectionCheckBox = new JCheckBox("Selected cluster annotations");
		ButtonGroup bg = new ButtonGroup();
		bg.add(clusterSelectionCheckBox);
		bg.add(viewAllCheckBox);
		selectionPanel.addComponent(viewAllCheckBox, 1, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 5, 5, 0, 0, 0);
		selectionPanel.addComponent(clusterSelectionCheckBox, 2, 0, 1, 1, 0.0, 0.0, WEST, NONE, 5, 20, 5, 5, 0, 0);
		selectionPanel.setBorder(BorderFactory.createEtchedBorder());
		addComponent(selectionPanel, 2, 0, 1, 1, 1.0, 0.0, CENTER, NONE, 0, 5, 5, 5, 0, 0);
		
		annotationTable = new CnSAnnotationTable();
		annotationTable.setRowHeight(26);
		annotationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		annotationTable.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		annotationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		annotationTable.setTableHeader(new JTableHeader(annotationTable.getColumnModel()) {
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent me) {
				int c = columnAtPoint(me.getPoint());
				CnSEvent ev = new CnSEvent(CyActivator.GET_RESOURCES_BUNDLE, CnSEventManager.CY_ACTIVATOR);
				ResourceBundle rBundle = (ResourceBundle)CnSEventManager.handleMessage(ev);
				rBundle = CyActivator.getResourcesBundle();
				switch(c) {
					case 1 : return rBundle.getString("CnSAnnotationTableModel.nodes_MO");
					case 2 : return rBundle.getString("CnSAnnotationTableModel.frequency_MO");
					case 3 : return rBundle.getString("CnSAnnotationTableModel.annotationFrequency_MO");
					case 4 : return rBundle.getString("CnSAnnotationTableModel.clusters_MO");
					case 5 : return rBundle.getString("CnSAnnotationTableModel.clusterPercent_MO");
					case 6 : return rBundle.getString("CnSAnnotationTableModel.enrichedClustersPhyperInf5_MO");
					case 7 : return rBundle.getString("CnSAnnotationTableModel.enrichedClustersMajorityRule_MO");
				}
				return null;
			}
		});
		
		annotationTable.getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		
		addComponent(new JScrollPane(annotationTable), 0, 1, 3, 1, 1.0, 1.0, CENTER, BOTH, 0, 5, 0, 5, 0, 0);
		
		CnSPanel commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		exportDataButton = new CnSButton("Export data");
		commandPanel.addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		addComponent(commandPanel, 0, 2, 2, 1, 0.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
	}
	
	public CnSNodeAnnotation getSelectedAnnotation() {
		if (annotationTable.getSelectedRow() != -1)
			return (CnSNodeAnnotation)annotationTable.getValueAt(annotationTable.getSelectedRow(), 0);
		else
			return null;
	}
	
	private void initListeners() {
		annotationTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = annotationTable.getSelectedRow();
					CnSEvent ev = new CnSEvent(CnSPartitionPanel.SET_SEARCH_ANNOTATION, CnSEventManager.PARTITION_PANEL);
					ev.addParameter(CnSPartitionPanel.ANNOTATION, annotationTable.getValueAt(annotationTable.getSelectedRow(), 0));
					CnSEventManager.handleMessage(ev);
					
					annotationTable.getSelectionModel().setSelectionInterval(index, index);
				}
			}
		});
		//viewAllCheckBox.addActionListener((e) -> annotationTable.fireTableDataChanged());
			
		viewAllCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				annotationTable.fireTableDataChanged();
			}
		});
		clusterSelectionCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				annotationTable.fireTableDataChanged();
			}
		});
	}
	
	public void selectAnnotation(CnSNodeAnnotation ann) {
		int index = annotationTable.indexOf(ann);
		annotationTable.getSelectionModel().setSelectionInterval(index, index);
		annotationTable.scrollRectToVisible(annotationTable.getCellRect(index, 0, true));
	}
	
	public void init(CnSPartition partition) {
		//CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		//Vector<CnSNodeAnnotation> annotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
		CnSAnnotationTableModel model = new CnSAnnotationTableModel(partition);
		annotationTable.setModel(model);
		
		RowFilter<CnSAnnotationTableModel,Integer> annotationFilter = new RowFilter<CnSAnnotationTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSAnnotationTableModel, ? extends Integer> entry) {
				if (viewAllCheckBox.isSelected()) 
					return true;
				else {
					CnSAnnotationTableModel model = entry.getModel();
					CnSCluster cluster = model.getSelectedCluster();
					if (cluster == null) return false;
					CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
					ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
					Vector<CnSNodeAnnotation> clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
					TreeSet<CnSNodeAnnotation> ts = new TreeSet<CnSNodeAnnotation>(clusterAnnotations);
					return ts.contains(model.getAnnotation(entry.getIdentifier()));
				}
			}
		};
		TableRowSorter<CnSAnnotationTableModel> sorter = new TableRowSorter<CnSAnnotationTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		annotationTable.setRowSorter(sorter);
	}

	public void init(CnSCluster cluster) {
		if (cluster != ((CnSAnnotationTableModel)annotationTable.getModel()).getSelectedCluster()) {
			System.err.println("CnSAnnotationTablePanel.init(" + cluster.getName() + ")");
			((CnSAnnotationTableModel)annotationTable.getModel()).setSelectedCluster(cluster);
			CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
			ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
			Vector<CnSNodeAnnotation> clusterAnnotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
			final TreeSet<CnSNodeAnnotation> ts = new TreeSet<CnSNodeAnnotation>(clusterAnnotations);
			
			ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
			final Vector<?> annotations = (Vector<?>)CnSEventManager.handleMessage(ev);
			
			RowFilter<CnSAnnotationTableModel,Integer> annotationFilter = new RowFilter<CnSAnnotationTableModel,Integer>() {
				@Override
				public boolean include(Entry<? extends CnSAnnotationTableModel, ? extends Integer> entry) {
					if (viewAllCheckBox.isSelected()) 
						return true;
					else {
						CnSAnnotationTableModel model = entry.getModel();
						CnSCluster cluster = model.getSelectedCluster();
						if (cluster == null) return false;
						return ts.contains(annotations.elementAt(entry.getIdentifier()));
					}
				}
			};
			TableRowSorter<CnSAnnotationTableModel> sorter = new TableRowSorter<CnSAnnotationTableModel>((CnSAnnotationTableModel)(annotationTable.getModel()));
			sorter.setRowFilter(annotationFilter);
			sorter.setMaxSortKeys(1);
			annotationTable.setRowSorter(sorter);
		}
	}
	public void init() {
		CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
		//Vector<CnSNodeAnnotation> annotations = (Vector<CnSNodeAnnotation>)CnSEventManager.handleMessage(ev);
		//System.err.println("ANNOTATIONS SIZE = " + annotations.size());
		ev = new CnSEvent(CyActivator.GET_APPLICATION_MANAGER, CnSEventManager.CY_ACTIVATOR);
		CyApplicationManager cam = (CyApplicationManager)CnSEventManager.handleMessage(ev);
		CyNetwork network = cam.getCurrentNetwork();
		CnSAnnotationTableModel model = new CnSAnnotationTableModel(network);
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
	
	public void clear() {
		((CnSAnnotationTableModel)annotationTable.getModel()).setSelectedCluster(null);
		annotationTable.clear();
		annotationTable.fireTableDataChanged();
		//annotationTable.doLayout();
		//annotationTable.repaint();
	}
	
	public void refresh() {
		System.err.println("CnSAnnotationTablePanel.refresh");
		
		annotationTable.fireTableDataChanged();
		//annotationTable.doLayout();
		//annotationTable.repaint();
	}
}
