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

package org.cytoscape.clustnsee3.internal.gui.annotationpanel.partitiontable;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.cytoscape.clustnsee3.internal.analysis.CnSCluster;
import org.cytoscape.clustnsee3.internal.event.CnSEvent;
import org.cytoscape.clustnsee3.internal.event.CnSEventManager;
import org.cytoscape.clustnsee3.internal.gui.annotationpanel.CnSAnnotationPanel;
import org.cytoscape.clustnsee3.internal.gui.annotationpanel.partitiontable.search.CnSSearchAnnotationComponent;
import org.cytoscape.clustnsee3.internal.gui.resultspanel.CnSResultsPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSButton;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSPanel;
import org.cytoscape.clustnsee3.internal.gui.widget.CnSTableHeaderRenderer;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotation;
import org.cytoscape.clustnsee3.internal.nodeannotation.CnSNodeAnnotationManager;
import org.cytoscape.clustnsee3.internal.partition.CnSPartition;

/**
 * 
 */
public class CnSPartitionTablePanel extends CnSPanel {
	private static final long serialVersionUID = 4538284737604540732L;
	
	private CnSSearchAnnotationComponent annotationSearchComponent;
	private CnSPartitionTable table;
	private CnSButton exportDataButton;
	
	public CnSPartitionTablePanel() {
		super();
		initGraphics();
		initListeners();
	}
	
	public void initGraphics() {
		annotationSearchComponent = new CnSSearchAnnotationComponent();
		addComponent(new JLabel("Search annotation :"), 0, 0, 1, 1, 0.0, 0.0, WEST, NONE, 0, 5, 0, 5, 0, 0);
		addComponent(annotationSearchComponent.getTextField(), 1, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, 0, 5, 0, 5, 0, 0);
		table = new CnSPartitionTable();
		addComponent(table.getScrollPane(), 0, 1, 2, 1, 1.0, 1.0, CnSPanel.CENTER, CnSPanel.BOTH, 5, 5, 0, 5, 0, 0);
		table.getTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		table.getFixedTable().getTableHeader().setDefaultRenderer(new CnSTableHeaderRenderer());
		CnSPanel commandPanel = new CnSPanel();
		commandPanel.setBorder(BorderFactory.createEtchedBorder());
		exportDataButton = new CnSButton("Export data");
		commandPanel.addComponent(exportDataButton, 0, 0, 1, 1, 0.0, 1.0, CnSPanel.CENTER, CnSPanel.NONE, 5, 5, 5, 5, 0, 0);
		addComponent(commandPanel, 0, 2, 2, 1, 0.0, 0.0, CnSPanel.SOUTH, CnSPanel.HORIZONTAL, 5, 5, 5, 5, 0, 0);
	}
	
	private void initListeners() {
		exportDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.LOOK_FOR_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.PREFIX, "GO:0012");
				Vector<?> ret = (Vector<?>)CnSEventManager.handleMessage(ev);
				for (Object s : ret) System.err.println(s.toString());
			}
		});
		
		table.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	CnSEvent ev = new CnSEvent(CnSResultsPanel.SELECT_CLUSTER, CnSEventManager.RESULTS_PANEL);
		    	CnSCluster cluster = null;
		    	int selectedRow = -1;
		    	
		    	if (table.getTable().isRowSelected(e.getLastIndex()))
		    		selectedRow = e.getLastIndex();
		    	else if (table.getTable().isRowSelected(e.getFirstIndex()))
		    		selectedRow = e.getFirstIndex();
		    	
		    	if (selectedRow != -1) {
		    		int modelRow = table.getTable().convertRowIndexToModel(selectedRow);
		                        
		    		cluster = ((CnSPartitionTableModel)table.getTable().getModel()).getCluster(modelRow);
		    	}
		    	            
		    	if (cluster != null) ev.addParameter(CnSResultsPanel.CLUSTER_NAME, Integer.parseInt(cluster.getName()));
		    	CnSEventManager.handleMessage(ev);
		    	if (cluster != null) {
		    		ev = new CnSEvent(CnSAnnotationPanel.INIT_ANNOTATION_PANEL, CnSEventManager.ANNOTATION_PANEL);
		    		ev.addParameter(CnSAnnotationPanel.CLUSTER, cluster);
		    		CnSEventManager.handleMessage(ev);
		    	}
		    }
		});
	}
	
	public void init(CnSPartition partition) {
		CnSPartitionTableModel model = new CnSPartitionTableModel(partition);
		table.setModel(model);
		RowFilter<CnSPartitionTableModel,Integer> annotationFilter = new RowFilter<CnSPartitionTableModel,Integer>() {
			@Override
			public boolean include(Entry<? extends CnSPartitionTableModel, ? extends Integer> entry) {
				CnSPartitionTableModel model = entry.getModel();
				if (model.getSelectedAnnotation() == null) return true;
				CnSCluster cluster = model.getCluster(entry.getIdentifier());
				CnSEvent ev = new CnSEvent(CnSNodeAnnotationManager.GET_CLUSTER_ANNOTATIONS, CnSEventManager.ANNOTATION_MANAGER);
				ev.addParameter(CnSNodeAnnotationManager.CLUSTER, cluster);
				Vector<?> clusterAnnotations = (Vector<?>)CnSEventManager.handleMessage(ev);
				return clusterAnnotations.contains(model.getSelectedAnnotation());
			}
		};
		TableRowSorter<CnSPartitionTableModel> sorter = new TableRowSorter<CnSPartitionTableModel>(model);
		sorter.setRowFilter(annotationFilter);
		sorter.setMaxSortKeys(1);
		table.getTable().setRowSorter(sorter);
		table.getFixedTable().setRowSorter(sorter);
		setColumnsWidth();
		table.getFixedTable().setPreferredScrollableViewportSize(table.getFixedTable().getPreferredSize());
	}
	
	private void setColumnsWidth() {
		int pWidth, maxWidth = 500, minWidth = 20;
		TableColumn tc;
		
		FontMetrics headerFontMetrics = table.getTable().getTableHeader().getFontMetrics(((CnSTableHeaderRenderer)table.getTable().getTableHeader().getDefaultRenderer()).getFont());
		 
		for (int col = 0; col < table.getModel().getColumnCount() - 1; col++) {
			pWidth = headerFontMetrics.stringWidth(table.getModel().getColumnName(col)) + table.getTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			pWidth = Math.max(pWidth,  minWidth);
			tc = table.getTable().getColumnModel().getColumn(col);
			tc.setMinWidth(minWidth);
			tc.setMaxWidth(maxWidth);
			for (int row = 0; row < table.getModel().getRowCount(); row++)
				pWidth = Math.max(pWidth, table.getTable().prepareRenderer(table.getTable().getDefaultRenderer(table.getModel().getColumnClass(col)), row, col).getPreferredSize().width + 10 + table.getTable().getIntercellSpacing().width);
	        pWidth = Math.min(pWidth, maxWidth);
			tc.setPreferredWidth(pWidth);
		}
		
		pWidth = headerFontMetrics.stringWidth(table.getFixedTable().getColumnName(0)) + table.getFixedTable().getIntercellSpacing().width + 10 + UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
		pWidth = Math.max(pWidth,  minWidth);
		tc = table.getFixedTable().getColumnModel().getColumn(0);
		tc.setMinWidth(minWidth);
		tc.setMaxWidth(maxWidth);
		for (int row = 0; row < table.getFixedTable().getModel().getRowCount(); row++)
			pWidth = Math.max(pWidth, table.getFixedTable().prepareRenderer(table.getFixedTable().getDefaultRenderer(table.getFixedTable().getColumnClass(0)), row, 0).getPreferredSize().width + 10 + table.getFixedTable().getIntercellSpacing().width);
        pWidth = Math.min(pWidth, maxWidth);
		tc.setPreferredWidth(pWidth);
	}
	
	public void selectCluster(CnSCluster cluster) {
		if (cluster == null) {
			table.getTable().getSelectionModel().clearSelection();
		}
		else {
			int index = 0;
			for (index = 0; index < table.getModel().getRowCount(); index++)
				if (Integer.valueOf(cluster.getID()).toString().equals(table.getModel().getValueAt(index, 0).toString())) {
					break;
				}
			if (index < table.getModel().getRowCount()) {
				int viewRow = table.getTable().convertRowIndexToView(index);
				if (viewRow != -1) {
					table.getTable().getSelectionModel().setSelectionInterval(viewRow, viewRow);
					table.getTable().scrollRectToVisible(table.getTable().getCellRect(viewRow, 0, true));
				}
				else {
					table.getTable().getSelectionModel().clearSelection();
					table.getTable().scrollRectToVisible(table.getTable().getCellRect(0, 0, true));
				}
				repaintTable();
			}
		}
	}
	
	public void setSelectedAnnotation(CnSNodeAnnotation annotation) {
		table.getModel().setSelectedAnnotation(annotation);
		table.getModel().fireTableDataChanged();
		repaintTable();
	}
	
	public void clear() {
		table.getTable().setModel(new DefaultTableModel());
		table.clear();
		table.getTable().doLayout();
		table.getTable().repaint();
	}
	public void repaintTable() {
		table.getTable().repaint();
		table.getFixedTable().repaint();
	}
}
